package codegen;

import symboltable.*;

public class CodeGenerationException extends SymbolTableException {

    public CodeGenerationException() {
        super();
    }

    public CodeGenerationException(String msg) {
        super(msg);
    }
}