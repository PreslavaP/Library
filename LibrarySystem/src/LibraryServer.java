import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LibraryServer {
    public static final List<Book> BOOK_DATABASE = new ArrayList<>();
    public static final int PORT = 5678;
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1);

    public static void main(String[] args) throws IOException {
        initializeDatabase();

        System.out.println("Server started on port: " + PORT + " ...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress().getHostName());

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e){
            System.out.println("Error while starting the server: " + e.getMessage());
        }
    }

    private static void initializeDatabase() {
        BOOK_DATABASE.add(new Book(ID_COUNTER.getAndIncrement(), "978-0134685991", "Java Programming", "J. Gosling", "Center Library", true));
        BOOK_DATABASE.add(new Book(ID_COUNTER.getAndIncrement(), "978-0134685991", "Java Programming", "J. Gosling", "East Library", false));
        BOOK_DATABASE.add(new Book(ID_COUNTER.getAndIncrement(), "978-0743273565", "The Great Gatsby", "F. Scott Fitzgerald", "West Library", true));
        BOOK_DATABASE.add(new Book(ID_COUNTER.getAndIncrement(), "978-0123456789", "Algorithms Explained", "A. Tutor", "Center Library", true));

        System.out.println("The database is loaded with " + BOOK_DATABASE.size() + " records.");
    }
}
