package com.example.codePost.service;

public interface UidPool {
    String GetId() throws InterruptedException;
    boolean PutId(String Uid);
}
