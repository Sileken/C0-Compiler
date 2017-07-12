package ast.statement;

import ast.expression.*;

public class ElseStatement extends Statement {
    Statement elseStatement;

    public ElseStatement(Statement elseStatement) {
        super();

        this.elseStatement = elseStatement;
        this.addChild(this.elseStatement);
    }
}