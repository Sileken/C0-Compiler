package ast.type;

public class ReferenceType extends Type {
    private Type innerType;

    public ReferenceType(Type innerType){
        super();        

        this.innerType = innerType;
        this.addChild(this.innerType);
    }
}