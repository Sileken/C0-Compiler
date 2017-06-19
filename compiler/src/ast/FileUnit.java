package ast;

import java.util.ArrayList;
import java.util.List;
import ast.*;
import ast.declaration.*;
import ast.definition.*;

public class FileUnit extends ASTNode {
    private List<ASTNode> globalDeclAndDef = new ArrayList<ASTNode>();

    public FileUnit(List<ASTNode> globalDeclAndDef) {
        super();

        if(globalDeclAndDef != null && !globalDeclAndDef.isEmpty()){
            this.globalDeclAndDef = globalDeclAndDef;
            this.addChilds(this.globalDeclAndDef);
        }
    }
}