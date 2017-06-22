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
        parser = new C0Parser(args[0]);
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
      //ast.PrintPretty();
      SymbolTable table = typeLinking(ast);
      table.listScopes(); 
      nameLinking(ast, table);
      typeChecking(ast, table);
      generateCode(ast, table);

        
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
      		
		ast.getRoot().accept(new GlobalDeclarationAndDefinitionVisitor(symbolTable));
		System.out.println("C0 Compiler: Global Declarations constructed");
		
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
}