package ru.nsu.fit.cdrann.Client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    private final static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LogManager logManager = LogManager.getLogManager();
        try {
            logManager.readConfiguration(new FileInputStream("C:\\Users\\Admin\\IdeaProjects\\Network_lab2\\src\\Resources\\logClient.properties"));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot get log configuration." + ex.getMessage());
        }

        if (args.length == 3) {
            Client client = new Client(args[0], args[1], Integer.parseInt(args[2]));
            client.startSendReceive();
        } else {
            logger.log(Level.SEVERE, "Required 3 args:\n" +
                    "1. file name\n" +
                    "2. server IP\n" +
                    "3. server port\n" +
                    "Try again.\n");
        }
    }
}