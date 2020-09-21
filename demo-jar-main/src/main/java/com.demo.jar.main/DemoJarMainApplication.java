package com.demo.jar.main;


import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author: 苏敏
 * @date: 2020/9/21 15:18
 */
@SpringBootApplication
@RestController
public class DemoJarMainApplication {

    private static ApplicationContext ctx;
    private static DefaultListableBeanFactory defaultListableBeanFactory;
    private static Class myClass;

    public static void main(String[] args) {
        //获取context.
        ctx = (ApplicationContext) SpringApplication.run(DemoJarMainApplication.class, args);
        //获取BeanFactory
        defaultListableBeanFactory = (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();
    }

    @RequestMapping("/register")
    public void register() throws ClassNotFoundException, MalformedURLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        loadJar("D:/work/test/out/artifacts/test_jar/test.jar");
        load();
//        Class<?> aClass = Class.forName("com.test.jar.Play");
        loadBean(myClass);
        System.out.println("已完成注入");

//        Object instance = aClass.newInstance();
//        Object strip = aClass.getDeclaredMethod("add", String.class, String.class).invoke(instance, "1", "2");
//        System.out.println(strip);
    }

    @RequestMapping("/find")
    public void find() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object testService = ctx.getBean("testService");
//        Class<?> aClass = Class.forName("com.test.jar.Play");
//        aClass.getDeclaredMethod("play", String.class).invoke(testService, "aaa");
        myClass.getDeclaredMethod("play").invoke(testService);
    }

    @RequestMapping("/delete")
    public void delete() {
        defaultListableBeanFactory.removeBeanDefinition("testService");
        System.out.println("已删除");
    }

    private void loadBean(Class clazz) {

        //获取BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();

        //创建bean信息
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
//        beanDefinitionBuilder.addPropertyValue("name","张三");
//        beanDefinitionBuilder.addPropertyValue("size",1024*1024*1024);
//        beanDefinitionBuilder.addConstructorArgValue("张三");
//        beanDefinitionBuilder.addConstructorArgValue(1024*1024*1024);
        //动态注册bean
        defaultListableBeanFactory.registerBeanDefinition("testService", beanDefinitionBuilder.getBeanDefinition());
    }

    private static void loadJar(String jarPath) {
        File jarFile = new File(jarPath);
        // 从URLClassLoader类中获取类所在文件夹的方法，jar也可以认为是一个文件夹
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException | SecurityException e1) {
            e1.printStackTrace();
        }
        // 获取方法的访问权限以便写回
        boolean accessible = method.isAccessible();
        try {
            method.setAccessible(true);
            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            URL url = jarFile.toURI().toURL();
            method.invoke(classLoader, url);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            method.setAccessible(accessible);
        }
    }
    private static void load() throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        URL url = new URL("http://47.100.0.69:8080/files/test.jar");
        URLClassLoader myClassLoader = new URLClassLoader( new URL[] { url } );
        myClass = myClassLoader.loadClass("com.test.jar.Play");
//        Object test=myClass.newInstance();
//        Method m = test.getClass().getMethod("play");
//        m.invoke(test);
    }
}
