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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;


public class VirtThreads {

    public static void main(String[] args) throws InterruptedException {
        Thread.ofVirtual().start(() -> {
            Thread ct = Thread.currentThread();
            System.out.printf("Is current thread virtual? %b%n", ct.isVirtual());
            System.out.printf("Current thread's ID: %d%n", ct.threadId());
            System.out.printf("Current thread's deprecated ID: %d%n", ct.getId());
            System.out.printf("Is current thread a daemon? %b%n", ct.isDaemon());
            System.out.println();

        }).join();

        if (args.length > 0) {
            var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
            var request = HttpRequest.newBuilder(URI.create(args[0])).GET().build();
            getGreetings(client, request, 20);
        }
    }

    public static void getGreetings(HttpClient client, HttpRequest request, int times) throws InterruptedException {
        Thread last = null;
        for (int i = 0; i < times; i++) {
            final var c = i;
            var g = new GreetingObtainer();
            last = Thread.startVirtualThread(() -> g.getGreeting(client, request, c));
            System.out.print(("[ Created " + c + "] "));
        }
        System.out.println();
        last.join();
    }
}

