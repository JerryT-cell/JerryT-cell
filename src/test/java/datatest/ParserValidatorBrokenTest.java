package datatest;

import de.unisaarland.cs.se.selab.config.ConfigParser;
import de.unisaarland.cs.se.selab.config.ModelBuilder;
import de.unisaarland.cs.se.selab.config.ModelBuilderInterface;
import de.unisaarland.cs.se.selab.config.ModelValidator;
import de.unisaarland.cs.se.selab.model.Model;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ParserValidatorBrokenTest {
    ModelBuilderInterface<Model> builder;

    void doTest(final String path) throws IOException {
        builder = new ModelValidator<>(new ModelBuilder());
        final String jsonText = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        builder.setSeed(42);
        Assertions.assertThrows(Exception.class,
                () -> ConfigParser.parse(jsonText, builder));

    }


    @Test
    void runBrokenConfigTest() throws IOException {
        doTest("src/main/resources/config_broken.json");
    }

    @Test
    void broken2Test() throws IOException {
        doTest("src/main/resources/config_broken2.json");
    }

    @Test
    void brokenResource() throws IOException {
        doTest("src/main/resources/config_broken_resource.json");
    }

    @Test
    void brokenResource2() throws IOException {
        doTest("src/main/resources/config_broken_resource2.json");
    }

    @Test
    void brokenStructure() throws IOException {
        doTest("src/main/resources/config_broken_structure.json");
    }

    @Test
    void brokenBidding() throws IOException {
        doTest("src/main/resources/config_brokenBidding.json");
    }

    @Test
    void brokenBuff() throws IOException {
        doTest("src/main/resources/config_brokenBuff.json");
    }

    @Test
    void brokenBuff2() throws IOException {
        doTest("src/main/resources/config_brokenBuff2.json");
    }

    @Test
    void brokenRoom() throws IOException {
        doTest("src/main/resources/config_brokenRoom.json");
    }
}
