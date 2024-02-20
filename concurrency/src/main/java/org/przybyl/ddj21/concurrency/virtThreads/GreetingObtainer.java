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


import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GreetingObtainer {

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

    public synchronized void getGreeting(HttpClient client, HttpRequest request, int attempt) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.print("[Attempt: " + attempt + ", body: " + response.body() + "] ");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
