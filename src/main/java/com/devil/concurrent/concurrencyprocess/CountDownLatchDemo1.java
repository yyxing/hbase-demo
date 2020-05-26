package com.devil.concurrent.concurrencyprocess;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description
 * @ClassName CountDownLatchDemo1
 * @Author Devil
 * @date 2020.05.19 22:06
 */
public class CountDownLatchDemo1 {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(5);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            final int no = i + 1;
            executorService.submit(new CheckWork(latch, no));
        }
        latch.await();
        System.out.println(latch.getCount());
        executorService.shutdown();
        System.out.println("所有质检员完成了质检工作，开始下一个环节。");
    }

    static class CheckWork implements Runnable {

        private CountDownLatch latch;
        private final int no;

        public CheckWork(CountDownLatch latch, int no) {
            this.latch = latch;
            this.no = no;
        }

        @Override
        public void run() {
            try {
                System.out.println("质检员" + no + "开始质检工作.");
                Thread.sleep((long) (Math.random() * 10000));
                System.out.println("质检员" + no + "完成质检工作.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }

        }
    }
}
