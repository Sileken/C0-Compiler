package ast;

public class AST {
  private ASTNode root;

  public ASTNode getRoot() {
    return this.root;
  }

  public void setRoot(ASTNode node) {
    this.root = node;
  }

  @Override
  public String toString() {
    return this.root.toString();
  }
}