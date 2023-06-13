package de.unisaarland.cs.se.selab;

import de.unisaarland.cs.se.selab.config.ConfigParser;
import de.unisaarland.cs.se.selab.config.ModelBuilder;
import de.unisaarland.cs.se.selab.config.ModelBuilderInterface;
import de.unisaarland.cs.se.selab.config.ModelValidator;
import de.unisaarland.cs.se.selab.model.Model;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONException;
import org.slf4j.LoggerFactory;

/**
 * The main class that starts the game server.
 */
public class Main {

    public static void main(final String[] args) {

        // Define command line parameters
        final Options options = new Options();
        addCmdLineOption(options, "<path>", "config",
                "The path to the config file from which the game should be loaded.");
        addCmdLineOption(options, "<int>", "port", "The port on which the server communicates.");
        addCmdLineOption(options, "<long>", "seed", "The seed which initializes the shuffler.");
        addCmdLineOption(options, "<int>", "timeout",
                "The servers timeout in seconds (maximal time to wait for an action of a client)");
        final CommandLineParser parser = new DefaultParser();

        // Parse values provided in args
        try {
            final CommandLine cmd = parser.parse(options, args);

            final int port = Integer.parseInt(cmd.getOptionValue("port"));
            final long seed = Long.parseLong(cmd.getOptionValue("seed"));
            final int timeout = Integer.parseInt(cmd.getOptionValue("timeout")) * 1000;


            final ModelBuilderInterface<Model> builder = new ModelValidator<>(new ModelBuilder());
            builder.setSeed(seed);

            // Parse model and start game
            try {
                final String config = Objects.requireNonNull(
                        Files.readString(Paths.get(cmd.getOptionValue("config")),
                                StandardCharsets.UTF_8));
                final ConnectionWrapper connection = new ConnectionWrapper(port, timeout, config);
                final Model model = ConfigParser.parse(config, builder);
                final Server server = new Server(model, connection);
                server.run();
                connection.close();
            } catch (final JSONException | IOException e) {
                LoggerFactory.getLogger(Main.class).info("Exception while reading config file.");
            }
        } catch (final ParseException e) {
            new HelpFormatter().printHelp("SE Labs 2022", options);
        }
    }

    private static void addCmdLineOption(final Options options, final String argName,
                                         final String longOpt,
                                         final String desc) {
        options.addOption(Option.builder()
                .required()
                .longOpt(longOpt)
                .desc(desc)
                .hasArg()
                .argName(argName)
                .build());
    }

}
