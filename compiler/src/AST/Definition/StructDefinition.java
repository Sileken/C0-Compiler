package ast.definition;

import java.util.ArrayList;
import java.util.List;
import ast.identifier.*;
import ast.type.*;

public class StructDefinition extends Definition {
  private StructType structType;
  private List<FieldDefinition> fieldDefs = new ArrayList<FieldDefinition>();

  public StructDefinition(StructType structType, StructIdentifier structId, List<FieldDefinition> fieldDefs){
    super(structId);
    this.structType = structType;

    if(fieldDefs != null && !fieldDefs.isEmpty()){
        this.fieldDefs = fieldDefs;
    }
  }
}