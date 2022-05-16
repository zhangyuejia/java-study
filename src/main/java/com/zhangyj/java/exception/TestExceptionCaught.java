package com.zhangyj.java.exception;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 测试线程池异常捕获
 * @author zhangyj
 */
@Slf4j
public class TestExceptionCaught {

    public static void main(String[] args) {
        // 1.无法捕获到线程池异常，execute
        uncaughtThreadPoolExecuteExceptionTest();
        // 2.1 捕获到线程池异常，execute + try-catch
        caughtThreadPoolExecuteExceptionTest1();
        // 2.2 捕获到线程池异常，execute + 重写UncaughtExceptionHandler
        caughtThreadPoolExecuteExceptionTest2();
        // 3.捕获到线程池异常，submit + future.get()
        caughtThreadPoolSubmitExceptionTest();
    }

    private static void uncaughtThreadPoolExecuteExceptionTest() {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            executor.execute(() -> {
                log.info("子线程1：" + Thread.currentThread().getName());
                doBusiness();
            });
        }catch (Exception e){
            log.info("捕获异常1");
            e.printStackTrace();
        }finally {
            sleepQuiet(3 * 1000L);
            executor.shutdown();
        }
    }

    public static class ExceptionLatch extends CountDownLatch {
        @Getter
        @Setter
        private Exception e;

        public ExceptionLatch(int count) {
            super(count);
        }
    }

    private static void caughtThreadPoolExecuteExceptionTest2() {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            ExceptionLatch latch = new ExceptionLatch(1);
            executor.execute(() -> {
                Thread thread = Thread.currentThread();
                thread.setUncaughtExceptionHandler((t, e) -> {
                    // 如果非Exception（即Error）则不进行捕获
                    if(e instanceof Exception){
                        latch.setE((Exception) e);
                    }
                    latch.countDown();
                });
                log.info("子线程1：" + thread.getName());
                doBusiness();
                latch.countDown();
            });
            latch.await();
            if(latch.getE() != null){
                throw latch.getE();
            }
        }catch (Exception e){
            log.info("捕获异常1");
            e.printStackTrace();
        }finally {
            sleepQuiet(3 * 1000L);
            executor.shutdown();
        }
    }

    private static void caughtThreadPoolExecuteExceptionTest1() {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            ExceptionLatch latch = new ExceptionLatch(1);
            executor.execute(() -> {
                try {
                    Thread thread = Thread.currentThread();
                    log.info("子线程1：" + thread.getName());
                    doBusiness();
                }catch (Exception e){
                    latch.setE(e);
                }finally {
                    latch.countDown();
                }
            });
            latch.await();
            if(latch.getE() != null){
                throw latch.getE();
            }
        }catch (Exception e){
            log.info("捕获异常1");
            e.printStackTrace();
        }finally {
            sleepQuiet(3 * 1000L);
            executor.shutdown();
        }
    }

    private static void doBusiness() {
        throw new RuntimeException("1");
    }

    /**
     * 日志如下：
     * 15:38:08.925 [ThreadPoolTaskExecutor-1] INFO TestExceptionCaught - 子线程2：ThreadPoolTaskExecutor-1
     * 15:38:08.925 [main] INFO TestExceptionCaught - 捕获异常2
     * java.util.concurrent.ExecutionException: java.lang.RuntimeException: 1
     *     at java.util.concurrent.FutureTask.report(FutureTask.java:122)
     *     at java.util.concurrent.FutureTask.get(FutureTask.java:192)
     *     at TestExceptionCaught.catchException(TestExceptionCaught.java:61)
     *     at TestExceptionCaught.main(TestExceptionCaught.java:33)
     * Caused by: java.lang.RuntimeException: 1
     *     at TestExceptionCaught.lambda$catchException$1(TestExceptionCaught.java:59)
     *     at java.util.concurrent.FutureTask.run$$$capture(FutureTask.java:266)
     *     at java.util.concurrent.FutureTask.run(FutureTask.java)
     *     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
     *     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
     *     at java.lang.Thread.run(Thread.java:748)
     */
    private static void caughtThreadPoolSubmitExceptionTest() {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            Future<Object> future = executor.submit(() -> {
                log.info("子线程2：" + Thread.currentThread().getName());
                doBusiness();
                return 0;
            });
            // 核心，不调用get()无法捕获到异常
            future.get();
        }catch (Exception e){
            log.info("捕获异常2");
            e.printStackTrace();
        }finally {
            sleepQuiet(3 * 1000L);
            executor.shutdown();
        }
    }

    private static void sleepQuiet(long millseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(millseconds);
        } catch (InterruptedException e) {
            log.error("线程睡眠异常", e);
        }
    }
}