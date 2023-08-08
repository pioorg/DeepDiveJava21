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


/*
####################################################################
 Call this file with java --source 21 --enable-preview NoClass.java
####################################################################
*/


//class NoClass {

//    public static void main(String[]args){
//        System.out.println("Classless public static main, with "+args.length+" arg(s).");
//    }

//    public static void main() {
//        System.out.println("New public static main in unnamed class, without arg(s).");
//    }

//    public void main(String ... args) {
//        System.out.println("New public instance main in unnamed class, with " +args.length + " arg(s).");
//    }

//    public void main() {
//        System.out.println("New public instance main in unnamed class, without arg(s).");
//    }

    void main() {
        System.out.println("New instance main in unnamed class, without arg(s).");
        System.out.println("Is the class unnamed? " + this.getClass().isUnnamedClass());
        System.out.println("The class name is " + this.getClass().getName() + "...?");
        System.out.println("And the modifiers are [" + java.lang.reflect.Modifier.toString(this.getClass().getModifiers())+"]");
        System.out.println("(The package name is [" +this.getClass().getPackage().getName()+"])");
    }

//    private void main() {
//        System.out.println("New private instance main in unnamed class, without arg(s).");
//    }

//}
