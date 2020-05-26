package com.devil.concurrent.concurrencyprocess;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @Description
 * @ClassName SemaphoreDemo
 * @Author Devil
 * @date 2020.05.20 21:26
 */
public class SemaphoreDemo {

    private static Semaphore semaphore = new Semaphore(3, true);


    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 50; i++) {
            final int no = i + 1;
            executorService.submit(() -> {
                try {
                    semaphore.acquire(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("公司" + no + "拿到临时许可证，允许工作");
                try {
                    Thread.sleep((long) (Math.random() * 10000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                semaphore.release();
                System.out.println("工作完毕！归还临时许可证");
            });
        }
        executorService.shutdown();
    }

}
