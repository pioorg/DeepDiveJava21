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
package org.przybyl.ddj21.stringTemplates;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.function.BiFunction;

public class StringTemplates {
    public static void main(String[] args) {
        easyDemo();
//        moreTemplates();
//        customTemplates();
    }

    private static void easyDemo() {
        String firstName = "John";
        String lastName = "Doe";
        String middleName = "M.";
        Instant born = Instant.EPOCH;
        var utc = ZoneId.of("UTC");
        var _duration = Duration.ofSeconds(1);

        String intro = STR."""
            Full name: \{firstName} \{middleName} \{lastName}
            Born in: \{born.atZone(ZoneId.of("UTC")).getYear()}
            (Exactly \{Duration.between(born, Instant.now()).getSeconds()} seconds ago)""";

        System.out.println(intro);

        String deeper = "deeper";
        String goDeeper = STR."go \{deeper}";
        System.out.println(STR."We have to \{goDeeper}...");
        System.out.println(STR."We have to \{STR."go \{
            deeper
        } again "}...");

        String json = STR."""
            {
              "fullName" : "\{firstName} \{middleName} \{lastName}",
              "yearOfBirth" : \{born.atZone(ZoneId.of("UTC")).getYear()}
            }
            """;
        System.out.println(json);

        System.out.println(java.util.FormatProcessor.FMT."""
            Formatted year: %tY\{born.atZone(ZoneId.of("UTC"))}
            """);
    }

    private static void moreTemplates() {
        int x = 0, y = 0;
        StringTemplate rawStringTemplate = java.lang.StringTemplate.RAW."\{x} x \{y} = \{x*y}";
        System.out.println(rawStringTemplate.fragments());
        System.out.println(rawStringTemplate.values());
        String strString = java.lang.StringTemplate.STR."\{x} x \{y} = \{x*y}";


        for (x = 0; x < 3; x++) {
            for (y = 0; y < 3; y++) {
//                rawStringTemplate = java.lang.StringTemplate.RAW."\{x} x \{y} = \{x*y}";
                System.out.println(rawStringTemplate);
                System.out.println(rawStringTemplate.interpolate());
            }
        }
    }

    private static void customTemplates() {
        record Op(BiFunction<Integer, Integer, Integer> fun, String rep) {}
        var BASIC_MATH = StringTemplate.Processor.of(
            (StringTemplate st) -> {
                if (st.values().size() % 3 != 0) {
                    throw new IllegalArgumentException("Expected simple math operations only, with 3 parts each.");
                }
                var stringBuilder = new StringBuilder();
                var fragmentsIterator = st.fragments().iterator();
                int i = 0;
                Op op = null;
                Integer n1 = null, n2 = null;
                for (var value : st.values()) {
                    if ((i % 3 == 0 || (i - 2) % 3 == 0) && value instanceof Integer number) {
                        if (n1 == null) {
                            n1 = number;
                        } else {
                            n2 = number;
                        }
                    } else if ((i - 1) % 3 == 0 && value instanceof Op givenOp) {
                        op = givenOp;
                    } else {
                        throw new IllegalArgumentException("Expected number or operation in a [number, op, number] order");
                    }
                    if ((i - 2) % 3 == 0) {
                        stringBuilder
                            .append(fragmentsIterator.next())
                            .append(n1)
                            .append(fragmentsIterator.next())
                            .append(op.rep)
                            .append(fragmentsIterator.next())
                            .append(n2)
                            .append(" = ")
                            .append(op.fun().apply(n1, n2));
                        n1 = n2 = null;
                    }
                    i++;
                }
                stringBuilder.append(fragmentsIterator.next());
                return stringBuilder.toString();
            });

        Op multiply = new Op((a, b) -> a * b, "x");
        Op add = new Op(Integer::sum, "+");
        Op sub = new Op((a, b) -> a - b, "-");
        String poorMT = BASIC_MATH."""
            \{12} \{add} \{7}, therefore \{19} \{sub} \{7}.
            Moreover, \{2} \{multiply} \{3}, and of course \{3} \{multiply} \{2}.
            What else have you expected?
            """;
        System.out.println(poorMT);
    }

}
