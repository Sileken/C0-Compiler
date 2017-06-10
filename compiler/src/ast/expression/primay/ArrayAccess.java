package ast.expression.primay;

import ast.expression.Expression;

public class ArrayAccess extends Primary {
    Primary prefix;
    Expression indexExpression;

    public ArrayAccess(Primary prefix, Expression indexExpression){
        super();

        this.prefix = prefix;
        this.addChild(prefix);

        this.indexExpression = indexExpression;
        this.addChild(indexExpression);
    }
}