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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ExecutorsDemo {

    public static void main(String[] args) {
        Instant start = Instant.now();

        // Manipulate parameters here, it depends on your hardware
        int tasks = 30_000;
        var blockedFor = Duration.ofSeconds(6);
        try (var e = createExecutor()) {
            IntStream.rangeClosed(0, tasks).forEach(i -> {
                e.submit(() -> {
                    VirtThreadsLimits.sneakySleep(blockedFor);
                    if (i % 5_000 == 0) {
                        System.out.printf("Current count %d%n", i);
                    }
                });
            });
        }

        Instant stop = Instant.now();
        Duration took = Duration.between(start, stop);

        System.out.printf("Finished; took %s%n", took);
    }

    private static ExecutorService createExecutor() {
        return Executors.newCachedThreadPool();
//        return Executors.newVirtualThreadPerTaskExecutor();
    }
}

