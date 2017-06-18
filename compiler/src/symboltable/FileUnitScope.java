package symboltable;

import ast.*;
import ast.definition.*;
import ast.type.*;

import java.util.List;

public class FileUnitScope extends Scope {
    public FileUnitScope(String name, ASTNode referenceNode) {
        super(name, referenceNode);
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
        signature = signature.substring(0, signature.length()-2);
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