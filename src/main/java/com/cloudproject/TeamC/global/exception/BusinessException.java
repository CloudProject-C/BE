package com.cloudproject.TeamC.global.exception;

import com.cloudproject.TeamC.global.common.code.BaseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BaseCode errorCode;

    public BusinessException(BaseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}