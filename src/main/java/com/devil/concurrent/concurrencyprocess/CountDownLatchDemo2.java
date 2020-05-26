package com.devil.concurrent.concurrencyprocess;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description
 * @ClassName CountDownLatchDemo2
 * @Author Devil
 * @date 2020.05.19 22:27
 */
public class CountDownLatchDemo2 {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(5);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            final int no = i + 1;
            Runnable runnable = () -> {
                try {
                    System.out.println("运动员No." + no + "号已经准备就绪");
                    begin.await();
                    System.out.println("运动员No." + no + "开始起跑");
                    Thread.sleep((long) (Math.random() * 10000L));
                    System.out.println("运动员No." + no + "到达终点");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    end.countDown();
                }
            };
            executorService.submit(runnable);
        }
        Thread.sleep(1000);
        System.out.println("预备，跑！");
        begin.countDown();
        end.await();
        System.out.println("所有选手都完成了比赛!");
        executorService.shutdown();
    }
}
