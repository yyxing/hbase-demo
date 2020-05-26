package com.devil.concurrent.atomic;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description 利用原子类写一个自旋锁
 * @ClassName SpinLock
 * @Author Devil
 * @date 2020.05.10 21:44
 */
public class SpinLock {

    private static AtomicReference<Thread> spin = new AtomicReference<>();

    public void lock() {
        while (!spin.compareAndSet(null, Thread.currentThread())) {
            System.out.println(Thread.currentThread().getName() + "自旋锁获取失败，重试获取。");
        }
    }

    public void unlock() {
        spin.compareAndSet(Thread.currentThread(), null);
    }

    public static void main(String[] args) {
        SpinLock lock = new SpinLock();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "尝试获取自旋锁");
            lock.lock();
            System.out.println(Thread.currentThread().getName() + "成功获取自旋锁");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
            System.out.println(Thread.currentThread().getName() + "释放自旋锁");
        }).start();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "尝试获取自旋锁");
            lock.lock();
            System.out.println(Thread.currentThread().getName() + "成功获取自旋锁");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
            System.out.println(Thread.currentThread().getName() + "释放自旋锁");
        }).start();
    }
}
