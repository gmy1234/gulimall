package com.gmy.gulimall.search.Thread;

import java.util.concurrent.*;

public class JUC {
    // 线程池
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void thred(String[] args) {

        /**
         *      三种方式：
         *
         *         R1 runnable01 = new R1();
         *         runnable01.run();
         *
         *         new Thread(runnable01).start();

         *          T1 t1 = new T1();
         *         t1.start();

         *         // 4 实现 Callable + FutureTask (可以拿到返回值，并且处理异常)
         *         FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
         *         new Thread(futureTask).start();
         *
         *         try {
         *             // 等整个线程执行完，拿到最终的返回结果
         *             Integer integer = futureTask.get();
         *             System.out.println(integer);
         *         } catch (InterruptedException | ExecutionException e) {
         *             e.printStackTrace();
         *         }
         *
         */

        /**
         * 4、线程池：
         *      给线程池直接提交任务 【异步任务都提交到线程池】
         */
        // 当前系统中 池子只有 一两个，每个异步任务都要直接提交到线程池，取出执行。
        executorService.execute(new R1());


        System.out.println("main...end");
    }


    public static void main(String[] args){

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(7,
                20,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>()
                );
    }


    public static class T1 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程（继承 Thread）：" + Thread.currentThread().getId());
            int i = 100;
            i /= 10;
            System.out.println( "当前结果（继承 Thread）" + i);
        }
    }


    public static class R1 implements Runnable{

        @Override
        public void run() {
            System.out.println("当前线程（实现 Runnable）：" + Thread.currentThread().getId());
            int i = 100;
            i /= 10;
            System.out.println( "当前结果（实现 Runnable）" + i);
        }
    }

    public static class Callable01 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程（Callable）：" + Thread.currentThread().getId());
            int i = 1000;
            i /= 10;
            System.out.println( "当前结果（Callable）" + i);
            return i;
        }
    }
}
