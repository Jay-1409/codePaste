package com.example.codePost.service.impl;

import com.example.codePost.service.UidGenerator;
import com.example.codePost.service.UidPool;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class UidPoolImpl implements UidPool {
    private static final long MAX_COUNTER = (1L << 62) - 1;
    private final AtomicLong counter = new AtomicLong(1);
    private final BlockingQueue<String> uidQueue = new ArrayBlockingQueue<>(1000);
    public UidPoolImpl(UidGenerator uidGenerator) {
        Thread producer = new Thread(() -> {
            try {
                while (true) {
                    long nextUid = counter.getAndIncrement();
                    if (nextUid > MAX_COUNTER) {
                        return;
                    }
                    uidQueue.put(uidGenerator.encode(nextUid).orElseThrow());
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            } catch (Exception exception) {
                throw new IllegalStateException("Unable to generate UID", exception);
            }
        });
        producer.setDaemon(true);
        producer.setName("uid-pool-producer");
        producer.start();
    }
    @Override
    public String GetId() throws InterruptedException {
        return uidQueue.take();
    }
    @Override
    public boolean PutId(String Uid) {
        return uidQueue.offer(Uid);
    }
}
