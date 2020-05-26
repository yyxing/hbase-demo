package com.devil.concurrent.concurrencyprocess;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 生产者消费者
 * @ClassName ConditionDemo
 * @Author Devil
 * @date 2020.05.20 21:50
 */
public class ConditionDemo {

    private static ReentrantLock lock = new ReentrantLock();
    private static Condition notFull = lock.newCondition();
    private static Condition notEmpty = lock.newCondition();
    private volatile static Queue<Product> productRepository = new LinkedBlockingQueue<>(50);
    private final static Integer maxNumber = 50;
    private static AtomicInteger currentNumber = new AtomicInteger(0);
    // 判断生产者是否等待
    private volatile static boolean consumerFlag = false;
    // 判断消费者是否等待
    private volatile static boolean productFlag = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(new MyConsumer()).start();
        new Thread(new MyProducer()).start();
//        Thread.sleep(1000000);
//        consumerExecutorService.shutdown();
//        producerExecutorService.shutdown();
    }


    static class MyProducer implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    while (productRepository.size() == 50) {
                        try {
                            System.out.println("仓库已满，暂停生产");
                            productFlag = true;
                            notFull.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    currentNumber.getAndIncrement();
                    productRepository.offer(new Product(currentNumber + "号产品"));
                    System.out.println("生产者" + Thread.currentThread().getName() + "号生产" + +currentNumber.get() + "号产品");
                    productFlag = false;
                    if (consumerFlag){
                        notEmpty.signalAll();
                    }
                } finally {
                    lock.unlock();
                    try {
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static class MyConsumer implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    while (productRepository.isEmpty()) {
                        try {
                            System.out.println("仓库为空，停止消费。");
                            consumerFlag = true;
                            notEmpty.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Product poll = productRepository.poll();
                    currentNumber.decrementAndGet();
                    consumerFlag = false;
                    System.out.println("消费者" + Thread.currentThread().getName() + "号消费" + poll.getProductName());
                    if (productFlag){
                        notFull.signalAll();
                    }
                } finally {
                    lock.unlock();
                    try {
                        Thread.sleep((long) (1500 * Math.random()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    static class Product {
        private String productName;

        public Product(String productName) {
            this.productName = productName;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }
    }


}
