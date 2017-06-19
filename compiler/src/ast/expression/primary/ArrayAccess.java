package ast.expression.primary;

import ast.expression.Expression;

public class ArrayAccess extends Primary {
    Expression indexExpression;

    public ArrayAccess(Expression indexExpression){
        super();

        this.indexExpression = indexExpression;
        this.addChild(indexExpression);
    }
}