package com.cloudproject.TeamC.global.cloudwatch;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

import java.util.List;

@Service
@EnableScheduling
public class CloudWatchLogsService {

    private final CloudWatchLogsClient client;
    private final LogBufferService buffer;

    private final String logGroup = "/myapp/spring";
    private final String logStream = "app";

    private String sequenceToken;

    public CloudWatchLogsService(
            CloudWatchLogsClient client,
            LogBufferService buffer
    ) {
        this.client = client;
        this.buffer = buffer;
        init();
    }

    private void init() {
        try {
            client.createLogGroup(
                    CreateLogGroupRequest.builder()
                            .logGroupName(logGroup)
                            .build()
            );
        } catch (ResourceAlreadyExistsException ignored) {}

        try {
            client.createLogStream(
                    CreateLogStreamRequest.builder()
                            .logGroupName(logGroup)
                            .logStreamName(logStream)
                            .build()
            );
        } catch (ResourceAlreadyExistsException ignored) {}
    }

    @Scheduled(fixedDelay = 5000)
    public void flush() {
        List<String> logs = buffer.drain(50);
        if (logs.isEmpty()) return;

        List<InputLogEvent> events = logs.stream()
                .map(msg -> InputLogEvent.builder()
                        .message(msg)
                        .timestamp(System.currentTimeMillis())
                        .build())
                .toList();

        try {
            PutLogEventsResponse response =
                    client.putLogEvents(
                            PutLogEventsRequest.builder()
                                    .logGroupName(logGroup)
                                    .logStreamName(logStream)
                                    .logEvents(events)
                                    .sequenceToken(sequenceToken)
                                    .build()
                    );

            sequenceToken = response.nextSequenceToken();
        } catch (Exception e) {
            // ❗ 로그 실패로 앱 죽으면 안 됨
        }
    }
}
