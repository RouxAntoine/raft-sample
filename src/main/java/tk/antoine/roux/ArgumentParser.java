package tk.antoine.roux;

import tk.antoine.roux.exception.InvalidFlagFormatException;
import tk.antoine.roux.node.LocalPeerDefinition;
import tk.antoine.roux.node.PeerDefinition;
import tk.antoine.roux.sockets.CommandLineArgumentType;

import java.lang.invoke.MethodHandles;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentParser {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final String PEERS_SEPARATOR = ",";
    private static final String ARGUMENTS_SEPARATOR = "=";

    public LocalPeerDefinition parse(String[] args) throws InvalidFlagFormatException, UnknownHostException {
        LocalPeerDefinition definition = new LocalPeerDefinition();

        for (String arg : args) {
            String[] splitArg = arg.split(ARGUMENTS_SEPARATOR);
            CommandLineArgumentType type = CommandLineArgumentType.valueOf(removeDashPrefix(splitArg[0]).toUpperCase());
            String value = splitArg[1];

            switch (type) {
                case ID -> definition.setId(value);
                case PEERS -> parsePeersArgument(definition, value);
            }
        }

        return definition;
    }

    private void parsePeersArgument(LocalPeerDefinition definition, String value) throws UnknownHostException {
        String[] peers = value.split(PEERS_SEPARATOR);
        for (int i = 0; i < peers.length; i++) {
            String peer = peers[i];
            PeerDefinition peerDefinition = new PeerDefinition(peer.trim());
            if(i == 0) {
                definition.setSelf(peerDefinition);
            } else {
                definition.addPeer(peerDefinition);
            }
        }
    }

    private String removeDashPrefix(String stringWithDash) throws InvalidFlagFormatException {
        Pattern dashPrefix = Pattern.compile("--(.*)");
        Matcher matcher = dashPrefix.matcher(stringWithDash);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        LOGGER.severe(String.format("bad flag format %s, expecting to match pattern --(.*)", stringWithDash));
        throw new InvalidFlagFormatException();
    }
}
