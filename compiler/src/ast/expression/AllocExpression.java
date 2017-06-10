package ast.expression;

import ast.expression.Expression;
import ast.type.*;

public class AllocExpression extends Expression {
    private boolean isArrayAllocation = false;
    private Type type;
    private Expression expression;

    public AllocExpression(Type type){
        super();

        this.type = type;
        this.addChild(type);        
    }

     public AllocExpression(Type type, Expression expression){
        this(type);
        isArrayAllocation = true;

        this.expression = expression;
        this.addChild(expression);
    }
}