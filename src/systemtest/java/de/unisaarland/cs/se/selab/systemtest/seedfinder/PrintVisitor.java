package de.unisaarland.cs.se.selab.systemtest.seedfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.slf4j.LoggerFactory;

public class PrintVisitor implements RandomNumberVisitor<Void> {

    private final Random random;

    public PrintVisitor(final int seed) {
        this.random = new Random(seed);
    }

    @Override
    public Void visitNumber(final RandomNumber randomNumber) {
        final var s = randomNumber.getSides();
        final var r = random.nextInt(s);

        LoggerFactory.getLogger(this.getClass())
                .info("Number:  size %d, rolled %d".formatted(s, r));
        return null;
    }

    @Override
    public Void visitEmpty(final EmptyRandomNumber emptyRandomNumber) {
        final var s = emptyRandomNumber.getSides();
        final var r = random.nextInt(s);

        LoggerFactory.getLogger(this.getClass())
                .info("Empty:   size %d, rolled %d".formatted(s, r));
        return null;
    }

    @Override
    public Void visitShuffle(final ShuffleElement shuffleElement) {
        final var list = new ArrayList<Integer>();
        for (int i = 0; i < shuffleElement.getSides(); i++) {
            list.add(i);
        }

        Collections.shuffle(list, random);

        LoggerFactory.getLogger(this.getClass()).info("Shuffle: size {}, {}",
                list.size(), list);
        return null;
    }

    @Override
    public Void visitRange(final RandomNumberRange randomNumberRange) {
        final var s = randomNumberRange.getSides();
        final var r = random.nextInt(s);

        LoggerFactory.getLogger(this.getClass()).info("Range:   %d to %d, rolled %d".formatted(
                randomNumberRange.getStart(), randomNumberRange.getEnd(), r));
        return null;
    }

    @Override
    public Void visitRandomNumberNotEqual(final RandomNumberNotEquals randomNumberNotEquals) {
        final var s = randomNumberNotEquals.getSides();
        final var r = random.nextInt(s);

        LoggerFactory.getLogger(this.getClass())
                .info("Not Eq:  sides %d, rolled %d".formatted(s, r));
        return null;
    }
}
