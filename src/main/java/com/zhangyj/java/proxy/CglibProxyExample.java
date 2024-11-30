package com.zhangyj.java.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLIB无法代理final类（无法生成子类），代理final方法不会进入代理逻辑，直接执行被代理的方法（final方法无法被重写）
 */
// 一个 final 类，CGLIB 无法代理它
final class FinalClass {
    public void testMethod() {
        System.out.println("FinalClass: testMethod");
    }
}

// 一个普通类，包含一个 final 方法，CGLIB 无法代理该方法
class FinalMethodClass {
    public final void finalMethod() {
        System.out.println("FinalMethodClass: finalMethod");
    }

    public void normalMethod() {
        System.out.println("FinalMethodClass: normalMethod");
    }
}

// 创建代理对象的类
public class CglibProxyExample implements MethodInterceptor {
    public Object getProxy(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("Before method: " + method.getName());
        Object result = proxy.invokeSuper(obj, args);  // 调用被代理的方法
        System.out.println("After method: " + method.getName());
        return result;
    }

    public static void main(String[] args) {
        CglibProxyExample proxyExample = new CglibProxyExample();

        // 尝试代理 FinalClass
        try {
            FinalClass finalClassProxy = (FinalClass) proxyExample.getProxy(FinalClass.class);
            finalClassProxy.testMethod();
        } catch (Exception e) {
            System.out.println("代理 FinalClass 失败: " + e.getMessage());
        }

        // 代理包含 final 方法的类
        try {
            FinalMethodClass finalMethodClassProxy = (FinalMethodClass) proxyExample.getProxy(FinalMethodClass.class);
            finalMethodClassProxy.finalMethod();  // 这不会被拦截
            finalMethodClassProxy.normalMethod(); // 这会被拦截
        } catch (Exception e) {
            System.out.println("代理 FinalMethodClass 失败: " + e.getMessage());
        }
    }
}
