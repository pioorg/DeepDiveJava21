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

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.ValueLayout;

/**
 * Please don't forget to --enable-preview
 * Based on https://openjdk.java.net/jeps/442
 */
public class ForeignLinkerAPIDemo {

    public static void main(String[] args) throws Throwable {

        short uid = getUid();
        System.out.println("Running with UID " + uid);

        if (isRoot(uid)) {
            System.err.println("Oh no, don't run me as root, PLEASE!!!11one");
            System.exit(42);
        }
        System.out.println("We're good to go! ;-)");

    }

    private static short getUid() throws Throwable {
        // calling https://man7.org/linux/man-pages/man2/geteuid.2.html
        var nativeLinker = Linker.nativeLinker();
        var stdlib = nativeLinker.defaultLookup();
        var getuid = stdlib.find("getuid")
            .orElseThrow(() -> new IllegalStateException("Cannot find `getuid`"));
        var funcDesc = FunctionDescriptor.of(ValueLayout.JAVA_SHORT);
        var getuidHandle = nativeLinker.downcallHandle(getuid, funcDesc);
        return (short) getuidHandle.invoke();
    }

    private static boolean isRoot(short uid) {
        return uid == 0;
    }


}
