package unit1;

/**
 * Unit 1 – Homework solutions.
 * Try every exercise in Homework.java yourself first!
 * Many exercises have more than one correct answer; these are just examples.
 */
public class HomeworkSolutions {
    public static void main(String[] args) {

        // ---------------------------------------------------------------
        // Exercise 1: Name card
        // Tip: count the spaces so the right-hand * lines up with the frame.
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 1 ---");
        System.out.println("**********");
        System.out.println("* Dana   *");
        System.out.println("**********");


        // ---------------------------------------------------------------
        // Exercise 2: Big initials (example: the letter C)
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 2 ---");
        System.out.println(" ####");
        System.out.println("#");
        System.out.println("#");
        System.out.println("#");
        System.out.println(" ####");


        // ---------------------------------------------------------------
        // Exercise 3: print vs println
        // \n starts a new line, so one print command is enough.
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 3 ---");
        System.out.print("Java\nis\nfun\n");


        // ---------------------------------------------------------------
        // Exercise 4 (written): Read a program
        // Answer:
        //   AB
        //   CD
        //   E
        //   F
        // Why: print("A") stays on the line, so "B" joins it, then println moves down.
        //      "C" and "D" share a line, and the empty println() ends it.
        //      "E\nF" prints E, then \n moves to a new line, then F.
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 4 ---");
        System.out.print("A");
        System.out.println("B");
        System.out.print("C");
        System.out.print("D");
        System.out.println();
        System.out.println("E\nF");


        // ---------------------------------------------------------------
        // Exercise 5 (written): Find the bugs
        // Errors found:
        //   1. system  ->  System             (Java is case-sensitive)
        //   2. "Good morning   is missing its closing quote  "
        //   3. "Have a nice day")   is missing ; at the end
        //   4. printn  ->  println            (spelling mistake)
        //   5. The class is missing its closing }   (braces come in pairs)
        //
        // Fixed version:
        //   public class Greeting {
        //       public static void main(String[] args) {
        //           System.out.println("Good morning");
        //           System.out.println("Have a nice day");
        //           System.out.println("Bye!");
        //       }
        //   }
        // ---------------------------------------------------------------


        // ---------------------------------------------------------------
        // Exercise 6 (written): Algorithm in plain words
        // Example answer for (a) Making a cheese sandwich:
        //   1. Take a plate and put it on the table.
        //   2. Take two slices of bread and put them on the plate.
        //   3. Take a knife and spread butter on one side of each slice.
        //   4. Put two slices of cheese on the buttered side of one slice.
        //   5. Put the second slice on top, buttered side down.
        //   6. Cut the sandwich in half with the knife.
        // A good answer is precise (says exactly what to do) and in the right order.
        // ---------------------------------------------------------------


        // ---------------------------------------------------------------
        // Exercise 7: Bucket tracing
        // Answers:
        //   x.display()  ->  [####] 4/4 liters      (only 4 of y's 6 liters fit in x)
        //   y.display()  ->  [##....] 2/6 liters    (2 liters stay behind)
        //   x.display()  ->  [##..] 2/4 liters      (x was emptied, then got y's 2 liters)
        //   y.display()  ->  [......] 0/6 liters    (y poured everything it had)
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 7 ---");
        Bucket x = new Bucket(4);
        Bucket y = new Bucket(6);
        y.fill();
        y.pourInto(x);
        x.display();
        y.display();
        x.empty();
        y.pourInto(x);
        x.display();
        y.display();


        // ---------------------------------------------------------------
        // Exercise 8: The water puzzle (5 and 3 -> exactly 4 liters)
        // Algorithm:
        //   1. Fill the 5-liter bucket.                    5: 5   3: 0
        //   2. Pour the 5 into the 3.                      5: 2   3: 3
        //   3. Empty the 3.                                5: 2   3: 0
        //   4. Pour the 5 into the 3.                      5: 0   3: 2
        //   5. Fill the 5 again.                           5: 5   3: 2
        //   6. Pour the 5 into the 3 (only 1 liter fits).  5: 4   3: 3   Done!
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 8 ---");
        Bucket big8 = new Bucket(5);
        Bucket small8 = new Bucket(3);

        big8.fill();              // step 1
        big8.display();
        small8.display();

        big8.pourInto(small8);    // step 2
        big8.display();
        small8.display();

        small8.empty();           // step 3
        big8.display();
        small8.display();

        big8.pourInto(small8);    // step 4
        big8.display();
        small8.display();

        big8.fill();              // step 5
        big8.display();
        small8.display();

        big8.pourInto(small8);    // step 6
        big8.display();           // [####.] 4/5 liters
        small8.display();


        // ---------------------------------------------------------------
        // Exercise 9 (challenge): 9 and 4 -> exactly 6 liters
        // Algorithm:
        //   1. Fill the 9.                                 9: 9   4: 0
        //   2. Pour the 9 into the 4.                      9: 5   4: 4
        //   3. Empty the 4.                                9: 5   4: 0
        //   4. Pour the 9 into the 4.                      9: 1   4: 4
        //   5. Empty the 4.                                9: 1   4: 0
        //   6. Pour the 9 into the 4.                      9: 0   4: 1
        //   7. Fill the 9.                                 9: 9   4: 1
        //   8. Pour the 9 into the 4 (only 3 liters fit).  9: 6   4: 4   Done!
        // ---------------------------------------------------------------
        System.out.println("--- Exercise 9 ---");
        Bucket big9 = new Bucket(9);
        Bucket small9 = new Bucket(4);
        big9.fill();              // step 1
        big9.pourInto(small9);    // step 2
        small9.empty();           // step 3
        big9.pourInto(small9);    // step 4
        small9.empty();           // step 5
        big9.pourInto(small9);    // step 6
        big9.fill();              // step 7
        big9.pourInto(small9);    // step 8
        System.out.print("Amount: ");
        System.out.println(big9.getCurrentAmount());
    }
}
