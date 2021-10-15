package com.example.kitchenapi.apparatus;

import java.util.concurrent.locks.ReentrantLock;

public enum Apparatus {
    Oven,
    Stove;

    private ReentrantLock lock = new ReentrantLock();

    public boolean tryLock() {
        return this.lock.tryLock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    public void lock() {this.lock.lock();}

    public boolean isLocked() {return this.lock.isLocked();}
}
