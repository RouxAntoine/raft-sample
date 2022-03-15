package tk.antoine.roux;

import tk.antoine.roux.exception.InvalidFlagFormatException;
import tk.antoine.roux.node.LocalPeerDefinition;
import tk.antoine.roux.node.Peer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    public static final String LOGGING_PROPERTIES_FILE = "logging.properties";
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static void main(String[] args) throws IOException, InvalidFlagFormatException, InterruptedException {
        configureLogger();
        LOGGER.info("Welcome 👋");

        ArgumentParser argumentParser = new ArgumentParser();
        LocalPeerDefinition definition = argumentParser.parse(args);
        if (definition.isValid()) {
            Peer peer = new Peer(definition);
            Thread reception = peer.listen();
            Thread election = peer.quorum();

            reception.join();
            peer.stop();
            election.join();
        }

        /*
         targetIP = new HTTPCaller().call("https://ifconfig.me/ip");
         LOGGER.info(String.format("current public ip is : %s",  targetIP));
         asyncLogString(List.of("toto", "titi"));
        */

        LOGGER.config("Bye 🥺");
    }

//    private static void asyncLogString(List<String> strings) {
//        Flux<String> stringFlux = Flux.fromIterable(strings);
//        stringFlux
//                .publishOn(Schedulers.boundedElastic())
//                .handle((String name, SynchronousSink<String> sink) -> {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        sink.error(e);
//                    }
//                    LOGGER.info(name);
//                    sink.next(name);
//                })
//                .blockLast();
//    }

    private static void configureLogger() throws IOException {
        InputStream resource = MethodHandles.lookup().lookupClass().getClassLoader().getResourceAsStream(LOGGING_PROPERTIES_FILE);

        if (resource != null) {
            LogManager.getLogManager().readConfiguration(resource);
        }
    }
}
