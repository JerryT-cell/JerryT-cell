package de.unisaarland.cs.se.selab.systemtest.seedfinder;

public class Main {
    public static void main(final String[] args) {
        final var b = new RandomNumberBuilder();
        b.addShuffle(12);
        b.addShuffle(3);
        b.addShuffle(16);
        b.addShuffle(8);
        b.addShuffle(12);
        b.addEmpty(25); //first tunnel evaluation -> no worry
        b.addNumber(25, 0); //second tunnel counter spell found
        b.addEmpty(25);
        b.addNumber(25, 1); // second tunnel counter spell
        SeedFinder.findSeed(b, 1_000_000);
    }
}
