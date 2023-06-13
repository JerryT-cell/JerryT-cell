package de.unisaarland.cs.se.selab.systemtest.seedfinder;

public class ShuffleElement extends RandomNumberElement {

    public ShuffleElement(final int sides) {
        super(sides);
    }

    @Override
    public <T> T accept(final RandomNumberVisitor<T> visitor) {
        return visitor.visitShuffle(this);
    }
}
