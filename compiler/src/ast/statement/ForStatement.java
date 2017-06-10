package ast.statement;

import ast.expression.*;

public class ForStatement extends Statement {
    private Expression initialization;
    private Expression condition;
    private StatementExpression increment;
    private Statement statement;

    public ForStatement(Expression initialization, Expression condition, StatementExpression increment, Statement statement){
        super();

        if(initialization != null){
            this.initialization = initialization;
            this.addChild(this.initialization);
        }

        this.condition = condition;
        this.addChild(this.condition);

        if(increment != null){
            this.increment = increment;
            this.addChild(this.increment);
        }

        this.statement = statement;
        this.addChild(this.statement);
    }
}