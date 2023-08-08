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
package org.przybyl.ddj21.unnamed;

import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class UnnamedPatterns {

    private final static Random random = new Random();


    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            var something = gimmeSomething();
            System.out.println(
                "Is " + something + " a positive complex? " +
                    (something instanceof Complex(Value(double r), _) && r > 0)
            );
        }

        // change this to var ;-)
        GenComplex<Number> genComplex = new GenComplex<>(2, 42);
        switch (genComplex) {
            case GenComplex(Integer _, _) -> System.out.println("unnamed pattern variable and unnamed pattern");
            case GenComplex(var real, _) -> System.out.println("real is real");
        }

        if (genComplex instanceof GenComplex<?> _) {
            System.out.println("yes, it's a complex thing");
        }

        try {
            int someMath = 1 / 0;
            System.out.println("and we got " + someMath);
        } catch (ArithmeticException _) {
            System.out.println("oh no, bad math");
        } catch (Exception _) {
            System.out.println("oh no");
        }

        Comparator<Integer> uselessComparator = (_, _) -> random.nextInt();
        var ints = new TreeSet<>(uselessComparator);
        ints.addAll(Set.of(1,2,3,4,5,6));
        System.out.println(ints.reversed());
        int weirdCounter = 0;
        for(var _ : ints){
            weirdCounter++;
        }
        System.out.println("We've got " + weirdCounter + " items");

        try (var _ = new CustomAutoCloseable()) {
            System.out.println("Can use custom autocloseable");
        } catch (Exception _) {
            System.out.println("Cannot use custom autocloseable");
        }
    }

    private static boolean positiveComplexForReal(Object something) {
        return something instanceof Complex(Value(double r), _) && r > 0;
    }

    public static Object gimmeSomething() {
        return switch (random.nextInt(3)) {
            case 0 -> new Value(random.nextDouble());
            case 1 -> new Complex(new Value(random.nextDouble() - 0.5), new Value(random.nextDouble() - 0.5));
            default -> "Kapusta und kwas!";
        };
    }
}


record Value(double value) {
}

record Complex(Value real, Value imaginary) {
}

record GenComplex<N>(N real, N imaginary) {
}

class CustomAutoCloseable implements AutoCloseable {

    @Override
    public void close() throws Exception {
        System.out.println("This is custom close in action!");
    }
}