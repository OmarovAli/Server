package org.example;

public class Main {
    private final static int ServerPort = 9999;
    private final static int threadPoolSize = 64;
    public static void main(String[] args) throws InterruptedException {
        Server server = new Server(threadPoolSize, ServerPort);
        server.start();
    }
}
