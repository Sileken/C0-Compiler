package symboltable;

import ast.*;
import ast.definition.*;
import ast.declaration.*;
import ast.type.*;
import ast.expression.primary.name.*;
import logger.*;

import java.util.List;

public class FileUnitScope extends Scope {
    private String declarationPrefix = "decl.";
    private String definitionPrefix = "def.";

    public FileUnitScope(String name, ASTNode referenceNode) {
        super(name, referenceNode);
    }

    public String getDeclarationPrefix() {
        return this.declarationPrefix;
    }

    public String getDefinitionPrefix() {
        return this.definitionPrefix;
    }

    public void addStructDeclaration(StructDeclaration structDecl) throws SymbolTableException {
        String symbolName = structDecl.getName().getName();
        String symbolNameWithDeclPrefix = this.getDeclarationPrefix() + symbolName;
        String symbolNameWithDefPrefix = this.getDefinitionPrefix() + symbolName;

        if (this.symbols.containsKey(symbolNameWithDeclPrefix)) {
            throw new SymbolTableException("Duplicate Struct Declaration \"" + symbolName + "\" at line "
                    + structDecl.getName().getToken().beginLine + " in column "
                    + structDecl.getName().getToken().beginColumn);
        } else if (this.symbols.containsKey(symbolNameWithDefPrefix)) {
            StructDefinition structDef = (StructDefinition) (this.symbols.get(symbolNameWithDefPrefix).getNode());
            Logger.error("Invalid Struct Declaration \"" + symbolName + "\" at line "
                    + structDecl.getName().getToken().beginLine + " in column "
                    + structDecl.getName().getToken().beginColumn);
            throw new SymbolTableException("Struct Definition already exists \"" + symbolName + "\" at line "
                    + structDef.getName().getToken().beginLine + " in column "
                    + structDef.getName().getToken().beginColumn);
        }

        Symbol symbol = new Symbol(symbolNameWithDeclPrefix, structDecl, this);
        this.putSymbol(symbolNameWithDeclPrefix, symbol);
    }

    public void addFunctionDeclaration(FunctionDeclaration functionDecl) throws SymbolTableException {
        String symbolName = getSignatureOfFunction(functionDecl.getName().getName(), functionDecl.getParameterTypes());
        String symbolNameWithDeclPrefix = this.getDeclarationPrefix() + symbolName;
        String symbolNameWithDefPrefix = this.getDefinitionPrefix() + symbolName;

        if (this.symbols.containsKey(symbolNameWithDeclPrefix)) {
            throw new SymbolTableException("Duplicate Function Declaration \"" + symbolName + "\" at line "
                    + functionDecl.getName().getToken().beginLine + " in column "
                    + functionDecl.getName().getToken().beginColumn);
        } else if (this.symbols.containsKey(symbolNameWithDefPrefix)) {
            FunctionDefinition functionDef = (FunctionDefinition) (this.symbols.get(symbolNameWithDefPrefix).getNode());
            Logger.error("Invalid Function Declaration \"" + symbolName + "\" at line "
                    + functionDecl.getName().getToken().beginLine + " in column "
                    + functionDecl.getName().getToken().beginColumn);
            throw new SymbolTableException("Function Definition already exists \"" + symbolName + "\" at line "
                    + functionDef.getName().getToken().beginLine + " in column "
                    + functionDef.getName().getToken().beginColumn);
        }

        Symbol symbol = new Symbol(symbolNameWithDeclPrefix, functionDecl, this);
        this.putSymbol(symbolNameWithDeclPrefix, symbol);
    }

    public void addStructDefinition(StructDefinition structDef) throws SymbolTableException {
        String symbolName = structDef.getName().getName();
        String symbolNameWithDefPrefix = this.getDefinitionPrefix() + symbolName;

        if (this.symbols.containsKey(symbolNameWithDefPrefix)) {
            throw new SymbolTableException("Duplicate Struct Definition \"" + symbolName + "\" at line "
                    + structDef.getName().getToken().beginLine + " in column "
                    + structDef.getName().getToken().beginColumn);
        }

        Symbol symbol = new Symbol(symbolNameWithDefPrefix, structDef, this);
        this.putSymbol(symbolNameWithDefPrefix, symbol);
    }

    public void addFunctionDefinition(FunctionDefinition functionDef) throws SymbolTableException {
        String symbolName = getSignatureOfFunction(functionDef);
        String symbolNameWithDefPrefix = this.getDefinitionPrefix() + symbolName;

        if (this.symbols.containsKey(symbolNameWithDefPrefix)) {
            throw new SymbolTableException("Duplicate Function Definition \"" + symbolName + "\" at line "
                    + functionDef.getName().getToken().beginLine + " in column "
                    + functionDef.getName().getToken().beginColumn);
        }

        Symbol symbol = new Symbol(symbolNameWithDefPrefix, functionDef, this);
        this.putSymbol(symbolNameWithDefPrefix, symbol);
    }

    public String getSignatureOfFunction(FunctionDefinition functionDef) {
        return getSignatureOfFunction(functionDef.getName().getName(), functionDef.getParameterTypes());
    }

    public String getSignatureOfFunction(FunctionDeclaration functionDecl) {
        return getSignatureOfFunction(functionDecl.getName().getName(), functionDecl.getParameterTypes());
    }

    public String getSignatureOfFunction(String methodName, List<Type> parameterTypes) {
        String signature = methodName + "(";
        for (Type parameterType : parameterTypes) {
            signature += parameterType.getFullyQualifiedName() + ",";
        }
        if (signature.endsWith(",")) {
            signature = signature.substring(0, signature.length() - 1);
        }
        signature += ")";
        return signature;
    }

    public Symbol resolveFunctionSymbol(String signatureOfFunction) throws SymbolTableException {
        Symbol symbol = null;
        // suffix to find definition or declaration
        List<Symbol> matchedSymbols = this.findEntriesWithSuffix(this.symbols.values(), signatureOfFunction);
        if (matchedSymbols.size() > 0) {
            symbol = matchedSymbols.get(0);
        }

        return symbol;
    }

    public Symbol resolveStructSymbol(String structName) throws SymbolTableException {
        Symbol symbol = null;
        // suffix to find definition or declaration
        List<Symbol> matchedSymbols = this.findEntriesWithSuffix(this.symbols.values(), structName);
        if (matchedSymbols.size() > 0) {
            symbol = matchedSymbols.get(0);
        }

        return symbol;
    }
}