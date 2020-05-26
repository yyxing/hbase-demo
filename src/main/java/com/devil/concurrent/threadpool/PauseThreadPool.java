package com.devil.concurrent.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 可暂停线程池
 * @ClassName PauseThreadPool
 * @Author Devil
 * @date 2020.05.08 14:35
 */
public class PauseThreadPool extends ThreadPoolExecutor {

    /**
     * 可重入锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 对于线程的唤醒或者阻塞
     */
    private Condition condition = lock.newCondition();

    private boolean isPaused = false;

    public PauseThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public PauseThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public PauseThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public PauseThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * 钩子方法 在每个执行之前调用
     *
     * @param t
     * @param r
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        lock.lock();
        try {
            while (isPaused) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    public void pause() {
        try {
            lock.lock();
            System.out.println("线程池暂停");
            isPaused = true;
        } finally {
            lock.unlock();
        }
    }

    public void resume() {
        try {
            lock.lock();
            System.out.println("线程池恢复");
            isPaused = false;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PauseThreadPool pauseThreadPool = new PauseThreadPool(10, 20, 10L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            pauseThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100L);
                        System.out.println(String.format("执行次数：%d，线程%s开始执行", finalI,Thread.currentThread().getName()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        Thread.sleep(1500);
        pauseThreadPool.pause();
        Thread.sleep(1500);
        pauseThreadPool.resume();
    }
}
