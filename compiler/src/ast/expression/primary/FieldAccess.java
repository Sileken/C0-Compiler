package ast.expression.primary;

import ast.identifier.FieldIdentifier;

public class FieldAccess extends Primary {
    FieldIdentifier fieldIdentifier;

    public FieldAccess(FieldIdentifier fieldIdentifier) {
        super();

        this.fieldIdentifier = fieldIdentifier;
        this.addChild(fieldIdentifier);
    }

    public Primary getPrimary() {
        return this.prefix;
    }
}