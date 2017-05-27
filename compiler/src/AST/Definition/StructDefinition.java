package ast.definition;

import java.util.ArrayList;
import java.util.List;
import ast.identifier.*;

public class StructDefinition extends Definition {
  private StructIdentifier structId;
  private List<FieldDefinition> fieldDefs = new ArrayList<FieldDefinition>();

  public StructDefinition(StructIdentifier structId, List<FieldDefinition> fieldDefs){
    this.structId = structId;
    if(fieldDefs != null && !fieldDefs.isEmpty()){
        this.fieldDefs = fieldDefs;
    }
  }
}