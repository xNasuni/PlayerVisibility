package dev.xnasuni.playervisibility.util;

import dev.xnasuni.playervisibility.PlayerVisibility;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ConfigUtil {
    private static final File MainFile = new File(PlayerVisibility.ConfigDirectory.resolve("whitelisted-players.txt").toUri());

    public static String[] Load() throws IOException {
        String[] WhitelistedPlayers;
        if (MainFile.canRead()) {
            try {
                List<String> AllLines = Files.readAllLines(Paths.get(MainFile.toURI()));
                WhitelistedPlayers = AllLines.toArray(new String[0]);
            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
        } else {
            throw new IOException("Cannot read file");
        }
        return WhitelistedPlayers;
    }

    public static void Save(String[] WhitelistedPlayers) throws IOException {
        String WhitelistedPlayersString = String.join("\n", WhitelistedPlayers);
        Files.write(MainFile.toPath(), WhitelistedPlayersString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
