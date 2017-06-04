package ast;

import java.util.ArrayList;
import java.util.List;
import ast.declaration.*;
import ast.definition.*;

public class FileUnit extends ASTNode {
    private List<Declaration> globalDeclarations = new ArrayList<Declaration>();
    private List<Definition> globalDefinitions = new ArrayList<Definition>();

    public FileUnit(List<Declaration> globalDeclarations, List<Definition> globalDefinitions) {
        super();

        if(globalDeclarations != null && !globalDeclarations.isEmpty()){
            this.globalDeclarations = globalDeclarations;
            this.addChilds(this.globalDeclarations);
        }

        if(globalDefinitions != null && !globalDefinitions.isEmpty()){
            this.globalDefinitions = globalDefinitions;
            this.addChilds(this.globalDefinitions);
        }
    }
}