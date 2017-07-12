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
            throw new SymbolTableException("Duplicate File Unit: " + fileUnitName);
        }

        if(fileUnitScope != null)
            throw new SymbolTableException("Only one FileUnitScope is allowed");

        fileUnitScope = new FileUnitScope(fileUnitName, fileUnit);
        this.scopes.put(fileUnitName, fileUnitScope);
        return fileUnitScope;
    }

    public FileUnitScope getFileUnitScope(FileUnit fileUnit) throws SymbolTableException {
        String fileUnitName = getFileUnitScopeName(fileUnit);
        Scope scope = this.scopes.get(fileUnitName);
        if (scope != null && !(scope instanceof FileUnitScope)) {
            throw new SymbolTableException("Expecting BlockScope but get " + scope);
        }
        return (FileUnitScope) scope;
    }

    public FileUnitScope getFileUnitScope() throws SymbolTableException{
        if(fileUnitScope == null)
            throw new SymbolTableException("FileUnitScope is not set in Symbol-Table");
        return fileUnitScope;
    }

    public String getBlockScopeName(Scope currentScope, int blockCount){
        return currentScope.getName() + ".block" + blockCount;
    }

    public BlockScope addBlockScope(String blockName, Scope parent, ASTNode referenceNode) throws SymbolTableException {
        if (this.scopes.containsKey(blockName)) {
            throw new SymbolTableException("Duplicate Block Declaration: " + blockName);
        }

        BlockScope scope = new BlockScope(blockName, parent, referenceNode);
        this.scopes.put(blockName, scope);
        return scope;
    }

    public BlockScope getBlockScope(String blockName) throws SymbolTableException {
        Scope scope = this.scopes.get(blockName);
        if (scope != null && !(scope instanceof BlockScope)) {
            throw new SymbolTableException("Expecting BlockScope but get " + scope);
        }
        return (BlockScope) scope;
    }

    public String getStructTypeScopeName(StructDefinition structDefinition) {
        return "struct." + structDefinition.getName().getName();
    }

    public StructTypeScope addStructTypeScope(String structTypeName, Scope parent, ASTNode referenceNode)
            throws SymbolTableException {
        if (this.scopes.containsKey(structTypeName)) {
            throw new SymbolTableException("Duplicate Struct Defintion: " + structTypeName);
        }

        StructTypeScope scope = new StructTypeScope(structTypeName, parent, referenceNode);
        this.scopes.put(structTypeName, scope);
        return scope;
    }

    public StructTypeScope getStructTypeScope(String structTypeName) throws SymbolTableException {
        Scope scope = this.scopes.get(structTypeName);
        if (scope != null && !(scope instanceof StructTypeScope)) {
            throw new SymbolTableException("Expecting StructTypeScope but get " + scope);
        }
        return (StructTypeScope) scope;
    }

    public void listScopes() {
        Logger.log("Listing Scopes:");
        List<String> keys = new ArrayList<String>(this.scopes.keySet());
        for (String key : keys) {
            Logger.log(this.scopes.get(key).getName());
            this.scopes.get(key).listSymbols();
        }
    }
}