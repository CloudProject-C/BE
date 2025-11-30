package com.cloudproject.TeamC.CampEat.exception;

import com.cloudproject.TeamC.CampEat.exception.code.CampEatErrorCode;
import com.cloudproject.TeamC.global.exception.BusinessException;
import lombok.Getter;

@Getter
public class CampEatException extends BusinessException {
    public CampEatException(CampEatErrorCode errorCode) {
        super(errorCode);
    }
}
