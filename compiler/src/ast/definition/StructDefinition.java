package ast.definition;

import java.util.ArrayList;
import java.util.List;
import ast.identifier.*;
import ast.type.*;

public class StructDefinition extends Definition {
  private StructType structType;
  private List<FieldDefinition> fieldDefs = new ArrayList<FieldDefinition>();
  private int totalFields;

  public StructDefinition(StructType structType, StructIdentifier structId, List<FieldDefinition> fieldDefs){
    super(structId);

    this.structType = structType;
    this.addChild(this.structType);

    if(fieldDefs != null && !fieldDefs.isEmpty()){
        this.fieldDefs = fieldDefs;
        this.addChilds(this.fieldDefs);
    }
    this.totalFields = 0;
  }

  public Type getType() {
		return structType;
	}

  public void setTotalFields(int totalFields) {
    this.totalFields = totalFields;
  }

  public int getTotalFields() {
    return this.totalFields;
  }
}