package ast.statement;

import ast.expression.*;

public class WhileStatement extends Statement {
    Expression condition;

    public WhileStatement(Expression condition){
        super();
        
        this.condition = condition;
        this.addChild(this.condition);
    }
}