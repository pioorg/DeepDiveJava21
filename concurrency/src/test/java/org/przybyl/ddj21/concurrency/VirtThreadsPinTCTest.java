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

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.NginxContainer;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

class VirtThreadsPinTCTest {

    @Test
    @Disabled
    public void shouldNotPin() throws IOException {

        Network network = Network.newNetwork();

        // we're going to copy this file from resources to nginx container
        var index = MountableFile.forClasspathResource("index.html");

        //preparing artifact to be copied
        Path jarPath = Paths.get("target/concurrency-1.0-SNAPSHOT.jar");
        Assertions.assertTrue(jarPath.toFile().exists(), "The JAR file has to exist first");

        try (
            var nginx = new NginxContainer<>("nginx:1.25.4")
                .withCopyFileToContainer(index, "/usr/share/nginx/html/index.html")
                .withNetwork(network)
                .withNetworkAliases("nginx")
                .waitingFor(new HttpWaitStrategy());
            var toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.7.0")
                .withNetwork(network)
                .withNetworkAliases("toxiproxy");
            var container = new GenericContainer<>("openjdk:21-slim")
                .withCopyFileToContainer(MountableFile.forHostPath(jarPath), "/tmp/test.jar")
                .withNetwork(network)
                .withExposedPorts(8000)
                .withCommand("jwebserver")
        ) {
            // starting both containers in parallel
            Stream.of(nginx, toxiproxy, container).parallel().forEach(GenericContainer::start);

            // creating intoxicated connection to be used between our client and nginx
            ToxiproxyClient client = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
            Proxy proxy = client.createProxy("slow-nginx", "0.0.0.0:8666", "nginx:80");
            proxy.toxics().latency("latency", ToxicDirection.DOWNSTREAM, 500).setJitter(50);

            Assertions.assertDoesNotThrow(() -> {

                // running the client which should call the nginx using intoxicated proxy
                var result = container.execInContainer(
                    "java", "--enable-preview", "-Djdk.tracePinnedThreads=full", "-jar", "/tmp/test.jar", "http://toxiproxy:8666");

                // eventually it should exit successfully
                Assertions.assertEquals(0, result.getExitCode());

                // and there should be no virtual threads pinned
                MatcherAssert.assertThat(result.getStdout(), not(containsString("onPinned")));
            });

        }
    }

}