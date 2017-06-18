package symboltable;

import ast.*;
import ast.definition.*;
import ast.type.*;

import java.util.List;

public class FileUnitScope extends Scope {
    public FileUnitScope(String name, ASTNode referenceNode) {
        super(name, referenceNode);
    }

    public void addStructDefinition(StructDefinition structDeclr) throws SymbolTableException {
        Symbol symbol = new Symbol(structDeclr.getName().getName(), structDeclr, this);

        if (this.symbols.containsKey(name)) {
            throw new SymbolTableException("Duplicate Struct Definitions: " + name);
        }

        this.symbols.put(name, symbol);
    }

    public String signatureOfFunction(FunctionDefinition functionDecl) {
        return signatureOfFunction(functionDecl.getName().getName(), functionDecl.getParameterTypes());
    }

    public String signatureOfFunction(String methodName, List<Type> parameterTypes) {
        String name = methodName + "(";
        for (Type parameterType : parameterTypes) {
            name += parameterType.getFullyQualifiedName() + ",";
        }
        name += ")";
        return name;
    }

    public void addFunctionDefinition(FunctionDefinition functionDecl) throws SymbolTableException {
        String name = signatureOfFunction(functionDecl.getName().getName(), functionDecl.getParameterTypes());
        if (this.symbols.containsKey(name)) {
            throw new SymbolTableException("Duplicate Method Declaraion " + name);
        }
        Symbol symbol = new Symbol(name, functionDecl, this);
        this.symbols.put(name, symbol);
    }
}