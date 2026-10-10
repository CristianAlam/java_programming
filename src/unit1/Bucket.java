package unit1;

/**
 * A ready-made class for Unit 1.
 * Treat it as a "black box": you only need its interface (see Tutorial.html, section 6),
 * not how it works inside. We will learn how to write classes like this in Unit 6.
 */
public class Bucket {
    private final int capacity;
    private int amount;

    public Bucket(int capacity) {
        this.capacity = capacity;
        this.amount = 0;
    }

    public void fill() {
        amount = capacity;
    }

    public void empty() {
        amount = 0;
    }

    public void pourInto(Bucket other) {
        int space = other.capacity - other.amount;
        int moved = Math.min(amount, space);
        amount -= moved;
        other.amount += moved;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentAmount() {
        return amount;
    }

    public void display() {
        System.out.println("[" + "#".repeat(amount) + ".".repeat(capacity - amount) + "] "
                + amount + "/" + capacity + " liters");
    }
}
