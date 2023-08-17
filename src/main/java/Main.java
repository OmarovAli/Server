public class Main {
    private final static int ServerPort = 9999;
    public static void main(String[] args) throws InterruptedException {
        Server server = new Server(64, ServerPort);
        server.start();
    }
}
