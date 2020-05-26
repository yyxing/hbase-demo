package com.devil.concurrent.reentrantLock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Description
 * @ClassName ReentrantWriteLock
 * @Author Devil
 * @date 2020.05.09 21:27
 */
public class ReentrantWriteLockTest {

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
    private static ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private static ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    public static void main(String[] args) throws InterruptedException {
        new Thread(ReentrantWriteLockTest::write, "write1").start();
        new Thread(ReentrantWriteLockTest::read, "read1").start();
        new Thread(ReentrantWriteLockTest::read, "read2").start();
        new Thread(ReentrantWriteLockTest::write, "write2").start();
        new Thread(ReentrantWriteLockTest::read, "read3").start();

//        Thread.sleep(200);
//        new Thread(() -> {
            Thread[] threads = new Thread[1000];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(ReentrantWriteLockTest::read, "子线程创建的读取" + i);
            }
            for (int i = 0; i < threads.length; i++) {
                threads[i].start();
            }
//        }).start();
    }


    public static void read() {
        System.out.println(Thread.currentThread().getName() + "尝试获取读锁");
        try {
            readLock.lock();
            System.out.println(Thread.currentThread().getName() + "获取读锁，开始读取");
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(Thread.currentThread().getName() + "释放读锁，结束读取");
            readLock.unlock();
        }

    }

    public static void write() {
        System.out.println(Thread.currentThread().getName() + "尝试获取写锁");
        try {
            writeLock.lock();
            System.out.println(Thread.currentThread().getName() + "获取写锁，开始写入");
            Thread.sleep(40);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(Thread.currentThread().getName() + "释放写锁，结束写入");
            writeLock.unlock();
        }

    }
}
