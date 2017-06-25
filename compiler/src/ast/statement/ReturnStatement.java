package ast.statement;

import ast.expression.*;

public class ReturnStatement extends Statement {
    private Expression expression;

    public ReturnStatement(Expression expression){
        super();

        if(expression != null){ // return;
            this.expression = expression;
            this.addChild(expression);
        }
    }

    public boolean hasExpression(){ return this.expression != null; }
}