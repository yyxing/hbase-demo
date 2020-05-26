package com.devil.concurrent.atomic;

import sun.misc.Unsafe;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Description 对于求和操作的原子性的新旧工具对比
 * @ClassName AdderDemo
 * @Author Devil
 * @date 2020.05.10 22:11
 */
public class AdderDemo {

    private static LongAdder longAdder = new LongAdder();
    private static AtomicLong atomicLong = new AtomicLong();
    LongAccumulator longAccumulator = new LongAccumulator((x, y) -> x + 1 + y, 0);
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            executorService.submit(new Adder(longAdder));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {

        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("LongAdder增加至1E需要%dms，结果是否为1E:%s", (end - start), longAdder.sum()));

        ExecutorService executorService2 = Executors.newFixedThreadPool(1);
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            executorService2.submit(new AtomicAdder(atomicLong));
        }
        executorService2.shutdown();
        while (!executorService2.isTerminated()) {

        }

        long end1 = System.currentTimeMillis();
        System.out.println(String.format("AtomicLong增加至1E需要%dms，结果是否为1E:%s", (end1 - start1), atomicLong.get()));
    }
}

class Adder implements Runnable {
    private LongAdder counter;
    Unsafe unsafe = Unsafe.getUnsafe();
    public Adder(LongAdder counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            counter.increment();
        }
    }
}

class AtomicAdder implements Runnable {
    private AtomicLong counter;

    public AtomicAdder(AtomicLong counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            counter.getAndIncrement();
        }
    }
}
