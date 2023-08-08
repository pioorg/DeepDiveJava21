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
package org.przybyl.ddj21.concurrency.scopedValues;


import java.util.Random;
import java.util.concurrent.StructuredTaskScope;

// Don't forget ot use --enable-preview
public class ScopedValuesDemo {

    static final ScopedValue<Integer> SECURITY_CLEARANCE_LEVEL = ScopedValue.newInstance();

    public static void main(String[] args) {

        int level = levelFromRequest();

        var voyager = new Starship();

        ScopedValue.where(SECURITY_CLEARANCE_LEVEL, level)
            .run(() -> {
                doSomeStuffOn(voyager);

                System.out.println(" -- let's jail current character");
                ScopedValue.where(SECURITY_CLEARANCE_LEVEL, levelForPrisoner())
                    .run(() -> doSomeStuffOn(voyager));

                System.out.println(" -- and let's try again");
                doSomeStuffOn(voyager);

            });

    }

    private static void doSomeStuffOn(Starship starship) {
        System.out.println("Acting with security clearance level " + SECURITY_CLEARANCE_LEVEL.orElse(-1));
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(starship::turnLightsOn);
            scope.fork(() -> starship.locateCrewMember("Tuvok"));
            scope.fork(starship::blockBridgeControls);
            scope.join();
        } catch (InterruptedException ie) {
            // please keep in mind this is demo only, don't do that at home!
            throw new RuntimeException(ie);
        }
    }

    private static int levelFromRequest() {
        return new Random().nextInt(10);
    }

    private static int levelForPrisoner() {
        return 0;
    }

    static class Starship {
        Random deckGen = new Random();

        boolean turnLightsOn() {
            System.out.println("Lights on");
            return true;
        }

        boolean blockBridgeControls() {
            if (SECURITY_CLEARANCE_LEVEL.isBound() && SECURITY_CLEARANCE_LEVEL.get() >= 10) {
                System.out.println("Bridge controls blocked");
                return true;
            } else {
                System.out.println("Insufficient security level");
                return false;
            }
        }

        boolean locateCrewMember(String name) {
            if (SECURITY_CLEARANCE_LEVEL.isBound() && SECURITY_CLEARANCE_LEVEL.get() >= 2) {
                System.out.println("Crew member " + name + " is on deck " + deckGen.nextInt(1, 17));
                return true;
            } else {
                System.out.println("Insufficient security level");
                return false;
            }
        }
    }
}
