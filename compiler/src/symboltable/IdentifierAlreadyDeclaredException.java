/**
 * Exception will be thrown from the SymbolTable when an identifier has 
 * been added to the list of identifiers or is not reachable within the current Scope.
 *
 * @version 1.0
 * @date 10.06.2017
 * @see Exception
 */
package symboltable;

public class IdentifierAlreadyDeclaredException extends Exception {

    public IdentifierAlreadyDeclaredException() {
        super();
    }

    public IdentifierAlreadyDeclaredException(String msg) {
        super(msg);
    }
}