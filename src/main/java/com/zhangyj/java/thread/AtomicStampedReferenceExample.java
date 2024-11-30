package com.zhangyj.java.thread;

import java.util.concurrent.atomic.AtomicStampedReference;

public class AtomicStampedReferenceExample {
    public static void main(String[] args) {
        // 初始化
        String initialRef = "A";
        int initialStamp = 0;
        AtomicStampedReference<String> atomicStampedReference = new AtomicStampedReference<>(initialRef, initialStamp);

        // 获取初始值和标记
        System.out.println("Initial Reference: " + atomicStampedReference.getReference());
        System.out.println("Initial Stamp: " + atomicStampedReference.getStamp());

        // 创建新的线程模拟ABA问题
        Thread thread1 = new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            String ref = atomicStampedReference.getReference();

            // CAS操作：更新为B
            boolean success = atomicStampedReference.compareAndSet(ref, "B", stamp, stamp + 1);
            System.out.println("Thread 1: Change A->B: " + success);

            // 再次将值改回A
            stamp = atomicStampedReference.getStamp();
            ref = atomicStampedReference.getReference();
            success = atomicStampedReference.compareAndSet(ref, "A", stamp, stamp + 1);
            System.out.println("Thread 1: Change B->A: " + success);
        });

        Thread thread2 = new Thread(() -> {
            try {
                // 等待thread1完成ABA操作
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            int stamp = 0;
            String ref = "A";
            // CAS操作：尝试更新为C
            boolean success = atomicStampedReference.compareAndSet(ref, "C", stamp, stamp + 1);
            System.out.println("Thread 2: Change A->C: " + success);
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final Reference: " + atomicStampedReference.getReference());
        System.out.println("Final Stamp: " + atomicStampedReference.getStamp());
    }
}
