package symboltable;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import utils.*;

import ast.*;
import ast.definition.*;

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
            String errorMsg = "Duplicate File Unit: " + fileUnitName;
            Logger.error(errorMsg);
            throw new SymbolTableException(errorMsg);
        }

        if (fileUnitScope != null) {
            String errorMsg = "Only one FileUnitScope is allowed";
            Logger.error(errorMsg);
            throw new SymbolTableException(errorMsg);
        }

        fileUnitScope = new FileUnitScope(fileUnitName, fileUnit);
        this.scopes.put(fileUnitName, fileUnitScope);
        return fileUnitScope;
    }

    public FileUnitScope getFileUnitScope(FileUnit fileUnit) throws SymbolTableException {
        String fileUnitName = getFileUnitScopeName(fileUnit);
        Scope scope = this.scopes.get(fileUnitName);
        if (scope != null && !(scope instanceof FileUnitScope)) {
            String errorMsg = "Expecting BlockScope but get " + scope;
            Logger.error(errorMsg);
            throw new SymbolTableException(errorMsg);
        }

        return (FileUnitScope) scope;
    }

    public FileUnitScope getFileUnitScope() throws SymbolTableException {
        if (fileUnitScope == null) {
            String errorMsg = "FileUnitScope is not set in Symbol-Table";
            Logger.error(errorMsg);
            throw new SymbolTableException(errorMsg);
        }

        return fileUnitScope;
    }

    public String getBlockScopeName(Scope currentScope, int blockCount) {
        return currentScope.getName() + ".block" + blockCount;
    }

    public BlockScope addBlockScope(String blockName, Scope parent, ASTNode referenceNode) throws SymbolTableException {
        if (this.scopes.containsKey(blockName)) {
            String errorMsg = "Duplicate Block Declaration: " + blockName;
            Logger.error(errorMsg);
            throw new SymbolTableException(errorMsg);
        }

        BlockScope scope = new BlockScope(blockName, parent, referenceNode);
        this.scopes.put(blockName, scope);
        return scope;
    }

    public BlockScope getBlockScope(String blockName) throws SymbolTableException {
        Scope scope = this.scopes.get(blockName);
        if (scope != null && !(scope instanceof BlockScope)) {
            String errorMsg = "Expecting BlockScope but get " + scope;
            Logger.error(errorMsg);
            throw new SymbolTableException(errorMsg);
        }

        return (BlockScope) scope;
    }

    public String getStructTypeScopeName(StructDefinition structDefinition) {
        return "struct." + structDefinition.getName().getName();
    }

    public StructTypeScope addStructTypeScope(String structTypeName, Scope parent, ASTNode referenceNode)
            throws SymbolTableException {
        if (this.scopes.containsKey(structTypeName)) {
            String errorMsg = "Duplicate Struct Defintion: " + structTypeName;
            Logger.error(errorMsg);
            throw new SymbolTableException(errorMsg);
        }

        StructTypeScope scope = new StructTypeScope(structTypeName, parent, referenceNode);
        this.scopes.put(structTypeName, scope);
        return scope;
    }

    public StructTypeScope getStructTypeScope(String structTypeName) throws SymbolTableException {
        Scope scope = this.scopes.get(structTypeName);
        if (scope != null && !(scope instanceof StructTypeScope)) {
            String errorMsg = "Expecting StructTypeScope but get " + scope;
            Logger.error(errorMsg);
            throw new SymbolTableException(errorMsg);
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