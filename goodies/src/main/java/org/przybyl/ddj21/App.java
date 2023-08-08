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
package org.przybyl.ddj21;

public class App {
    public static void main(String[] args) {

        Object version = Runtime.version().feature();
        String greeting = getGreeting(version);
        System.out.println(greeting);

    }

    private static String getGreeting(Object version) {
        return switch (version) {
            case null -> throw new IllegalArgumentException("Impossible!");
            case Integer i when (i >= 17) -> "Hello, this is Java™ " + i + " ;-)";
            case Integer i -> i + "? But how?";
            default -> "oops...";
        };
    }
}
