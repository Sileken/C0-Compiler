/**
 * Symbol class for "easy" extension of the symbol-table
 *
 * Possible extension could be for the TypeChecker an enum type
 * 
 * History:
 * V.1.1  - identifier is now ast.identifier.Identifier instead of String
 *
 * @version 1.1
 * @date 15.06.2017
 */
package symboltable;

import ast.identifier.*;

public class Symbol {

    private final int index;
    private final Identifier identifier;    

    public Symbol(Identifier identifier, int index) {
        this.identifier = identifier;
        this.index = index;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public int getIndex() {
        return this.index;
    }
}