package ast.definition;

import ast.type.*;
import ast.identifier.*;

public class FieldDefinition extends Definition {
    private Type fieldType;

    public FieldDefinition(Type fieldType, FieldIdentifier fieldId){
        super(fieldId);
        
        this.fieldType = fieldType;
        this.addChild(this.fieldType);
    }
}