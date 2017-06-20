package ast;

import java.util.ArrayList;
import java.util.List;

import ast.visitor.ASTVisitor;

public abstract class ASTNode {
	protected ASTNode parent = null;
	protected String identifier = new String();
	protected List<ASTNode> childrenList = new ArrayList<ASTNode>();

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

	public final void accept(ASTVisitor visitor) throws Exception {
		visitor.willVisit(this);
		//System.out.println("Visiting <" + this.getClass().getSimpleName() + ">");

		if (visitor.visit(this)) {
			for (ASTNode childNode : this.childrenList) {
				childNode.accept(visitor);
			}
		}

		visitor.didVisit(this);
	}

	public void PrintPretty(String indent, boolean last) {
		System.out.print(indent);
		if (last) {
			System.out.print("\\-");
			indent += "  ";
		} else {
			System.out.print("|-");
			indent += "| ";
		}
		System.out.println(this.getClass().getSimpleName());

		for (int i = 0; i < this.childrenList.size(); i++) {
			this.childrenList.get(i).PrintPretty(indent, i == this.childrenList.size() - 1);
		}
	}

	public List<ASTNode> getChildren() {
		return this.childrenList;
	}
}