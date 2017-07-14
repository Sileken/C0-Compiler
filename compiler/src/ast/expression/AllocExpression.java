package ast.expression;

import ast.expression.Expression;
import ast.type.*;

public class AllocExpression extends Expression {
    private boolean isArrayAllocation = false;
    private Type type;
    private Expression size;

    public AllocExpression(Type type) {
        super();

        this.type = type;
        this.addChild(type);
    }

    public AllocExpression(Type type, Expression size) {
        this(type);

        isArrayAllocation = true;

        this.size = size;
        this.addChild(size);
    }

    public boolean isArrayAlloc() {
        return isArrayAllocation;
    }

    public Type getAllocationType() {
        return type;
    }

    public Expression getArrayAllocationSize(){
        return size;
    }
}