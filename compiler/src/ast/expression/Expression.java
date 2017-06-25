package ast.expression;

import ast.ASTNode;
import ast.type.*;

public abstract class Expression extends ASTNode {
    private Type exprType;

    public Expression() {
        super();
        exprType = null;
    }


    public Type getType()
    {
        return exprType;
    }

    public void setType(Type type)
    {
        exprType = type;
    }

}