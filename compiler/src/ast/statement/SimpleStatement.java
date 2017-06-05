package ast.statement;

import ast.expression.*;
import ast.expression.simple.*;

public class SimpleStatement extends Statement {
    private Simple simple;

    public SimpleStatement(Simple simple){
        super();
        this.simple = simple;
        this.addChild(this.simple);
    }
}