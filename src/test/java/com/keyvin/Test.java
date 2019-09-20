package com.keyvin;

import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author weiwh
 * @date 2019/8/15 18:48
 */
public class Test {
    //可缓存线程池，若线程池长度超过处理需要，则回收空线程，否则创建新线程，线程规模可无限大。
    //当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程。
    static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    //定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
    //定长线程池的大小最好根据系统资源进行设置。如Runtime.getRuntime().availableProcessors()。
    static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    //定长线程池，支持定时及周期性任务执行，类似Timer。
    static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

    //单线程 的线程池，支持FIFO, LIFO, 优先级策略。
    static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();



    public static void main(String[] args) {

        //创建池,可放置4个线程
        ExecutorService pool = Executors.newFixedThreadPool(4);

        for(int i = 0;i<20;i++) {
            int number = i;
            //将线程加入池
            pool.execute(new Runnable() {

                @Override
                public void run() {
                    System.out.println("现在时间是："+System.currentTimeMillis()+"第"+number+"个线程"+Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


    }

}
