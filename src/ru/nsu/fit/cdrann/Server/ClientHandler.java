package ru.nsu.fit.cdrann.Server;

import ru.nsu.fit.cdrann.Exceptions.TooLongNameException;
import ru.nsu.fit.cdrann.Exceptions.UpLimitFileSizeException;
import ru.nsu.fit.cdrann.Exceptions.WrongReceivedDataSizeException;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private final static Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final int SIZE = 4096;
    private final int TIMEOUT = 3000;

    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    ClientHandler(Socket clientSocket) {
        logger.log(Level.INFO, "Try to configure ClientHandler..");

        try {
            this.clientSocket = clientSocket;
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }

        logger.log(Level.INFO, "Created new handler for socket port:" + clientSocket.getPort());
    }

    private File createFile(String path) {
        File filePath = new File("Uploads");
        String fileName = path.substring(path.lastIndexOf("/") + 1);

        return new File(filePath + File.separator + fileName);
    }

    private void printInstantSpeed(long time, long receivedBytes) {
        if (time < 1000) {
            time = 1000;
        }

        System.out.println("Instant speed for[" + clientSocket.getInetAddress() + "]:"
                + (long) (receivedBytes / (time / (double) 1000)) + "(bytes/s)");
    }

    private void printAverageSpeed(long time, long receivedBytes) {
        if (time < 1000) {
            time = 1000;
        }

        System.out.println("Average speed for[" + clientSocket.getInetAddress() + "]:"
                + (long) (receivedBytes / (time / (double) 1000)) + "(bytes/s)");
    }

    private void sendErrorMessage() {
        try {
            outputStream.writeUTF("Failed.");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            long fileNameSize = inputStream.readLong();
            if (fileNameSize > SIZE) {
                throw new TooLongNameException(fileNameSize);
            }

            String fileName = inputStream.readUTF();

            long fileSize = inputStream.readLong();
            if (fileSize > Math.pow(2, 40)) {
                throw new UpLimitFileSizeException(fileSize);
            }

            byte[] buff = new byte[SIZE];
            File file = createFile(fileName);

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                int bytesCurrReceived;
                long bytesCommonReceived = 0;
                long bytesInstantReceived = 0;

                long currTime;
                long startTime = System.currentTimeMillis();
                long timeBorder = System.currentTimeMillis();


                while ((bytesCurrReceived = inputStream.read(buff)) != -1) {
                    if ((currTime = System.currentTimeMillis() - timeBorder) > TIMEOUT) {
                        printAverageSpeed(System.currentTimeMillis() - startTime, bytesCommonReceived);
                        printInstantSpeed(currTime, bytesInstantReceived);

                        timeBorder = System.currentTimeMillis();
                        bytesInstantReceived = 0;
                    }

                    bytesInstantReceived += bytesCurrReceived;
                    bytesCommonReceived += bytesCurrReceived;

                    outputStream.write(buff, 0, bytesCurrReceived);

                    if (bytesCommonReceived == fileSize) {
                        break;
                    }

                    if (bytesCommonReceived > fileSize) {
                        throw new WrongReceivedDataSizeException("Received size != Size of file");
                    }
                }

                currTime = System.currentTimeMillis() - timeBorder;

                printAverageSpeed(System.currentTimeMillis() - startTime, bytesCommonReceived);
                printInstantSpeed(currTime, bytesInstantReceived);

                outputStream.flush();
            }

            outputStream.writeUTF("Success.");
        } catch (IOException| UpLimitFileSizeException | TooLongNameException | WrongReceivedDataSizeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());

            sendErrorMessage();
        }
    }
}
