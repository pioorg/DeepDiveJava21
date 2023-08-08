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

package org.przybyl.ddj21.foreign;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Path;
import java.nio.file.Paths;

class ForeignLinkerAPIDemoTest {

    @Test
//    @Disabled
    public void shouldNotWorkForRoot() {
        Path jarPath = Paths.get("target/ffm-1.0-SNAPSHOT.jar");
        Assertions.assertTrue(jarPath.toFile().exists(), "The JAR file has to exist first");
        var jar = MountableFile.forHostPath(jarPath);

        try (var container = new GenericContainer<>("openjdk:21-slim")
            .withCopyFileToContainer(jar, "/tmp/test.jar")
            .withExposedPorts(8000)
            .withCommand("jwebserver")) {

            container.start();

            Assertions.assertDoesNotThrow(() -> {
                var result = container.execInContainer("java", "--enable-preview", "-jar", "/tmp/test.jar");
                Assertions.assertNotEquals(0, result.getExitCode());
                Assertions.assertTrue(result.getStderr().contains("Oh no, don't run me as root"));
            });
        }
    }
}