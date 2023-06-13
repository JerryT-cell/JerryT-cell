package de.unisaarland.cs.se.selab.systemtest.seedfinder;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeedFinder {

    private static Logger logger = LoggerFactory.getLogger(SeedFinder.class);

    public static void findSeed(final RandomNumberBuilder builder,
            final int upperSeedBound) {
        int seed = 0;
        while (seed < upperSeedBound) {
            final var random = new Random(seed);
            boolean match = true;

            final var visitor = new EvalVisitor(random);
            final var conditions = builder.get();
            for (final var c : conditions) {
                if (!c.accept(visitor)) {
                    seed++;
                    match = false;
                    break;
                }
            }

            if (match) {
                logger.info("Found a valid seed: {}", seed);
                final var printVisitor = new PrintVisitor(seed);
                for (final var c : conditions) {
                    c.accept(printVisitor);
                }
                return;
            }
        }
        logger.info("Found no matching seed.");
    }
}
