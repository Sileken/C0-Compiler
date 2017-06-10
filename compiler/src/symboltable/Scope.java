/**
 * Every Scope contains a Hashtable of it's Symbols.
 *
 * @version 1.0
 * @date 10.06.2017
 * @see Hashtable, Symbol
 */
package symboltable;

import java.util.Hashtable;

public class Scope {

    private Hashtable<Integer, Symbol> symbols;

    public Scope() {
        this.symbols = new Hashtable<Integer, Symbol>();
    }

    public void addSymbol(Symbol symbol) {
        this.symbols.put(symbol.getIndex(), symbol);
    }

    public Symbol getSymbol(int index) {
        return this.symbols.get(new Integer(index));
    }
}