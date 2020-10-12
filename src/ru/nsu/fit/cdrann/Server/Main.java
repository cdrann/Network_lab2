package ru.nsu.fit.cdrann.Server;

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
            logManager.readConfiguration(new FileInputStream("C:\\Users\\Admin\\IdeaProjects\\Network_lab2\\src\\Resources\\logServer.properties"));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot get log configuration." + ex.getMessage());
        }

        if (args.length == 1) {
            logger.log(Level.INFO, "Create server..");
            Server server = new Server(Integer.parseInt(args[0]));

            logger.log(Level.INFO, "Start server..");
            server.startServer();
        } else {
            logger.log(Level.SEVERE, "No argument to create server. Please, enter port.");
            logger.log(Level.INFO, "Failed to create server.");
        }
    }

}