/**
 * Exception will be thrown from the SymbolTable when an identifier has never
 * been added to the list of identifiers.
 *
 * @version 1.0
 * @date 10.06.2017
 * @see Exception
 */
package symboltable;

public class IdentifierNotFoundException extends Exception {

    public IdentifierNotFoundException() {
        super();
    }

    public IdentifierNotFoundException(String msg) {
        super(msg);
    }
}