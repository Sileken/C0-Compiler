package ast.expression.primay;

import ast.identifier.FieldIdentifier;

public class FieldAccess extends Primary {
    Primary prefix;
    FieldIdentifier fieldIdentifier;

    public FieldAccess(Primary prefix, FieldIdentifier fieldIdentifier){
        super();

        this.prefix = prefix;
        this.addChild(prefix);

        this.fieldIdentifier = fieldIdentifier;
        this.addChild(fieldIdentifier);
    }
}