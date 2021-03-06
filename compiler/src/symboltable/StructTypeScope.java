package symboltable;

import ast.*;
import ast.definition.*;
import ast.identifier.*;
import ast.type.*;
import symboltable.*;
import logger.*;

import java.util.List;

public class StructTypeScope extends Scope {
    protected Scope parent;

    public StructTypeScope(String name, Scope parent, ASTNode referenceNode) {
        super(name, referenceNode);
        this.parent = parent;
    }

    public String nameForFieldDefinition(FieldDefinition fieldDef) throws SymbolTableException {
        return this.getName() + "." + fieldDef.getName().getName();
    }

    public String nameForFieldIdentifier(FieldIdentifier fieldId) throws SymbolTableException {
        return this.getName() + "." + fieldId.getName();
    }

    public void addFieldDefinition(FieldDefinition fieldDef) throws SymbolTableException {
        String symbolName = this.nameForFieldDefinition(fieldDef);
        Symbol symbol = new Symbol(symbolName, fieldDef, this);

        if (this.symbols.containsKey(symbolName)) {
            throw new SymbolTableException("Duplicate Field Definition \"" + fieldDef.getName().getName() + "\" at line "
                    + fieldDef.getName().getToken().beginLine + " in column "
                    + fieldDef.getName().getToken().beginColumn);
        }

        this.putSymbol(symbolName, symbol);
    }

    public Symbol getFieldDefinition(FieldDefinition fieldDef) throws SymbolTableException {
        return this.symbols.get(this.nameForFieldDefinition(fieldDef));
    }

    public Symbol getFieldDefinition(FieldIdentifier fieldId) throws SymbolTableException {
        return this.symbols.get(this.nameForFieldIdentifier(fieldId));
    }

}