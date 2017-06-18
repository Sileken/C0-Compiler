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
        String symbolName = structDeclr.getName().getName();
        Symbol symbol = new Symbol(symbolName, structDeclr, this);

        if (this.symbols.containsKey(symbolName)) {
            throw new SymbolTableException("Duplicate Struct Definitions: " + symbolName);
        }

        this.symbols.put(symbolName, symbol);
    }

    public String signatureOfFunction(FunctionDefinition functionDecl) {
        return signatureOfFunction(functionDecl.getName().getName(), functionDecl.getParameterTypes());
    }

    public String signatureOfFunction(String methodName, List<Type> parameterTypes) {
        String signature = methodName + "(";
        for (Type parameterType : parameterTypes) {
            signature += parameterType.getFullyQualifiedName() + ",";
        }
        signature += ")";
        return signature;
    }

    public void addFunctionDefinition(FunctionDefinition functionDecl) throws SymbolTableException {
        String symbolName = signatureOfFunction(functionDecl.getName().getName(), functionDecl.getParameterTypes());
        if (this.symbols.containsKey(symbolName)) {
            throw new SymbolTableException("Duplicate Method Declaraion " + symbolName);
        }
        Symbol symbol = new Symbol(symbolName, functionDecl, this);
        this.symbols.put(symbolName, symbol);
    }
}