package de.unisaarland.cs.se.selab.systemtest.seedfinder;

public class RandomNumber extends RandomNumberElement {

    private final int number;

    public RandomNumber(final int sides, final int number) {
        super(sides);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public <T> T accept(final RandomNumberVisitor<T> visitor) {
        return visitor.visitNumber(this);
    }
}