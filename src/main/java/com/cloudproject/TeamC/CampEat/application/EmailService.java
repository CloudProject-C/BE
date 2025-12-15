package com.cloudproject.TeamC.CampEat.application;
import com.cloudproject.TeamC.CampEat.dto.request.EmailVerifyRequest;
import com.cloudproject.TeamC.global.common.code.ErrorCode;
import com.cloudproject.TeamC.global.exception.BusinessException;
import com.cloudproject.TeamC.global.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    @Value("${spring.mail.username}")
    private String id;

    @Value("${spring.mail.smtp.timeout}")
    private long codeExpTime;

    // 매 요청마다 새로운 6자리 코드 생성
    private String createKey() {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 6; i++) {
            key.append(rnd.nextInt(10));
        }
        return key.toString();
    }

    // 코드까지 파라미터로 받아서 메일 생성
    private MimeMessage createMessage(String to, String code)
            throws MessagingException, UnsupportedEncodingException {

        log.info("[AUTH] 보내는 대상: {}", to);
        log.info("[AUTH] 인증 번호: {}", code);

        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("Campeat 회원가입 인증코드");

        String msg = buildMailBody(code);

        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress(id, "Campeat"));

        return message;
    }

    private String buildMailBody(String code) {
        String msg = "";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 화면에서 입력해주세요.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += code;
        msg += "</td></tr></tbody></table></div>";
        return msg;
    }

    @Transactional
    public String sendMessage(String to) {
        String code = createKey(); // 요청마다 새 코드 생성

        try {
            MimeMessage message = createMessage(to, code);
            javaMailSender.send(message);
            log.info("[AUTH] 메일 발송 성공");
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            log.error("메일 발송 중 오류", e);
            throw new BusinessException(ErrorCode.UNABLE_TO_SEND_EMAIL);
        }

        // 이메일별로 다른 키에 코드 저장 + TTL
        redisUtil.save(AUTH_CODE_PREFIX + to, code, codeExpTime, TimeUnit.MILLISECONDS);

        return code;
    }

    public boolean verifyCode(EmailVerifyRequest requestDto) {
        String key = AUTH_CODE_PREFIX + requestDto.email();

        if (!redisUtil.hasKey(key)) {
            throw new BusinessException(ErrorCode.CODE_IS_NOT_VALID);
        }

        String storedCode = redisUtil.get(key);
        if (storedCode != null && storedCode.equals(requestDto.code())) {
            log.info("[AUTH] 코드 인증 성공");
            return true;
        }

        throw new BusinessException(ErrorCode.CODE_IS_NOT_VALID);
    }
}