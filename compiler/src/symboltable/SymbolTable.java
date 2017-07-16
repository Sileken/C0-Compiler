package symboltable;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import logger.*;

import ast.*;
import ast.definition.*;
import ast.type.*;

/** Symbol Table holds all possible scopes and provides functions to create a scope */
public class SymbolTable {
    private FileUnitScope fileUnitScope = null;
    private Map<String, Scope> scopes = new LinkedHashMap<String, Scope>();

    public SymbolTable() {
        this.scopes = new LinkedHashMap<String, Scope>();
    }

    private String getFileUnitScopeName(FileUnit fileUnit) {
        return fileUnit.getIdentifier() + ".FileUnit";
    }

    public FileUnitScope addFileUnitScope(FileUnit fileUnit) throws SymbolTableException {
        String fileUnitName = getFileUnitScopeName(fileUnit);
        if (this.scopes.containsKey(fileUnitName)) {
            throw new SymbolTableException("Found duplicated File \"" + fileUnitName);
        }

        if (fileUnitScope != null) {
            throw new SymbolTableException("Symboltable internal Error: Only one FileUnitScope is allowed");
        }

        fileUnitScope = new FileUnitScope(fileUnitName, fileUnit);
        this.scopes.put(fileUnitName, fileUnitScope);
        return fileUnitScope;
    }

    public FileUnitScope getFileUnitScope(FileUnit fileUnit) throws SymbolTableException {
        String fileUnitName = getFileUnitScopeName(fileUnit);
        Scope scope = this.scopes.get(fileUnitName);
        if (scope != null && !(scope instanceof FileUnitScope)) {
            throw new SymbolTableException("Symboltable internal Error: Expecting BlockScope but get " + scope);
        }

        return (FileUnitScope) scope;
    }

    public FileUnitScope getFileUnitScope() throws SymbolTableException {
        if (fileUnitScope == null) {
            throw new SymbolTableException("Symboltable internal Error: FileUnitScope is not set in Symbol-Table");
        }

        return fileUnitScope;
    }

    public String getBlockScopeName(Scope currentScope, int blockCount) {
        return currentScope.getName() + ".block" + blockCount;
    }

    public BlockScope addBlockScope(String blockName, Scope parent, ASTNode referenceNode) throws SymbolTableException {
        if (this.scopes.containsKey(blockName)) {
            throw new SymbolTableException("Symboltable internal Error: Duplicate Block Declaration: " + blockName);
        }

        BlockScope scope = new BlockScope(blockName, parent, referenceNode);
        this.scopes.put(blockName, scope);
        return scope;
    }

      public BlockScope getBlockScope(String blockName) throws SymbolTableException {
        Scope scope = this.scopes.get(blockName);
        if (scope != null && !(scope instanceof BlockScope)) {
            throw new SymbolTableException("Symboltable internal Error: Expecting BlockScope but get " + scope);
        }

        return (BlockScope) scope;
    }

    public String getStructTypeScopeName(StructDefinition structDefinition) {
        return "struct." + structDefinition.getName().getName();
    }

    public StructTypeScope addStructTypeScope(String structTypeName, Scope parent, StructDefinition referenceNode)
            throws SymbolTableException {
        if (this.scopes.containsKey(structTypeName)) {
            StructType structType = (StructType) referenceNode.getType();
            throw new SymbolTableException("Duplicate Struct Defintion \"" + structType.getIdentifier() + "\" at line "
                    + structType.getIdentifierNode().getToken().beginLine + " in column "
                    + structType.getIdentifierNode().getToken().beginColumn);
        }

        StructTypeScope scope = new StructTypeScope(structTypeName, parent, referenceNode);
        this.scopes.put(structTypeName, scope);
        return scope;
    }

    public StructTypeScope getStructTypeScope(String structTypeName) throws SymbolTableException {
        Scope scope = this.scopes.get(structTypeName);
        if (scope != null && !(scope instanceof StructTypeScope)) {
            throw new SymbolTableException("Symboltable internal Error: Expecting " + StructTypeScope.class.getSimpleName()  + scope.getClass().getSimpleName());
        }

        return (StructTypeScope) scope;
    }

   public String listScopes() {
        String out = "";

        List<String> keys = new ArrayList<String>(this.scopes.keySet());
        for (String key : keys) {
            out += "\n" + this.scopes.get(key).getName();
            out += this.scopes.get(key).listSymbols();
        }

        return out;
    }
}