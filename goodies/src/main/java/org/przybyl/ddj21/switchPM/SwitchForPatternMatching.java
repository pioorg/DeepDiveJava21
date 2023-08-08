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

package org.przybyl.ddj21.switchPM;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SwitchForPatternMatching {

    public static void main(String[] args) {
        switchInstructionRemains();
        switchCanHandleNulls();
        switchCanHandleDifferentTypes();
        switchCanHandleDifferentTypesWithoutDominantCases();
        switchWithPatterns();
        switchWithPatternsDominantCases();
        switchWithSealedClasses();
        cannotUsePrimitives();
        canMixEnumLabels();
        switchInstructionsHandlePatternsToo();
    }

    private static void switchInstructionRemains() {
        switch7("Switch instruction keeps working the same");
        switch7("something");
        switch7("");
//        switch7(null);

        switch14("Switch instruction keeps working the same");
        switch14("something");
        switch14("");
//        switch14(null);
    }

    private static void switch7(String input) {
        switch (input) {
            case "something":
                System.out.println("a thing!");
                break;
            case "":
                System.out.println("EMPTY");
            default:
                System.out.printf("Received [%s]%n", input);
        }
    }

    private static void switch14(String input) {
        switch (input) {
            case "something" -> System.out.println("a thing!");
            case "" -> System.out.println("EMPTY");
            default -> System.out.printf("Received [%s]%n", input);
        }
    }

    private static void switchCanHandleNulls() {
        switchInstructionWithNull(null);
        switchExpressionWithNull(null);
        switchInstructionWithNullOrDefault("fubar");
        switchInstructionWithNullOrDefault(null);
    }

    private static void switchInstructionWithNull(String input) {
        switch (input) {
            case "something":
                System.out.println("a thing!");
                break;
            case "":
                System.out.println("EMPTY");
                break;
            case null:
                System.out.println("NULL");
                break;
            default:
                System.out.printf("Received [%s]%n", input);
        }
    }

    private static void switchInstructionWithNullOrDefault(String input) {
        switch (input) {
            case "something":
                System.out.println("a thing!");
                break;
            case "":
                System.out.println("EMPTY");
                break;
            case null:
            default:
                System.out.printf("Received [%s]%n", input);
        }
    }

    private static void switchExpressionWithNull(String input) {
        String output = switch (input) {
            case null -> "NULL";
            case "something" -> "a thing!";
            case "" -> "EMPTY";
            default -> "Received [%s]%n".formatted(input);
        };
        System.out.println(output);
    }

    private static void switchCanHandleDifferentTypes() {
        switchOverClasses("hallo");
        switchOverClasses(42);
        switchOverClasses(42L);
        switchOverClasses(Instant.now());
        switchOverClasses(new double[]{5d, 7d, 73d});
    }

    private static void switchOverClasses(Object obj) {
        var res = switch (obj) {
            case String string -> "You're a String: " + string;
            case Integer integer -> "You're an Integer: " + integer;
            case Long aLong -> "You're a Long: " + aLong;
            case double[] doubleArray -> "You're a double array: " + Arrays.toString(doubleArray);
            default -> "You're something else, " + obj.getClass().getCanonicalName() + ": " + obj;
        };
        System.out.println(res);
    }

    private static void switchCanHandleDifferentTypesWithoutDominantCases() {
        switchOverClassesDominantCases("hallo");
        switchOverClassesDominantCases(42);
        switchOverClassesDominantCases(42L);
        switchOverClassesDominantCases(Instant.now());
    }

    private static void switchOverClassesDominantCases(Object obj) {
        var res = switch (obj) {
            case Serializable serializable -> "You're a Serializable: " + serializable;
//            case String string -> "You're a String: "+ string;
//            case Integer integer -> "You're an Integer: " + integer;
//            case Long aLong -> "You're a Long: " + aLong;
            default -> "You're something else, " + obj.getClass().getCanonicalName() + ": " + obj;
        };
        System.out.println(res);
    }

    private static void switchWithPatterns() {
        whoAreYou(Instant.now());
        whoAreYou("Hi");
        whoAreYou("Hello");
        whoAreYou(-2);
        whoAreYou(12);
        whoAreYou(777L);
        whoAreYou(new double[]{5d, 7d, 73d});
    }

    private static void whoAreYou(Object obj) {

//        if (obj instanceof String string && string.length() >= 3) {
//            System.out.println("You're String, at least 3 characters long!");
//        } else if (obj instanceof Integer integer && integer > 0) {
//            System.out.println("You're a positive Integer");
//        } else if (obj instanceof Long aLong && (aLong < -9 || aLong > 9)) {
//            System.out.println("You're a Long with at least two digits");
//        }

        var res = switch (obj) {
            case String string when string.length() >= 3 -> "You're a String, at least 3 characters long!";
            case Integer integer when integer > 0 -> "You're a positive Integer";
            case Long aLong when (aLong < -9 || aLong > 9) -> "You're a Long with at least two digits";
            case double[] doubleArray when (doubleArray[0] == 5.0) -> "You're a double array starting with 5.0";
            default -> "You're something else, " + obj.getClass().getCanonicalName() + ": " + obj;
        };
        System.out.println(res);
    }

    private static void switchWithPatternsDominantCases() {
        whoAreYouDominantCases(Instant.now());
        whoAreYouDominantCases("Hi");
        whoAreYouDominantCases("Hello");
        whoAreYouDominantCases(-2);
        whoAreYouDominantCases(12);
        whoAreYouDominantCases(777L);
    }

    private static void whoAreYouDominantCases(Object obj) {
        var res = switch (obj) {
            //try moving this below
            case String string -> "You're a String: " + string;
            default -> "You're something else, " + obj.getClass().getCanonicalName() + ": " + obj;
        };
        System.out.println(res);
    }

    private static void switchWithSealedClasses() {
        describeSealedClass(new FinalClass());
        describeSealedClass(new Record());
        describeSealedClass(new NonSealedClass());
    }

    private static void describeSealedClass(SealedInterface sealedInterface) {

        var description = switch (sealedInterface) {
            case FinalClass fc -> "final class";
            case Record r -> "record";
            case NonSealedClass nsc -> "non-sealed class";
//        default -> "not needed!";
        };
        System.out.println(description);
    }

    private static void cannotUsePrimitives() {
        var random = new Random();
        var oneOfPrimitives = switch (random.nextInt(3)) {
            case 0 -> 0;
            case 1 -> 1L;
            case 2 -> true;
            default -> 'd';
        };
//        var description = switch (oneOfPrimitives) {
//            case int i -> "int";
//            case long l -> "long";
//            case boolean b -> "boolean";
//            default -> "something else";
//        };

        var description = switch (oneOfPrimitives) {
            case Integer i -> "int";
            case Long l -> "long";
            case Boolean b -> "boolean";
            default -> "something else";
        };
        System.out.println(description);
    }

    private static void canMixEnumLabels() {
        enum MoveKeys {W, S, A, D}
        record ChatMsg(String msg) {
        }
        var gameEvents = List.of(MoveKeys.W, MoveKeys.S, new ChatMsg("come over here"), MoveKeys.D);
        for (Object gameEvent : gameEvents) {
            System.out.println(
                switch (gameEvent) {
                    case ChatMsg cm when cm.msg() != null -> "Said: " + cm.msg();
                    case MoveKeys.W -> "⬆";
                    case MoveKeys.S -> "⬇";
                    case MoveKeys.A -> "⬅";
                    case MoveKeys.D -> "➡";
                    default -> "";
                });

        }
    }

    private static void switchInstructionsHandlePatternsToo() {
        SealedInterface sealedInterface = new Record();
        switch (sealedInterface) {
            case FinalClass fc:
                System.out.println("final class " + fc);
                return;
            case Record r:
                System.out.println("record " + r);
                break;
            case NonSealedClass nsc:
                System.out.println("non-sealed class " + nsc);
//                break;
        }
    }

}

