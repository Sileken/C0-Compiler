package ast.definition;

import ast.type.*;
import ast.identifier.*;

public class FieldDefinition extends Definition {
    private Type fieldType;
    private FieldIdentifier fieldId;

    public FieldDefinition(Type fieldType, FieldIdentifier fieldId){
        this.fieldType = fieldType;
        this.fieldId = fieldId;
    }
}