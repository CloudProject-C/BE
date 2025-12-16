package com.cloudproject.TeamC.global.cloudwatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogCapture {

    private final LogBufferService buffer;

    public LogCapture(LogBufferService buffer) {
        this.buffer = buffer;
    }

    public void capture(String msg) {
        buffer.add(msg);
    }
}
