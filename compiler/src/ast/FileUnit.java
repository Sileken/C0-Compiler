package ast;

import java.util.ArrayList;
import java.util.List;
import ast.*;
import ast.declaration.*;
import ast.definition.*;

public class FileUnit extends ASTNode {
    private List<ASTNode> globalDeclAndDef = new ArrayList<ASTNode>();
    private List<String> generatedCode = new ArrayList<String>();

    public FileUnit(String fileName, List<ASTNode> globalDeclAndDef) {
        super();

        this.setIdentifier(fileName);

        if (globalDeclAndDef != null && !globalDeclAndDef.isEmpty()) {
            this.globalDeclAndDef = globalDeclAndDef;
            this.addChilds(this.globalDeclAndDef);
        }
    }

    public void setGeneratedCode(List<String> code) {
        this.generatedCode = code;
    }

    public List<String> getGeneratedCode() {
        return this.generatedCode;
    }
}