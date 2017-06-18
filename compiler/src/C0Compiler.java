import parser.*;
import ast.*;
import ast.identifier.*;
import ast.statement.*;
import symboltable.*;
import symboltable.semanticsvisitor.*;

public class C0Compiler {
  public static void main(String args[]) {
    C0Parser parser;

    if (args.length == 1) {
      System.out.println("C0 Compiler: Reading from file " + args[0] + " . . .");
      try {
        parser = new C0Parser(new java.io.FileInputStream(args[0]));
      } catch (java.io.FileNotFoundException e) {
        System.out.println("C0 Compiler: File " + args[0] + " not found.");
        return;
      }
    } else {
      //System.out.println("C0 Compiler: Usage :");
      //System.out.println("Please pass a C0 File-Path to the C0 Compiler.");
      System.out.println("C0 Compiler: Reading input from console.");
      parser = new C0Parser(System.in);
    }

    try {
      AST ast = parser.parseTree();
      SymbolTable table = typeLinking(ast);
      nameLinking(ast, table);
      typeChecking(ast, table);
      generateCode(ast, table);

    
      initializeASTNodes(ast);
      
      // ast.PrintPretty();
    } catch (ParseException e) {
      System.out.println("C0 Compiler: Encountered errors during parse.");
      e.printStackTrace();
    } catch (Exception e1) {
      System.out.println("C0 Compiler: Encountered errors during interpretation/tree building.");
      e1.printStackTrace();
    }
  }

  private static SymbolTable typeLinking(AST ast) throws Exception {
    final SymbolTable symbolTable = new SymbolTable();
      		
		ast.getRoot().accept(new TopDeclarationVisitor(symbolTable));
		System.out.println("C0 Compiler: Top Declarations constructed");

	  TypeLinker linker = new TypeLinker(symbolTable);
    ast.getRoot().accept(linker);
		System.out.println("C0 Compiler: Type Linking finished");
		
		ast.getRoot().accept(new DeepDeclarationVisitor(symbolTable));
		System.out.println("C0 Compiler: Deep Declaration constructed");

    return symbolTable;
  }

  private static void nameLinking(AST ast, SymbolTable symbolTable) throws Exception{
    NameLinker linker = new NameLinker(symbolTable);
		ast.getRoot().accept(linker);
		System.out.println("C0 Compiler: Name Linking finished");
  }

  private static void typeChecking(AST ast, SymbolTable symbolTable) throws Exception{
    TypeChecker checker = new TypeChecker(symbolTable);
		ast.getRoot().accept(checker);
		System.out.println("C0 Compiler: Type Checking finished");
  }

  private static void generateCode(AST ast, SymbolTable symbolTable) throws Exception{
  }  

  /**
   * Set additional information into the nodes of the nearly created AST.
   * <anmerkung> da AST als Referenz uebergeben wird ist return ueberfluessig (?) </anmerkung> 
   */
  private static void initializeASTNodes(AST ast) {
    final SymbolTable symbolTable = new SymbolTable();
    symbolTable.enterScope();
    createSymbolTable(symbolTable, ast.getRoot());

  }

  /**
   * Creates the symbol-table and checks for errors during the <symbol-table (hier richtigen Begriff)> phase.
   */
  private static void createSymbolTable(SymbolTable symbolTable, ASTNode astNode) {
    
    if (astNode instanceof Identifier) {
      symbolTable.addSymbol((Identifier) astNode);
      System.out.println("New identifier: " + ((Identifier) astNode).getName());

      // doMore stuff

    } else if (astNode instanceof Block) {
      symbolTable.enterScope();
      System.out.println("New Scope");

      // doMore stuff

    }

    // recursive traverse children
    for (ASTNode node : astNode.getChildren()) {
      createSymbolTable(symbolTable, node);
    }
  }
}