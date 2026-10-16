package unit2;

import java.util.Scanner;
import unit1.Bucket;

/**
 * Unit 2 – Examples.
 * Run this file and compare each block of code with the output it produces.
 * Example 13 (the last one) waits for you to type input in the Run window.
 * Tip: set a breakpoint and step with F8 (Debug mode) to watch the Variables panel change.
 */
public class Examples {
    public static void main(String[] args) {

        // ===== Example 1: creating variables =====
        System.out.println("=== Example 1: variables ===");
        int age = 16;              // a box for a whole number
        double height = 1.72;      // a box for a decimal number
        System.out.println(age);
        System.out.println(height);
        System.out.println("age");  // in quotes: the TEXT "age", not the variable
        System.out.println();

        // ===== Example 2: assignment copies; swapping =====
        System.out.println("=== Example 2: assignment and swap ===");
        int a = 5;
        int b = 8;
        int copy = a;              // copy gets 5
        a = 100;                   // changing a doesn't change copy
        System.out.println("a = " + a + ", copy = " + copy);

        a = 5;
        int temp = a;              // swap a and b using a temporary box
        a = b;
        b = temp;
        System.out.println("After swap: a = " + a + ", b = " + b);

        int counter = 0;
        counter = counter + 1;     // take the old value, add 1, store it back
        counter += 5;              // shortcut for counter = counter + 5
        counter++;                 // shortcut for counter = counter + 1
        System.out.println("counter = " + counter);
        System.out.println();

        // ===== Example 3: arithmetic and order of operations =====
        System.out.println("=== Example 3: arithmetic ===");
        System.out.println(2 + 3 * 4);       // 14: * before +
        System.out.println((2 + 3) * 4);     // 20: parentheses first
        System.out.println(20 / 4 * 2);      // 10: left to right
        int x = 10, y = 20;
        System.out.println("Wrong average: " + (x + y / 2));     // 20: y / 2 happens first
        System.out.println("Right average: " + ((x + y) / 2));   // 15
        System.out.println();

        // ===== Example 4: integer division and remainder =====
        System.out.println("=== Example 4: / and % ===");
        System.out.println("17 / 5 = " + (17 / 5));   // 3
        System.out.println("17 % 5 = " + (17 % 5));   // 2

        int totalMinutes = 135;
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        System.out.println(totalMinutes + " minutes = " + hours + " h " + minutes + " min");

        int num = 472;
        System.out.println("units: " + (num % 10) + ", tens: " + (num / 10 % 10) + ", hundreds: " + (num / 100));
        System.out.println("Is 7 odd? remainder of 7 % 2 = " + (7 % 2));   // 1 means odd
        System.out.println();

        // ===== Example 5: int vs double, casting =====
        System.out.println("=== Example 5: int vs double ===");
        System.out.println(7 / 2);      // 3   (int / int)
        System.out.println(7.0 / 2);    // 3.5 (double / int)
        int total = 17, count = 4;
        double wrongAvg = total / count;            // 4.0: int division happened first
        double rightAvg = (double) total / count;   // 4.25
        System.out.println("wrong: " + wrongAvg + ", right: " + rightAvg);
        double d = 5;                   // int -> double happens automatically
        int cut = (int) 9.99;           // double -> int needs a cast, and it CUTS (no rounding)
        System.out.println("d = " + d + ", cut = " + cut);
        System.out.println();

        // ===== Example 6: limits of int and double =====
        System.out.println("=== Example 6: limits ===");
        int big = 2147483647;           // the largest int
        System.out.println("big + 1 = " + (big + 1));   // wraps around to the most negative int!
        System.out.println("0.1 + 0.2 = " + (0.1 + 0.2));   // not exactly 0.3
        System.out.println();

        // ===== Example 7: the Math library =====
        System.out.println("=== Example 7: Math ===");
        System.out.println("abs(-7)    = " + Math.abs(-7));
        System.out.println("max(3, 9)  = " + Math.max(3, 9));
        System.out.println("min(3, 9)  = " + Math.min(3, 9));
        System.out.println("pow(2, 10) = " + Math.pow(2, 10));
        System.out.println("sqrt(25)   = " + Math.sqrt(25));
        System.out.println("round(2.5) = " + Math.round(2.5));
        System.out.println("PI         = " + Math.PI);
        double hyp = Math.sqrt(Math.pow(3, 2) + Math.pow(4, 2));
        System.out.println("hypotenuse of 3 and 4 = " + hyp);
        int largest = Math.max(4, Math.max(11, 7));    // a call inside a call
        System.out.println("largest of 4, 11, 7 = " + largest);
        double twoPlaces = Math.round(Math.PI * 100) / 100.0;
        System.out.println("PI to 2 places = " + twoPlaces);
        int grade = (int) Math.round(84.6);            // round returns a long, so cast to int
        System.out.println("84.6 rounded = " + grade);
        System.out.println();

        // ===== Example 8: random numbers =====
        System.out.println("=== Example 8: random (run again to get new numbers) ===");
        double r = Math.random();                      // 0.0 up to (not including) 1.0
        int die = (int) (Math.random() * 6) + 1;       // 1 to 6
        int lotto = (int) (Math.random() * 37) + 1;    // 1 to 37
        System.out.println("random: " + r);
        System.out.println("die: " + die + ", lotto ball: " + lotto);
        System.out.println();

        // ===== Example 9: String and concatenation =====
        System.out.println("=== Example 9: Strings ===");
        String name = "Dana";
        System.out.println("Hi " + name + ", you are " + age + ".");
        System.out.println("Sum: " + 3 + 4);       // Sum: 34   (text, then text)
        System.out.println("Sum: " + (3 + 4));     // Sum: 7    (parentheses first)
        System.out.println(3 + 4 + " is the sum"); // 7 is the sum
        System.out.println();

        // ===== Example 10: helper methods =====
        System.out.println("=== Example 10: helper methods ===");
        double area = circleArea(2.5);             // the returned value goes into area
        System.out.println("Area of radius 2.5: " + area);
        printLine();
        System.out.println("Area of radius 1, doubled: " + circleArea(1) * 2);   // used inside an expression
        System.out.println("Average of 7 and 8: " + average(7, 8));
        System.out.println("Pizzas for 10 guests: " + pizzasNeeded(10 * 3));
        System.out.println();

        // ===== Example 11: call by value =====
        System.out.println("=== Example 11: call by value ===");
        int n = 3;
        int sq = square(n);                        // the VALUE 3 is copied into the parameter
        System.out.println("n = " + n + ", square = " + sq);   // n is still 3
        System.out.println();

        // ===== Example 12: objects + variables =====
        System.out.println("=== Example 12: Bucket + variables ===");
        Bucket bigBucket = new Bucket(7);
        Bucket smallBucket = new Bucket(3);
        bigBucket.fill();
        bigBucket.pourInto(smallBucket);
        int left = bigBucket.getCurrentAmount();                 // store the returned value
        double percent = 100.0 * left / bigBucket.getCapacity(); // calculate with it
        System.out.println("big has " + left + " liters, " + Math.round(percent) + "% full");
        int together = bigBucket.getCurrentAmount() + smallBucket.getCurrentAmount();
        System.out.println("total water: " + together);
        System.out.println();

        // ===== Example 13: input with Scanner =====
        System.out.println("=== Example 13: input (type in the Run window, then press Enter) ===");
        Scanner in = new Scanner(System.in);
        System.out.print("How old are you? ");
        int userAge = in.nextInt();
        System.out.print("Your height in meters (e.g. 1.65)? ");
        double userHeight = in.nextDouble();
        System.out.println("In 10 years you will be " + (userAge + 10) + ".");
        System.out.println("Your height in cm: " + (userHeight * 100));
    }

    // Returns the area of a circle with the given radius.
    private static double circleArea(double radius) {
        return Math.PI * radius * radius;
    }

    // Prints a separator line. Returns nothing.
    private static void printLine() {
        System.out.println("--------------------");
    }

    // Returns the average of two whole numbers, as a decimal.
    private static double average(int a, int b) {
        return (a + b) / 2.0;     // 2.0 makes it double division
    }

    // Returns how many 8-slice pizzas are needed for the given number of slices (rounded up).
    private static int pizzasNeeded(int slices) {
        return (slices + 7) / 8;
    }

    // Changes its own copy of n only. The caller's variable is not affected.
    private static int square(int n) {
        n = n * n;
        return n;
    }
}
