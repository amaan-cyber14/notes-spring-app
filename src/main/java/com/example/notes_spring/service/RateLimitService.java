package com.example.notes_spring.service;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class RateLimitService {
    
    private final int MAX_REQUESTS_PER_MINUTE = 5;
    private final long TIME_WINDOW_MILLIS = 60000; // 1 minute


    private final Map<String, RequestBucket> buckets = new java.util.concurrent.ConcurrentHashMap<>();


    public boolean isRequestAllowed(String clientId) {
        long currentTime = System.currentTimeMillis();
    
        RequestBucket bucket = buckets.computeIfAbsent(clientId, k -> new RequestBucket(0, currentTime));


        if (currentTime - bucket.windowStart > TIME_WINDOW_MILLIS) {
            // Reset the bucket
            bucket.count = 1;
            bucket.windowStart = currentTime;
            return true;
        } else {
            if (bucket.count < MAX_REQUESTS_PER_MINUTE) {
                bucket.count++;
                return true;
            } else {
                return false; // Rate limit exceeded
            }
        }
    }




    class RequestBucket {
        int count;
        long windowStart;

        RequestBucket(int count, long windowStart) {
            this.count = count;
            this.windowStart = windowStart;
        }
    }


}
