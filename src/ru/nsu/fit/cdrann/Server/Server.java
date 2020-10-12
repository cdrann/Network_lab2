package ru.nsu.fit.cdrann.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private final static Logger logger = Logger.getLogger(Server.class.getName());
    private ServerSocket serverSocket;

    Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to create ServerSocket. " + ex.getMessage());
        }
    }

    void startServer() {
        try {
            while (true) {
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
                new Thread(clientHandler).start();
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
            }
        }
    }
}