package ast;

import java.util.ArrayList;
import java.util.List;

public abstract class ASTNode {
    private ASTNode parent = null;
    private String identifier = new String();
    private List<ASTNode> childrenList = new ArrayList<ASTNode>();

	public ASTNode() {
	}

    /**
	 * @return the parent
	 */
	public ASTNode getParent() {
		return parent;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

    public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

    public void addChild(ASTNode child) {
		child.parent = this;
		this.childrenList.add(child);
	}

	public void addChilds(List<? extends ASTNode> childs) {
		for (ASTNode child : childs) {
			this.addChild(child);
		}
	}

	@Override
	public final String toString() {
		String str = "";
		str += "<" + this.getClass().getSimpleName() + ">";
		if(this.identifier.length() > 0) str += " " + this.identifier + " |";
		for (ASTNode child : this.childrenList) {
			str += " " + child.getClass().getSimpleName() + " { " + child.toString() + " } |";
		}
		if (this.parent != null)
			str += " parent: " + this.parent.getClass().getSimpleName();
		return str;
	}
}