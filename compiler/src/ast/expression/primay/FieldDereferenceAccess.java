package ast.expression.primay;

import ast.identifier.FieldIdentifier;

public class FieldDereferenceAccess extends Primary {
    Primary prefix;
    FieldIdentifier fieldIdentifier;

    public FieldDereferenceAccess(Primary prefix, FieldIdentifier fieldIdentifier){
        super();

        this.prefix = prefix;
        this.addChild(prefix);

        this.fieldIdentifier = fieldIdentifier;
        this.addChild(fieldIdentifier);
    }
}