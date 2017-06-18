package symboltable;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import ast.*;

public class SymbolTable {

    private Map<String, Scope> scopes = new HashMap<String, Scope>();

    public SymbolTable() {
        this.scopes = new HashMap<String, Scope>();
    }

    private String getFileUnitScopeName(FileUnit fileUnit) {
        return fileUnit.getIdentifier() + ".FileUnit";
    }

    public FileUnitScope addFileUnitScope(FileUnit fileUnit) throws SymbolTableException {
        String fileUnitName = getFileUnitScopeName(fileUnit);
        if (this.scopes.containsKey(fileUnitName)) {
            throw new SymbolTableException("Duplicate File Unit: " + fileUnitName);
        }

        FileUnitScope scope = new FileUnitScope(fileUnitName, fileUnit);
        this.scopes.put(fileUnitName, scope);
        return scope;
    }

    public FileUnitScope getFileUnitScope(FileUnit fileUnit) throws SymbolTableException {
        String fileUnitName = getFileUnitScopeName(fileUnit);
        Scope scope = this.scopes.get(fileUnitName);
        if (scope != null && !(scope instanceof FileUnitScope)) {
            throw new SymbolTableException("Expecting BlockScope but get " + scope);
        }
        return (FileUnitScope) scope;
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

    public void listScopes() {
        System.out.println("Listing Scopes:");
        List<String> keys = new ArrayList<String>(this.scopes.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            System.out.println(this.scopes.get(key).getName());
            this.scopes.get(key).listSymbols();
        }
    }
}