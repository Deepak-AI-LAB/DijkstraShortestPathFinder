/**
 * GraphException.java
 *
 * A custom checked exception specific to this project. Using our own
 * exception type instead of a generic RuntimeException makes errors
 * self-documenting -- anyone reading the code (or a stack trace) can
 * tell immediately that the problem is invalid graph data, such as an
 * out-of-range vertex or a negative edge weight.
 *
 * It extends Exception (not RuntimeException), which makes it a
 * "checked" exception -- Java forces any method that can throw it to
 * either handle it or declare it with 'throws'. That's intentional
 * here: it forces careful error handling wherever graph data is built
 * or used, which is exactly the discipline a professional project
 * should show.
 */
public class GraphException extends Exception {
    public GraphException(String message) {
        super(message);
    }
}