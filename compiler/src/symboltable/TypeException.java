package symboltable;

import symboltable.SymbolTableException;

public class TypeException extends SymbolTableException {

    public TypeException() {
        super();
    }

    public TypeException(String msg) {
        super(msg);
    }
}