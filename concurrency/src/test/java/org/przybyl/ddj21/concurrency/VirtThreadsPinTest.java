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
package org.przybyl.ddj21.concurrency;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moditect.jfrunit.EnableEvent;
import org.moditect.jfrunit.JfrEventTest;
import org.moditect.jfrunit.JfrEvents;
import org.przybyl.ddj21.concurrency.virtThreads.VirtThreads;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Executors;

@JfrEventTest
public class VirtThreadsPinTest {

    private final Random random = new Random();
    public JfrEvents jfrEvents = new JfrEvents();
    private HttpServer server;

    @BeforeEach
    void setUp() throws Exception {
        server = HttpServer.create(new InetSocketAddress(8080), 1_000_000);
        server.createContext("/", exchange -> {
            int sum = random.ints(15_000_000).sum();
            var responseBytes = (sum + "").getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("content-type", "text/plain");
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
            exchange.close();
        });
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(1);
    }

    @Test
    @EnableEvent("jdk.VirtualThreadPinned")
    public void shouldNotPin() throws InterruptedException {

        var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:8080")).GET().build();

        VirtThreads.getGreetings(client, request, 20);

        jfrEvents.awaitEvents();
        Assertions.assertTrue(jfrEvents.events().findAny().isEmpty(), "there should be no pinned events");
    }

}
