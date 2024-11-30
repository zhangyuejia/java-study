package com.zhangyj.java.base;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 配置jclasslib插件查看该类生成的字节码文件，查看synchronized原理
 */
public class TestLockClass {

    public synchronized void lockMethod() {
        System.out.println("修饰方法块");
    }

    public static synchronized void lockStaticMethod() {
        System.out.println("修饰方法块");
    }

    private final Object lockObj = new Object();

    public void lockObj() {
        ReentrantLock lock = new ReentrantLock();
        synchronized (lockObj) {
            System.out.println("修饰代码块");
        }
    }

    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
}
