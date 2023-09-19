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
import java.time.Instant;

public class VirtThreadsLimits {

    private final static Duration EMULATE_WORK_FOR = Duration.ofSeconds(10);

    public static void main(String[] args) throws InterruptedException {
        Instant start = Instant.now();
        Thread lastThread = null;

        int maxThreadNo = 30_000;
        for (int i = 0; i <= maxThreadNo; i++) {
            lastThread = createThread(i);
            lastThread.start();
            if (i % 5_000 == 0) {
                System.out.printf("Current count %d%n", i);
            }
        }
        lastThread.join();

        Instant stop = Instant.now();
        Duration took = Duration.between(start, stop);

        System.out.printf("Finished running %d threads; took %s%n", maxThreadNo, took);
    }

    private static Thread createThread(int i) {
        Runnable job = () -> blockingOperation(i);
        return new Thread(job);
//        return Thread.ofVirtual().unstarted(job);
//        return Thread.ofPlatform().unstarted(job);
    }

    static void blockingOperation(int task) {
//            System.out.printf("Task: %6d, thread %s%n", task, Thread.currentThread());
        sneakySleep(EMULATE_WORK_FOR);
    }


    public static void sneakySleep(long d) {
        try {
            Thread.sleep(d);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//            UncertaintyPrincipleOfVirtualThreads.hardWork(d);
    }

    public static void sneakySleep(Duration d) {
        try {
            Thread.sleep(d);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

