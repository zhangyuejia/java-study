package com.zhangyj.java.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 父子线程
 */
public class ParentChildThreadExample {
    // 定义 InheritableThreadLocal，用于在父子线程之间传递数据
    // 实际上只是创建线程new Thread的时候做了一份拷贝，之后互不影响
    private static final InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        // 在父线程中设置值
        inheritableThreadLocal.set("Parent thread value");
        System.out.println("1.Parent: " + inheritableThreadLocal.get());

        CountDownLatch latch = new CountDownLatch(1);
        // 创建子线程
        Thread childThread = new Thread(() -> {
            // 获取 InheritableThreadLocal 值
            System.out.println("2.Child: " + inheritableThreadLocal.get());
            inheritableThreadLocal.set("Child thread value");
            System.out.println("3.Child: " + inheritableThreadLocal.get());
            latch.countDown();
        });
        System.out.println("4.Parent: " + inheritableThreadLocal.get());
        inheritableThreadLocal.set("Parent2 thread value");
        childThread.start();

        // 主线程继续执行
        latch.await();
        System.out.println("5.Parent: " + inheritableThreadLocal.get());

        // 打印结果
        // 1.Parent: Parent thread value
        // 4.Parent: Parent thread value
        // 2.Child: Parent thread value
        // 3.Child: Child thread value
        // 5.Parent: Parent2 thread value
    }
}
