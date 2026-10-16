package unit2;

import java.util.Scanner;
import unit1.Bucket;

/**
 * Unit 2 – Homework.
 * Solve each exercise in the place marked TODO. Run the file to check your output.
 * Exercises marked (written) are answered inside comments, not code.
 *
 * Tips:
 *  - All exercises share one main, so every variable needs its own name.
 *    Add the exercise number to your names (e.g. total3, total6) to avoid clashes.
 *  - Exercises 5-8 and 13 read input: the program waits for you to type in the Run window.
 *    To skip finished exercises, select their code and press Ctrl+/ to comment it out.
 *  - Use ONE Scanner for everything: the one called "in" created at the top of main.
 *  - Exercises 10 and 13 need helper methods: write them BELOW main, where marked.
 */
public class Homework {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        // ---------------------------------------------------------------
        // Exercise 1 (written): Trace table
        // Fill in the trace table for this code, WITHOUT running it.
        //
        //   1  int a = 7;
        //   2  int b = 2;
        //   3  int c = a / b;
        //   4  double d = a / b;
        //   5  double e = (double) a / b;
        //   6  a = a % b;
        //   7  b = b * c + a;
        //   8  System.out.println(a + " " + b + " " + c);
        //   9  System.out.println(d + " " + e);
        //
        //   Line |  a  |  b  |  c  |  d  |  e  | Output
        //   -----+-----+-----+-----+-----+-----+--------
        //    1   |     |     |     |     |     |
        //    2   |     |     |     |     |     |
        //    3   |     |     |     |     |     |
        //    4   |     |     |     |     |     |
        //    5   |     |     |     |     |     |
        //    6   |     |     |     |     |     |
        //    7   |     |     |     |     |     |
        //    8   |     |     |     |     |     |
        //    9   |     |     |     |     |     |
        //
        // Then copy the code below (rename the variables, e.g. a1, b1, ...) and run it to check.
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 1 ---");
        // TODO (paste the code here to check)


        // ---------------------------------------------------------------
        // Exercise 2 (written): What's the value?
        // For each expression, write its VALUE and its TYPE (int, double or String).
        //
        //   a) 3 + 4 * 2                         value:        type:
        //   b) (3 + 4) * 2                       value:        type:
        //   c) 17 / 5                            value:        type:
        //   d) 17 % 5                            value:        type:
        //   e) 17 / 5.0                          value:        type:
        //   f) 1 / 2 * 10.0                      value:        type:
        //   g) 10.0 * 1 / 2                      value:        type:
        //   h) "A" + 1 + 2                       value:        type:
        //   i) 1 + 2 + "A"                       value:        type:
        //   j) Math.max(4, Math.abs(-9))         value:        type:
        //   k) Math.sqrt(16) + Math.pow(2, 3)    value:        type:
        //   l) (int) 7.9                         value:        type:
        //   m) (int) Math.round(7.5)             value:        type:
        //
        // Check yourself by printing them.
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 2 ---");
        // TODO (optional: print the expressions to check)


        // ---------------------------------------------------------------
        // Exercise 3: Seconds converter
        // Given:  int totalSeconds = 7384;
        // Print it as hours, minutes and seconds, exactly like this:
        //   7384 seconds = 2 hours, 3 minutes, 4 seconds
        // Use / and %. Don't type the numbers 2, 3 or 4 yourself: calculate them.
        // Test: change totalSeconds to 59 and to 3600. Is the output still right?
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 3 ---");
        int totalSeconds = 7384;
        // TODO


        // ---------------------------------------------------------------
        // Exercise 4: Playing with digits
        // Given:  int num4 = 583;   (always a 3-digit number)
        // Print:
        //   Digits: 5 8 3
        //   Sum of digits: 16
        //   Reversed: 385
        // "Reversed" must be stored in an int variable (calculate it, don't just print the digits).
        // Test with 120. What does "Reversed" print, and why?
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 4 ---");
        int num4 = 583;
        // TODO


        // ---------------------------------------------------------------
        // Exercise 5: Temperature converter (input)
        // Read a temperature in Celsius (a decimal number) and print it in Fahrenheit.
        // Formula: F = C * 9 / 5 + 32
        //   Enter temperature in Celsius: 25
        //   25.0 C = 77.0 F
        //
        // (written) Someone wrote the formula as  9 / 5 * c + 32.
        //   What answer does it give for c = 25? Why?
        //   Your answer:
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 5 ---");
        // TODO


        // ---------------------------------------------------------------
        // Exercise 6: Average of three grades (input)
        // Read three whole-number grades. Print their exact average, and the average
        // rounded to the nearest whole number:
        //   Enter 3 grades: 90 85 81
        //   Average: 85.33333333333333
        //   Rounded: 85
        // Test with 90 85 80 (the average must be 85.0, not 85) and with 100 99 99.
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 6 ---");
        // TODO


        // ---------------------------------------------------------------
        // Exercise 7: Change machine (input)
        // Read a price and the amount paid (both whole shekels). Print the change,
        // and how many coins of each kind to give: 10, 5, 2 and 1 (largest coins first).
        //   Price: 37
        //   Paid: 100
        //   Change: 63
        //   10: 6   5: 0   2: 1   1: 1
        // Hint: after taking the 10s, what's left is  change % 10.
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 7 ---");
        // TODO


        // ---------------------------------------------------------------
        // Exercise 8: Distance between two points (input)
        // Read x1, y1, x2, y2 (decimals) and print the distance between (x1,y1) and (x2,y2),
        // rounded to 2 decimal places.
        // Formula: distance = square root of ( (x2-x1)^2 + (y2-y1)^2 )
        //   Enter x1 y1 x2 y2: 0 0 3 4
        //   Distance: 5.0
        //   Enter x1 y1 x2 y2: 1 1 2 2
        //   Distance: 1.41
        // Use Math.sqrt and Math.pow (or Math.abs if you like).
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 8 ---");
        // TODO


        // ---------------------------------------------------------------
        // Exercise 9: Rolling dice
        // Simulate rolling two dice. Print each die (1 to 6) and their sum:
        //   Die 1: 4
        //   Die 2: 6
        //   Sum: 10
        // Run it several times. Do you ever see a 0 or a 7? (You shouldn't!)
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 9 ---");
        // TODO


        // ---------------------------------------------------------------
        // Exercise 10: Helper methods for circles
        // Below main, write these three methods:
        //   - circleArea(r)       returns the area of a circle:       PI * r * r
        //   - circlePerimeter(r)  returns the perimeter of a circle:  2 * PI * r
        //   - printCircle(r)      returns nothing. Prints one line, using the two methods above:
        //       Radius 2.5: area = 19.63, perimeter = 15.71
        //     (round both to 2 decimal places)
        // Choose the right return type and parameter type for each one.
        // Then call printCircle here for the radiuses 1, 2.5 and 10.
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 10 ---");
        // TODO


        // ---------------------------------------------------------------
        // Exercise 11 (written): Find the errors
        // This code has 5 errors. For each one, write the line, what's wrong,
        // and its kind: SYNTAX, RUNTIME or LOGIC.
        //
        //   1  int x = 10
        //   2  int y = 0;
        //   3  double avg = x + y / 2;          // should be the average of x and y
        //   4  int z = x / y;
        //   5  int w = 3.5;
        //   6  System.out.println("Done: " + q);
        //
        // Errors found:
        //   1. line   kind:          problem:
        //   2. line   kind:          problem:
        //   3. line   kind:          problem:
        //   4. line   kind:          problem:
        //   5. line   kind:          problem:
        // ---------------------------------------------------------------


        // ---------------------------------------------------------------
        // Exercise 12: Buckets and percentages
        // Create a 7-liter bucket and a 4-liter bucket. Fill the 7, then pour it into the 4.
        // Then, using getCurrentAmount() and getCapacity() (don't type the numbers yourself!),
        // store the amounts in variables and print:
        //   big: 3 of 7 liters (43% full)
        //   small: 4 of 4 liters (100% full)
        //   total water: 7
        // Percentages are rounded to the nearest whole number.
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 12 ---");
        // TODO


        // ---------------------------------------------------------------
        // Exercise 13 (challenge): Pizza party (input + methods)
        // Read the number of guests and how many slices each guest eats.
        // A pizza has 8 slices and costs 45 shekels.
        // Print how many pizzas to order (rounded UP), the total cost, and how many slices are left over:
        //   Guests: 5
        //   Slices per guest: 3
        //   Pizzas: 2
        //   Cost: 90
        //   Leftover slices: 1
        // Rules:
        //   - Write at least two helper methods below main (for example: pizzasNeeded, leftover).
        //   - No if statements: round up using integer division only.
        //     Hint: think about what (slices + 7) / 8 does for 15, 16 and 17 slices.
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 13 ---");
        // TODO

    }

    // ===================================================================
    // Write your helper methods for Exercises 10 and 13 below this line.
    // ===================================================================

}
