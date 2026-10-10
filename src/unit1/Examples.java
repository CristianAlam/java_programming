package unit1;

/**
 * Unit 1 – Examples.
 * Run this file and compare each block of code with the output it produces.
 * Tip: set a breakpoint on the first line of main and step through with F8 (Debug mode).
 */
public class Examples {
    public static void main(String[] args) {

        // ===== Example 1: println vs print =====
        System.out.println("=== Example 1: println vs print ===");
        System.out.println("println moves to a new line after printing.");
        System.out.print("print stays ");
        System.out.print("on the ");
        System.out.print("same line.");
        System.out.println();   // an empty println just ends the line
        System.out.println();

        // ===== Example 2: commands run top to bottom =====
        System.out.println("=== Example 2: order matters ===");
        System.out.println("1. Wake up");
        System.out.println("2. Brush teeth");
        System.out.println("3. Go to school");
        System.out.println();

        // ===== Example 3: drawing with text =====
        System.out.println("=== Example 3: a text picture ===");
        System.out.println("    /\\");      // "\\" prints a single backslash
        System.out.println("   /  \\");
        System.out.println("  /____\\");
        System.out.println("  |    |");
        System.out.println("  | [] |");
        System.out.println("  |____|");
        System.out.println();

        // ===== Example 4: special characters inside text =====
        System.out.println("=== Example 4: escape sequences ===");
        System.out.println("She said \"Hello!\"");   // \" prints a quote
        System.out.println("Line one\nLine two");     // \n starts a new line
        System.out.println("Name:\tDana");            // \t inserts a tab
        System.out.println();

        // ===== Example 5: creating objects and calling methods =====
        System.out.println("=== Example 5: first objects ===");
        Bucket b = new Bucket(4);    // create an empty 4-liter bucket
        b.display();                 // [....] 0/4 liters
        b.fill();
        b.display();                 // [####] 4/4 liters
        b.empty();
        b.display();                 // [....] 0/4 liters
        System.out.println();

        // ===== Example 6: two objects of the same class are independent =====
        System.out.println("=== Example 6: two separate objects ===");
        Bucket red = new Bucket(2);
        Bucket blue = new Bucket(6);
        red.fill();                  // only red changes
        red.display();
        blue.display();
        System.out.println();

        // ===== Example 7: objects interacting =====
        System.out.println("=== Example 7: pouring between buckets ===");
        Bucket big = new Bucket(5);
        Bucket small = new Bucket(3);
        big.fill();
        big.pourInto(small);         // only 3 liters fit, so 2 stay in big
        big.display();               // [##...] 2/5 liters
        small.display();             // [###] 3/3 liters
        System.out.println();

        // ===== Example 8: printing a value a method returns =====
        System.out.println("=== Example 8: asking an object a question ===");
        System.out.print("Liters left in big: ");
        System.out.println(big.getCurrentAmount());
        System.out.print("Capacity of small: ");
        System.out.println(small.getCapacity());
        System.out.println();

        // ===== Example 9: from algorithm to program =====
        // Task: using a 5-liter and a 3-liter bucket, end up with exactly 2 liters.
        // Algorithm in words:
        //   1. Fill the 5-liter bucket.
        //   2. Pour from the 5-liter bucket into the 3-liter bucket.
        //   3. Now 2 liters are left in the 5-liter bucket. Done!
        System.out.println("=== Example 9: algorithm -> program ===");
        Bucket five = new Bucket(5);
        Bucket three = new Bucket(3);
        five.fill();             // step 1
        five.pourInto(three);    // step 2
        five.display();          // step 3: check the result
    }
}
