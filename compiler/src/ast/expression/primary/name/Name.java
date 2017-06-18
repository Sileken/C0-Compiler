package ast.expression.primary.name;

import ast.expression.primary.*;
import symboltable.*;

public abstract class Name extends Primary {
    protected Symbol originalDeclaration;

    public Name() {
        super();
    }
    
    public Symbol getOriginalDeclaration() {
		return originalDeclaration;
	}

	public void setOriginalDeclaration(Symbol originalDeclaration) {
		this.originalDeclaration = originalDeclaration;
	}

}