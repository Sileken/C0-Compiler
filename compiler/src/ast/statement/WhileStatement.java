package ast.statement;

import ast.expression.*;

public class WhileStatement extends Statement {
    private Expression condition;
    private Statement statement;

    public WhileStatement(Expression condition, Statement statement){
        super();
        
        this.condition = condition;
        this.statement = statement;
        this.addChild(this.condition);
        this.addChild(this.statement);
    }

    public Expression getWhileCondition() {
        return this.condition;
    }

    public Statement getWhileStatement() {
        return this.statement;
    }
}