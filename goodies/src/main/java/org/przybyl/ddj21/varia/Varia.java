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
package org.przybyl.ddj21.varia;


// run as e.g.
// java org/przybyl/ddj21/varia/Varia.java
public class Varia {
    public static void main(String[] args) {
        String test = "\uD83E\uDD26\uFE0F";
        for (char c : test.toCharArray()) {
            var emoji = Character.isEmoji(c);
            var component = Character.isEmojiComponent(c);
            var modifier = Character.isEmojiModifier(c);
            var modifierBase = Character.isEmojiModifierBase(c);
            var presentation = Character.isEmojiPresentation(c);
            System.out.println("ENDE!");
        }
    }

    // consider reading https://www.smashingmagazine.com/2016/11/character-sets-encoding-emoji/

}
