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

package org.przybyl.ddj21.recordPatterns;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecordPatternsDemo {

    private final static Random random = new Random();

    public static void main(String[] args) {
        var s = gimmeSomething();
        if (s instanceof Value(double v)) {
            System.out.printf("I got the value %f%n", v);
        }
        if (s instanceof Complex(Value r, Value i)) {
            System.out.printf("I got the Complex [%f, %f]%n", r.value(), i.value());
        }

        if (s instanceof Complex(Value(double r), Value(double i))) {
            System.out.printf("I got the Complex [%f, %f]%n", r, i);
        }

        switch (s) {
            case Value(double v) -> System.out.printf("I got the value %f%n", v);
//            case Complex(Value r, Value i) -> System.out.printf("I got the Complex [%f, %f]%n", r.value(), i.value());
            case Complex(Value(double r), Value(double i)) -> System.out.printf("I got the Complex [%f, %f]%n", r, i);
            default -> System.out.printf("Was ist das? %s%n", s);
        }

//        switch (gimmeGenericComplex()) {
//            case GenComplex<Number>(Integer r, Double i) -> printAll(r, i);
//            case GenComplex<Number>(Integer r, Integer i) -> printAll(r, i);
//            case GenComplex<Number>(Number r, Double i) -> printAll(r, i);
//            case GenComplex<Number>(Number r, Integer i) -> printAll(r, i);
//            case GenComplex<Number>(Number r, Number i) -> printAll(r, i);
//        }

//        switch (gimmeSealedGenericComplex()) {
//            case GenComplex<NValue>(IValue r, DValue i) -> printAll(r, i);
//            case GenComplex<NValue>(IValue r, IValue i) -> printAll(r, i);
//            case GenComplex<NValue>(NValue r, DValue i) -> printAll(r, i);
//            case GenComplex<NValue>(NValue r, IValue i) -> printAll(r, i);
//        }
    }

    public static Object gimmeSomething() {
        return switch (random.nextInt(3)) {
            case 0 -> new Value(random.nextDouble());
            case 1 -> new Complex(new Value(random.nextDouble() - 0.5), new Value(random.nextDouble() - 0.5));
            default -> "Kapusta und kwas!";
        };
    }

    public static GenComplex<Number> gimmeGenericComplex() {
        return switch (random.nextInt(4)) {
            case 0 -> new GenComplex<>(random.nextInt(), random.nextInt());
            case 1 -> new GenComplex<>(random.nextDouble(), random.nextDouble());
            case 2 -> new GenComplex<>(random.nextInt(), random.nextDouble());
            case 3 -> new GenComplex<>(random.nextDouble(), random.nextInt());
            default -> throw new UnsupportedOperationException();
        };
    }

    public static GenComplex<NValue> gimmeSealedGenericComplex() {
        return switch (random.nextInt(4)) {
            case 0 -> new GenComplex<>(new IValue(random.nextInt()), new IValue(random.nextInt()));
            case 1 -> new GenComplex<>(new DValue(random.nextDouble()), new DValue(random.nextDouble()));
            case 2 -> new GenComplex<>(new IValue(random.nextInt()), new DValue(random.nextDouble()));
            case 3 -> new GenComplex<>(new DValue(random.nextDouble()), new IValue(random.nextInt()));
            default -> throw new UnsupportedOperationException();
        };
    }

    static void printAll(Object... objects) {
        System.out.println(Stream.of(objects).map(Object::toString).collect(Collectors.joining(",")));
    }

}

record Value(double value) {
}

record Complex(Value real, Value imaginary) {
}

record GenComplex<N>(N real, N imaginary) {
}

sealed interface NValue {
}

record DValue(double value) implements NValue {
}

record IValue(int value) implements NValue {
}

