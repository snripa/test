package org.rii.runner;

import org.rii.Checker;
import org.rii.StatsUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Runner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);
    public static final long PERIOD_DEF = 15;
    public static final String HOST_DEF = "http://localhost:12345";

    public static void main(String[] args) throws InterruptedException {

        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("stats-check");
            thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Uncaught exception: {}", e, e));
            return thread;
        };
        long period = initPeriod();
        String host = initHost();
        System.out.println("Starting server '" + host + "' health check periodically (every " + period + " sec). " +
                "See results in log/server-status.log");
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(threadFactory);

        StatsUpdater updater = new StatsUpdater();
        Checker ch = new Checker(host);
        executor.scheduleAtFixedRate(() ->
                        updater.update(ch.checkHealth()),
                period, period, TimeUnit.SECONDS);
        System.out.println("Health monitor started");
        Thread.currentThread().join();
    }

    private static String initHost() {
        String env = System.getProperty("checkHost");
        if (env == null || env.isEmpty()) {
            return HOST_DEF;
        } else {
            return env;
        }
    }

    private static long initPeriod() {
        long result = PERIOD_DEF;
        String periodStr = System.getProperty("periodSec");
        if (periodStr != null) {
            try {
                result = Long.parseLong(periodStr);
            } catch (Throwable t) {
                //ignore
            }
        }
        return result;
    }
}
