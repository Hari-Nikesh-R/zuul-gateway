package com.example.zuulgateway.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ThreadCleanupListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Do nothing
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        for (Thread thread : threads) {
            if (!thread.isDaemon()) {
                thread.interrupt();
            }
        }
    }
}
