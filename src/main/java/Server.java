import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    public final int SERVERSOCKET;
    public final int threadPoolSize = 64;
    ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

    public Server(int threadPoolSize, int serverSocket) {
        SERVERSOCKET = serverSocket;
    }

    public void start() throws InterruptedException {
        try (final var serverSocket = new ServerSocket(SERVERSOCKET)) {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                executor.execute(() -> processingOfConnection(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            executor.awaitTermination(500L, TimeUnit.MINUTES);
            executor.shutdown();
        }
    }

    private void processingOfConnection(Socket socket) {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}