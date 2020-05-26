package com.devil.concurrent.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @Description 原子数组操作
 * @ClassName AtomicArrayDemo
 * @Author Devil
 * @date 2020.05.10 21:29
 */
public class AtomicArrayDemo {

    public static void main(String[] args) throws InterruptedException {
        AtomicIntegerArray array = new AtomicIntegerArray(100);
        int[] arrays = new int[100];
        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Task(array, arrays));
            threads[i].start();
            threads[i].join();
        }
        for (int i = 0; i < array.length(); i++) {
            System.out.println(array.get(i));
            System.out.println(arrays[i]);
        }
    }

}

class Task implements Runnable {

    private AtomicIntegerArray array;
    public static volatile int[] arrays;

    public Task(AtomicIntegerArray array, int[] arrays) {
        this.array = array;
        Task.arrays = arrays;
    }

    @Override
    public void run() {
        for (int i = 0; i < array.length(); i++) {
            for (int j = 0; j < 100; j++) {
                array.getAndIncrement(i);
                arrays[i]++;
            }
        }
    }
}
