/**
 * SymbolTable class.
 *
 * Contains a final and unique List of all string-identifiers and a Stack of Scopes.
 *
 * Design of the class brings one limitation:
 * Maximal count of unique string identifiers is restricted to almost MAX_INT
 * (Java max. size of ArrayList is a bit less than a signed integer (2^32 bit) depending on
 * JDK-version and virtual machine)
 *
 * @version 0.1
 * @date 10.06.2016
 */
package symboltable;

import java.util.Stack;

public class SymbolTable {

    private FinalUniqueList<String> variables;      // List of every defined identifier
    private Stack<Scope> stack;                     // Stack of Scopes

    public SymbolTable() {
        variables = new FinalUniqueList<String>();
        stack = new Stack<Scope>();
    }

    /**
     * Enter a new scope for variables
     */
    public void enterScope() {
        stack.push(new Scope());
    }

    /**
     * Exit the current scope.
     * All information will be lost.
     */
    public void exitScope() {
        stack.pop();
    }

    /**
     * Add a new Symbol to the current scope.
     * @param identifier String name of the variable
     */
    public void addSymbol(String identifier) {
        int index = variables.indexOf(identifier);

        // new identifier
        if (index == -1) {
            // no additional index needed because list is final and unique
            index = variables.size();
            variables.add(identifier);
        }

        Symbol s = new Symbol(identifier, index);
        stack.peek().addSymbol(s);
    }

    /**
     * Check if the identifier is in the current scope.
     *
     * @param identifier String name of the variable
     * @return Symbol if the identifier exists or null
     * @throws IdentifierNotFoundException if the identifier was never added before
     */
    public Symbol getSymbol(String identifier) throws IdentifierNotFoundException {
        int index = identifier2Index(identifier);
        return stack.peek().getSymbol(index);
    }

    /**
     * Check if the identifier is used ever before.
     *
     * @param identifier String name of the variable
     * @return Symbol if the identifier exists or null
     * @throws IdentifierNotFoundException if the identifier was never added before
     */
    public Symbol lookup(String identifier) throws IdentifierNotFoundException {
        int index = identifier2Index(identifier);

        // Stack is internally implemented by a vector
        for (int i = stack.size() - 1; i >= 0; i--) {
            Symbol symbol = stack.elementAt(i).getSymbol(index);
            if (symbol != null) {
                return symbol;
            }
        }
        return null; // or throw Exception e. g. "not available in current block"
    }

    /**
     *
     * @param identifier String name of the variable
     * @return Integer index of the symbol with the identifier
     * @throws IdentifierNotFoundException if the identifier was never added before
     */
    private int identifier2Index(String identifier) throws IdentifierNotFoundException {
        int index = this.variables.indexOf(identifier);

        if (index < 0) {
            throw new IdentifierNotFoundException("Identifier '" + identifier + "' never used before.");
        }
        return index;
    }
}