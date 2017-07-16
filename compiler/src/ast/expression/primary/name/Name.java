package ast.expression.primary.name;

import ast.expression.primary.*;
import symboltable.*;
import ast.identifier.*;

public abstract class Name extends Primary {
    protected Symbol originalDeclaration;
    Identifier identifer;

    public Name(Identifier identifer) {
        super();

        this.identifer = identifer;
        this.addChild(this.identifer);
        this.setIdentifier(this.identifer.getName());
    }

    public String getName() {
        return identifer.getName();
    }

    public Symbol getOriginalDeclaration() {
        return originalDeclaration;
    }

    public void setOriginalDeclaration(Symbol originalDeclaration) {
        this.originalDeclaration = originalDeclaration;
    }

    public Identifier getIdentifierNode() {
        return identifer;
    }

}