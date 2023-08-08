/*
 *  Copyright (C) 2023 Piotr Przybył
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.przybyl.ddj21.concurrency.virtThreads;


import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class UncertaintyPrincipleOfVirtualThreads {

    // seed is used here ONLY for demonstration purposes, don't copy and use that in your production code!
    private static final Random rand = new Random(42L);

    private static final Logger logger = Logger.getLogger(UncertaintyPrincipleOfVirtualThreads.class.getName());

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
            "[%1$tF %1$tT %1$tZ] [%4$-7s] %5$s %n");
        // as an experiment, uncomment the following line and see what happens ;-)
        // logger.setLevel(java.util.logging.Level.FINE);
        /*
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(java.util.logging.Level.FINE);
        logger.addHandler(consoleHandler);
        */
    }


    static AtomicInteger startedTasks = new AtomicInteger(0);
    static AtomicInteger finishedTasks = new AtomicInteger(0);


    public static void main(String[] args) throws InterruptedException {
//        logger.info("Started with PID [" + ProcessHandle.current().pid() + "]");

        streamedTasks();
        virtuallyThreadedTasks();

        Thread.sleep(Duration.ofSeconds(6));
        logger.info("Tasks: started [%d], finished [%d]".formatted(startedTasks.get(), finishedTasks.get()));
        System.out.println();
    }

    private static void virtuallyThreadedTasks() throws InterruptedException {

        for (int i = 0; i < 100; i++) {
            int taskId = i;
            Thread.ofVirtual().start(() -> {
                handleTask(taskId);
            });
        }

    }

    private static void streamedTasks() throws InterruptedException {
        Thread.ofPlatform().daemon(true).name("stream-1").start(() -> {
            System.out.println("Started " + Thread.currentThread() + " to do some work");
            IntStream.range(0, 100).parallel().forEach(UncertaintyPrincipleOfVirtualThreads::handleTask);
        });

//        Thread.sleep(10);

        Thread.ofPlatform().daemon(true).name("stream-2").start(() -> {
            System.out.println("Started " + Thread.currentThread() + " to do some OTHER work");
            IntStream.range(100, 200).parallel().forEach(UncertaintyPrincipleOfVirtualThreads::handleTask);
        });
    }

    public static void handleTask(int id) {
        startedTasks.incrementAndGet();
        report(id, "1");
        hardWork(1_000);
        report(id, "2");
        hardWork(1_000);
        report(id, "3");
        hardWork(1_000);
        report(id, "4");
        hardWork(1_000);
        report(id, "5");
        hardWork(1_000);
        logger.info(() -> "FINISHED %3d %s".formatted(id, Thread.currentThread()));
        finishedTasks.incrementAndGet();
    }


    private static void report(int taskId, String stage) {
        logger.fine(() -> "STEP %s %3d %s".formatted(stage, taskId, Thread.currentThread()));
    }

    public static long hardWork(long ms) {
        long stop = System.nanoTime() + ms * 1_000_000;
        long mod = -1;
        while (System.nanoTime() < stop) {
            long a = rand.nextLong(1_000_000_000, 1_000_000_000_000L);
            long b = rand.nextLong(1_000_000, 1_000_000_000);
            mod = a % b;
        }
        return mod;
    }

}
