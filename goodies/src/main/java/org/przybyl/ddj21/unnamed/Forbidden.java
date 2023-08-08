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


public class Forbidden {
//    private String _ = "this is forbidden";
//
//    private void forbidden(int _) {
//    }

    private String __ = "this still works";

    private void stillWorks(int __) {
    }

    private void nameIt(Object o) {
        switch (o) {
//            case var _ -> System.out.println("literally anything");
//            case _ -> System.out.println("literally anything");
            case Object _ -> System.out.println("this is okay");
        }
    }
}
