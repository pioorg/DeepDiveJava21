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


import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SequenceLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

/**
 * Demo of Foreign Memory Access API preview feature of Java 21, 7th or 8th? preview,
 * based on https://openjdk.java.net/jeps/442
 */
public class ForeignMemoryAccessAPIDemo {

    public static void main(String[] args) throws InterruptedException {
        sameThreadDemo();
//        twoThreadsDemo();
    }

    private static void sameThreadDemo() {
        SequenceLayout intArrayLayout = MemoryLayout.sequenceLayout(10, ValueLayout.JAVA_INT);
        Arena arena = Arena.ofAuto();
//        try(Arena arena = Arena.ofShared()) {
            MemorySegment segment = arena.allocate(intArrayLayout);
            populateNativeMem(intArrayLayout, segment);
            examineNativeMem(intArrayLayout, segment);
//        }
    }

    private static void twoThreadsDemo() throws InterruptedException {
        SequenceLayout intArrayLayout = MemoryLayout.sequenceLayout(10, ValueLayout.JAVA_INT);
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(intArrayLayout);
            populateNativeMem(intArrayLayout, segment);
            examineNativeMem(intArrayLayout, segment);
//            var readerThread = new Thread(() -> examineNativeMem(intArrayLayout, segment), "readerThread");
//            readerThread.start();
//            readerThread.join(100L);
            arena.close();
            examineNativeMem(intArrayLayout, segment);
        }
    }

    private static void populateNativeMem(SequenceLayout intArrayLayout, MemorySegment segment) {

        VarHandle intElemHandle = intArrayLayout.varHandle(MemoryLayout.PathElement.sequenceElement());
        for (int i = 0; i < intArrayLayout.elementCount(); i++) {
            segment.setAtIndex(ValueLayout.JAVA_INT, i, i);
//            intElemHandle.set(segment, i, i);
        }
    }

    private static void examineNativeMem(SequenceLayout intArrayLayout, MemorySegment segment) {
        System.out.println("Examining memory at: " + segment.address());
        System.out.println("Is accessible by current thread? " +segment.isAccessibleBy(Thread.currentThread()));
        VarHandle intElemHandle = intArrayLayout.varHandle(MemoryLayout.PathElement.sequenceElement());
        for (int i = 0; i < intArrayLayout.elementCount(); i++) {
            System.out.print(segment.getAtIndex(ValueLayout.JAVA_INT, i));
            System.out.print(" ");
            System.out.println(intElemHandle.get(segment, i));
        }
    }
}
