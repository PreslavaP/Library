public class Book {
    private final int id;
    private final String isbn;
    private final String title;
    private final String author;
    private final String libraryName;
    private boolean isAvailable;

    public Book(int id, String isbn, String title, String author, String libraryName, boolean isAvailable) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.libraryName = libraryName;
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getLibraryName() {
        return libraryName;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Title: " + title + ", Author: " + author +
                ", Library: " + libraryName + ", Available: " + (isAvailable ? "YES" : "NO");
    }
}
