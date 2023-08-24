package org.example;

import java.util.List;

public class Main {
    private final static int ServerPort = 9999;
    private final static int threadPoolSize = 64;
    public static void main(String[] args) throws InterruptedException {
        final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
        Server server = new Server(threadPoolSize, ServerPort);
        server.start();
    }
}
