package com.devil.concurrent.threadpool;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @ClassName EveryTaskOneThread
 * @Author Devil
 * @date 2020.05.07 15:05
 */
public class EveryTaskOneThread {

    private final static String obj1 = "1";

    private final static String obj2 = "2";

    static class Lock1 implements Runnable {
        @Override
        public void run() {
            synchronized (EveryTaskOneThread.obj1) {
                System.out.println("Lock1 lock obj1");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (EveryTaskOneThread.obj2) {
                    System.out.println("Lock1 lock obj2");
                }
            }
        }
    }

    static class Lock2 implements Runnable {
        @Override
        public void run() {
            synchronized (EveryTaskOneThread.obj2) {
                System.out.println("Lock2 lock obj2");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (EveryTaskOneThread.obj1) {
                    System.out.println("Lock2 lock obj1");
                }
            }
        }
    }

    public static void main(String[] args) {
//        new Thread(new Lock1()).start();
//        new Thread(new Lock2()).start();
        new Thread(new DeadLock1()).start();
        new Thread(new DeadLock2()).start();

    }

    private static Lock lock1 = new ReentrantLock();
    private static Lock lock2 = new ReentrantLock();

    static class DeadLock1 implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    lock1.lock();
                    Thread.sleep(1500);
                    System.out.println(String.format("线程%s尝试获取lock1成功", Thread.currentThread().getName()));
                    lock2.lock();
                    Thread.sleep(1500);
                    System.out.println(String.format("线程%s尝试获取lock2成功", Thread.currentThread().getName()));
                    lock1.unlock();
                    lock2.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class DeadLock2 implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    lock2.lock();
                    Thread.sleep(1500);
                    System.out.println(String.format("线程%s尝试获取lock2成功", Thread.currentThread().getName()));
                    lock1.lock();
                    Thread.sleep(1500);
                    System.out.println(String.format("线程%s尝试获取lock1成功", Thread.currentThread().getName()));
                    lock2.unlock();
                    lock1.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
