package com.devil.concurrent.atomic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @ClassName AtomicIntegerDemo
 * @Author Devil
 * @date 2020.05.10 16:28
 */
public class AtomicIntegerDemo implements Runnable {

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);
    private static volatile int a = 0;

    public void incrementBasic(){
        a++;
    }

    public void incrementAtomic(){
        atomicInteger.incrementAndGet();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            incrementAtomic();
            incrementBasic();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicIntegerDemo task = new AtomicIntegerDemo();
        Thread thread = new Thread(task);
        Thread thread1 = new Thread(task);
        thread.start();
        thread1.start();
        thread.join();
        thread1.join();
        System.out.println("原子类变量结果：" + task.atomicInteger.get());
        System.out.println("普通变量结果：" + task.a);
    }
}

