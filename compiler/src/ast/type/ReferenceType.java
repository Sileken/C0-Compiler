package ast.type;

public class ReferenceType extends Type {
    private Type innerType;

    public ReferenceType(Type innerType) {
        super();

        this.innerType = innerType;
        this.addChild(this.innerType);
    }

    public Type getInnerType() {
        return innerType;
    }

    @Override
    public String getFullyQualifiedName() {
        String fullyQualifedName = "*";
        Type currentInnerType = this.getInnerType();
        while (currentInnerType instanceof ReferenceType) {
            fullyQualifedName += "*";
            currentInnerType = ((ReferenceType) currentInnerType).getInnerType();
        }

        return fullyQualifedName + currentInnerType.getFullyQualifiedName();
    }

    @Override
    public String getIdentifier() {
        String name = "*";
        Type currentInnerType = this.getInnerType();
        while (currentInnerType instanceof ReferenceType) {
            name += "*";
            currentInnerType = ((ReferenceType) currentInnerType).getInnerType();
        }
        name += currentInnerType.getIdentifier();
        return name;
    }
}