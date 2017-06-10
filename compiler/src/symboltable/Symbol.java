/**
 * Symbol class for "easy" extension of the symbol-table
 *
 * Possible extension could be for the TypeChecker an enum type
 *
 * @version 1.0
 * @date 10.06.2017
 */
package symboltable;

public class Symbol {
    private final String identifier;
    private final int index;

    public Symbol(String identifier, int index) {
        this.identifier = identifier;
        this.index = index;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public int getIndex() {
        return this.index;
    }
}