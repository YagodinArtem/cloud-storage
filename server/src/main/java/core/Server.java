package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final Logger LOG = LogManager.getLogger(Server.class.getName());
    private final ExecutorService executorService;

    public Server() {
        executorService = Executors.newCachedThreadPool();
        try (ServerSocket socket = new ServerSocket(8181)) {
            LOG.trace("Server started");
            while (true) {
                LOG.info("Awaiting client connection");
                executorService.execute(
                        new Thread(
                                new ClientHandler(
                                        socket.accept())));
                LOG.info("Client connected");
            }
        } catch (Exception e) {
            LOG.error("Unable to open socket");
        } finally {
            // some future code
        }
    }
}
