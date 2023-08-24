package org.example;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private final ExecutorService executor;
    private final int socket;

    public Server(int threadPoolSize, int serverSocket) {
        socket = serverSocket;
        executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void start() throws InterruptedException {
        try (final var serverSocket = new ServerSocket(socket)) {
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
            // read only request line for simplicity //строка запроса только для чтения, для простоты
            // must be in form GET /path HTTP/1.1 //должен быть в формате GET /путь HTTP/1.1
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                socket.close();
                return;
            }

            String method = parts[0];
            final String path = parts[1];
            Request request = new Request(method, path);

            if (request == null) {
                connectionResponseUnCorrect(out, "404", "Not found");
            } else {
                connectionResponseCorrect(out, path);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    protected void connectionResponseUnCorrect(BufferedOutputStream out, String responseCode, String responseStatus) throws IOException {
        out.write((
                "HTTP/1.1 " + responseCode + " " + responseStatus + "\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }
    protected void connectionResponseCorrect(BufferedOutputStream out, String path) throws IOException {
        final var filePath = Path.of(".", "public", path);
        final var mimeType = Files.probeContentType(filePath);

        final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }
}