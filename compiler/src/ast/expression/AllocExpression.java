package ast.expression;

import ast.expression.Expression;
import ast.type.*;

public class AllocExpression extends Expression {
    private boolean isArrayAllocation = false;
    private Type type;
    private Expression dimension;

    public AllocExpression(Type type) {
        super();

        this.type = type;
        this.addChild(type);
    }

    public AllocExpression(Type type, Expression dimension) {
        this(type);

        isArrayAllocation = true;

        this.dimension = dimension;
        this.addChild(dimension);
    }

    public boolean isArrayAlloc() {
        return isArrayAllocation;
    }
}