package com.devil.concurrent.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @Description 普通变量升级成原子变量
 * @ClassName AtomicIntegerFieldUpdaterDemo
 * @Author Devil
 * @date 2020.05.10 21:55
 */
public class AtomicIntegerFieldUpdaterDemo implements Runnable {
    private static AtomicIntegerFieldUpdater<Candidate> updater = AtomicIntegerFieldUpdater.newUpdater(Candidate.class,
            "score");

    static Candidate tom = new Candidate();
    static Candidate peter = new Candidate();

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            tom.score++;
            updater.getAndIncrement(peter);
        }
    }

    static class Candidate {
        public volatile int score = 0;
    }

    public static void main(String[] args) {
        AtomicIntegerFieldUpdaterDemo demo = new AtomicIntegerFieldUpdaterDemo();
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 100; i++) {
            executorService.submit(demo);
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {

        }

        System.out.println("经过升级的变量" + peter.score);
        System.out.println("普通变量" + tom.score);
    }
}

