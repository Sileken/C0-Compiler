package ast.statement;

import ast.expression.*;
import ast.expression.primary.*;

public class ExpressionStatement extends Statement {
    private Expression expression;

    public ExpressionStatement(Expression expression) {
        super();

        this.expression = expression;
        this.addChild(this.expression);
    }
}