package ast.expression.primary;

import ast.expression.Expression;

public class ArrayAccess extends Primary {
    Expression indexExpression;

    public ArrayAccess(Expression indexExpression) {
        super();

        this.indexExpression = indexExpression;
        this.addChild(indexExpression);
    }

    public Expression getIndexExpression(){
        return indexExpression;
    }

    // Override method to return the prefix name (which is the array-name)
    @Override
    public String getIdentifier() {
        Primary prefix = getPrefix();
        return prefix.getIdentifier();
    }
}