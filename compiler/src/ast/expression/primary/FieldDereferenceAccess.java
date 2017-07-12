package ast.expression.primary;

import ast.identifier.FieldIdentifier;

public class FieldDereferenceAccess extends Primary {
    FieldIdentifier fieldIdentifier;

    public FieldDereferenceAccess(FieldIdentifier fieldIdentifier) {
        super();

        this.fieldIdentifier = fieldIdentifier;
        this.addChild(fieldIdentifier);
    }

    public FieldIdentifier getFieldIdentifier() {
        return fieldIdentifier;
    }
}