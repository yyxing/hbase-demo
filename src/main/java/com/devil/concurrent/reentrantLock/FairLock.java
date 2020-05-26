package com.devil.concurrent.reentrantLock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 测试公平锁和非公平锁
 * @ClassName FairLock
 * @Author Devil
 * @date 2020.05.09 17:36
 */
public class FairLock {
    public static void main(String[] args) throws InterruptedException {
        PrintQueue queue = new PrintQueue();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Task(queue));
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
            Thread.sleep(100);
        }
    }

}

class Task implements Runnable {

    private PrintQueue queue;

    public Task(PrintQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        queue.print();
    }
}

class PrintQueue {
    private ReentrantLock lock = new ReentrantLock(false);

    public void print() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() +
                    "获取到锁，打印购买信息");
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() +
                    "获取到锁，打印购买信息2");
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        System.out.println(Thread.currentThread().getName() + "打印完成");
    }
}