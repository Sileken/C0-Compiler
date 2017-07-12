package ast.statement;

import java.util.ArrayList;
import java.util.List;

public class Block extends Statement {
    private List<Statement> statements = new ArrayList<Statement>();

    public Block(List<Statement> statements) {
        super();

        if (statements != null && !statements.isEmpty()) {
            this.statements = statements;
            this.addChilds(this.statements);
        }
    }
}