package com.cloudproject.TeamC.global.cloudwatch;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class LogBufferService {

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public void add(String log) {
        queue.offer(log);
    }

    public List<String> drain(int max) {
        List<String> logs = new ArrayList<>();
        queue.drainTo(logs, max);
        return logs;
    }
}
