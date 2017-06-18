package symboltable;

import ast.*;
import ast.definition.*;
import ast.declaration.*;
import ast.type.*;

import java.util.List;

public class FileUnitScope extends Scope {
    private String declartionPrefix = "decl.";

    public FileUnitScope(String name, ASTNode referenceNode) {
        super(name, referenceNode);
    }

    public String getDeclartionPrefix() {
        return this.declartionPrefix;
    }

    public void addStructDeclaration(StructDeclaration structDecl) throws SymbolTableException {
        String symbolName = this.getDeclartionPrefix() + structDecl.getName().getName();
        Symbol symbol = new Symbol(symbolName, structDecl, this);

        if (this.symbols.containsKey(symbolName)) {
            throw new SymbolTableException("Duplicate Struct Declaration: " + symbolName);
        }

        this.symbols.put(symbolName, symbol);
    }

    public void addFunctionDeclaration(FunctionDeclaration functionDecl) throws SymbolTableException {
        String symbolName = this.getDeclartionPrefix()
                + signatureOfFunction(functionDecl.getName().getName(), functionDecl.getParameterTypes());
        if (this.symbols.containsKey(symbolName)) {
            throw new SymbolTableException("Duplicate Function Declaration:" + symbolName);
        }
        Symbol symbol = new Symbol(symbolName, functionDecl, this);
        this.symbols.put(symbolName, symbol);
    }

    public void addStructDefinition(StructDefinition structDef) throws SymbolTableException {
        String symbolName = structDef.getName().getName();
        Symbol symbol = new Symbol(symbolName, structDef, this);

        if (this.symbols.containsKey(symbolName)) {
            throw new SymbolTableException("Duplicate Struct Definition: " + symbolName);
        }

        this.symbols.put(symbolName, symbol);
    }

    public String signatureOfFunction(FunctionDefinition functionDef) {
        return signatureOfFunction(functionDef.getName().getName(), functionDef.getParameterTypes());
    }

    public String signatureOfFunction(String methodName, List<Type> parameterTypes) {
        String signature = methodName + "(";
        for (Type parameterType : parameterTypes) {
            signature += parameterType.getFullyQualifiedName() + ", ";
        }
        if (signature.endsWith(", ")) {
            signature = signature.substring(0, signature.length() - 2);
        }
        signature += ")";
        return signature;
    }

    public void addFunctionDefinition(FunctionDefinition functionDef) throws SymbolTableException {
        String symbolName = signatureOfFunction(functionDef.getName().getName(), functionDef.getParameterTypes());
        if (this.symbols.containsKey(symbolName)) {
            throw new SymbolTableException("Duplicate Function Definition:" + symbolName);
        }
        Symbol symbol = new Symbol(symbolName, functionDef, this);
        this.symbols.put(symbolName, symbol);
    }
}