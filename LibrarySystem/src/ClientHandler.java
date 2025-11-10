import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            clientSocket) {

            out.println("Welcome to the Library System!");
            out.println("Enter a function number (1-Search, 2-Borrow, 3-Show) or 'exit' to end:");

            String inputLine;
            while((inputLine = in.readLine()) != null){
                if(inputLine.equalsIgnoreCase("exit")){
                    break;
                }

                try {
                    int choice = Integer.parseInt(inputLine.trim());

                    switch (choice) {
                        case 1:
                            handleSearch(in, out);
                            break;
                        case 2:
                            handleBorrow(in, out);
                            break;
                            case 3:
                            handleShow(in, out);
                            break;
                        default:
                            out.println("Invalid choice. Please select <1, 2, 3 or 'exit>.'");
                            break;
                    }
                } catch (NumberFormatException e){
                    out.println("Invalid input. Please enter a number between 1 and 3.");
                }
                out.println("\nSelect next function <1, 2, 3 or 'exit>: ");
            }
        } catch (Exception e) {
            System.err.println("The connection with the client was interrupted: " + e.getMessage());
        } finally {
            System.out.println("Client closed.");
        }
    }

    private void handleShow(BufferedReader in, PrintWriter out) throws IOException {
        out.println("Enter name of the library:");
        String libraryName = in.readLine().trim();

        if (libraryName.isEmpty()) {
            out.println("Please, enter right name of the library:");
            return;
        }

        String results = LibraryServer.BOOK_DATABASE.stream()
                .filter(book -> book.getLibraryName().equalsIgnoreCase(libraryName))
                .filter(Book::isAvailable)
                .map(Book::toString)
                .collect(Collectors.joining("\n"));

        if (results.isEmpty()) {
            boolean libraryExists = LibraryServer.BOOK_DATABASE.stream()
                    .anyMatch(book -> book.getLibraryName().equalsIgnoreCase(libraryName));

            if (libraryExists) {
                out.println("Library '" + libraryName + "' exist, but there are no books for borrow.");
            } else {
                out.println("ERROR: Library '" + libraryName + "' does not exist in the system.");
            }
        } else {
            out.println("Books available in '" + libraryName + "':");
            out.println(results);
        }
    }

    private void handleBorrow(BufferedReader in, PrintWriter out) throws IOException {
        out.println("Enter ID to the book you want to borrow:");
        String idInput = in.readLine().trim();

        if(idInput.isEmpty()){
            out.println("Invalid ID. Please enter a valid ID.");
            return;
        }

        try {
            int bookId = Integer.parseInt(idInput);

            synchronized(LibraryServer.BOOK_DATABASE){
                Book bookToBorrow = LibraryServer.BOOK_DATABASE.stream()
                        .filter(book -> book.getId() == bookId)
                        .findFirst()
                        .orElse(null);

                if(bookToBorrow == null){
                    out.println("ERROR: Book with ID " + bookId + " not found.");
                } else if(!bookToBorrow.isAvailable()){
                    out.println("WARNING: The book '" + bookToBorrow.getTitle() + "' is already taken.");
                } else {
                    bookToBorrow.setAvailable(false);
                    out.println("SUCCESS: The book '" + bookToBorrow.getTitle() + "' from " + bookToBorrow.getLibraryName() + " was successfully borrowed.");
                }
            }
        } catch (NumberFormatException e){
            out.println("Invalid ID. The ID must be a number.");
        }
    }

    private void handleSearch(BufferedReader in, PrintWriter out) throws IOException {
        out.println("Enter title of the book you want to search:");
        String title = in.readLine().trim().toLowerCase();

        if (title.isEmpty()) {
            out.println("Please, enter valid title.");
            return;
        }

        String results = LibraryServer.BOOK_DATABASE.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title))
                .filter(Book::isAvailable)
                .map(book -> String.format("ID: %d, Title: %s, Author: %s, Library: %s",
                        book.getId(), book.getTitle(), book.getAuthor(), book.getLibraryName()))
                .collect(Collectors.joining("\n"));

        if (results.isEmpty()) {
            out.println("No available books with this title were found.");
        } else {
            out.println("Available books found:");
            out.println(results);
        }
    }
}
