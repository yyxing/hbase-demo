package com.devil.concurrent.reentrantLock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 可重入锁展示
 * @ClassName ReentrantLockTest
 * @Author Devil
 * @date 2020.05.09 15:54
 */
public class ReentrantLockTest {

    private static ReentrantLock lock = new ReentrantLock();

    public void test(){
        lock.lock();
        System.out.println("");
    }
}
