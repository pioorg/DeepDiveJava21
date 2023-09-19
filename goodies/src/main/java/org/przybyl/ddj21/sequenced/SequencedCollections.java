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

package org.przybyl.ddj21.sequenced;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class SequencedCollections {

    private static final Random random = new Random();

    public static void main(String[] args) {
        demoCollection();
//        demoMap();
    }

    private static void demoCollection() {
        SequencedCollection<String> sc = gimmeSequencedCollection();
        try {
            sc.addFirst("B");
            sc.addFirst("A");
            sc.addLast("F");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot add elements");
//            e.printStackTrace(System.out);
        }

        System.out.println(sc.getClass());
        System.out.println(sc);
        System.out.println(sc.reversed());
        System.out.println("First element: " + sc.getFirst());
        for (String s : sc.reversed()) {
            System.out.print(s);
        }
        System.out.println();
    }

    private static SequencedCollection<String> gimmeSequencedCollection() {
        var init = List.of("C", "D", "E");
        return switch (random.nextInt(6)) {
            case 0 -> new ArrayList<>(init);
            case 1 -> new LinkedList<>(init);
            case 2 -> new ArrayDeque<>(init);
            case 3 -> new LinkedHashSet<>(init);
            case 4 -> new TreeSet<>(init);
            case 5 -> Collections.unmodifiableSequencedCollection(init);
            default -> throw new IllegalStateException("Unexpected value");
        };
    }

    private static void demoMap() {
        SequencedMap<String, String> sm = gimmeSequencedMap();

        try {
            sm.putFirst("B", "7");
            sm.putFirst("A", "5");
            sm.putLast("E", "17");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot add entries");
//            e.printStackTrace(System.out);
        }

        System.out.println(sm.getClass());
        System.out.println(sm);
        System.out.println(sm.reversed());
        System.out.println("First entry: " + sm.firstEntry());
        for (String s : sm.sequencedKeySet().reversed()) {
            System.out.print(s);
        }
        System.out.println();

    }

    private static SequencedMap<String, String> gimmeSequencedMap() {
        var init = Map.of("C", "12", "D", "15");
        return switch (random.nextInt(3)) {
            case 0 -> new TreeMap<>(init);
            case 1 -> new LinkedHashMap<>(init);
            case 2 -> Collections.unmodifiableSequencedMap(new TreeMap<>(init));
            default -> throw new IllegalStateException("Unexpected value");
        };
    }
}
