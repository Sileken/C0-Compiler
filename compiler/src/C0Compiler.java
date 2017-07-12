import java.lang.IllegalArgumentException;

import parser.*;
import ast.*;
import ast.identifier.*;
import ast.statement.*;
import codegen.*;
import symboltable.*;
import symboltable.semanticsvisitor.*;
import utils.*;
import utils.Logger.LogLevel;

public class C0Compiler {
  private static String compilerName = "C0 Compiler";

  public static void main(String args[]) {
    Logger.enableLogging();
    Logger.setLogLevel(LogLevel.DEBUG);

    C0Parser parser = getC0ParserByArguments(args);

    try {
      AST ast = parser.parseTree();

      if (Logger.getLogLevel().ordinal() <= LogLevel.TRACE.ordinal()) {
        Logger.log("\nListing AST:");
        Logger.log(ast.PrintPretty());
        Logger.log("\nFinished listing AST");
      }

      final SymbolTable table = new SymbolTable();
      typeLinking(ast, table);

      if (Logger.getLogLevel().ordinal() <= LogLevel.TRACE.ordinal()) {
        Logger.log("\nListing Scopes:");
        Logger.log(table.listScopes());
        Logger.log("\nFinished listing Scopes");
      }

      nameLinking(ast, table);
      typeChecking(ast, table);
      indexing(ast);
      generateCode(ast, table);  
    } catch (ParseException parseException) {
      Logger.log(compilerName + ": Encountered errors during parse.");
      parseException.printStackTrace();
    } catch (Exception exception) {
      Logger.log(compilerName + ": Encountered errors during interpretation/tree building.");
      exception.printStackTrace();
    }
  }

  private static C0Parser getC0ParserByArguments(String args[]) throws IllegalArgumentException {
    C0Parser parser = null;

    if (args.length >= 1) {
      Logger.log("\n" + compilerName + ": Reading from file " + args[0]);
      try {
        parser = new C0Parser(args[0]);
        Logger.log(compilerName + ": Finished reading from file " + args[0]);
      } catch (java.io.FileNotFoundException e) {
        String errorMsg = compilerName + ": File " + args[0] + " not found.";
        Logger.error(errorMsg);
        throw new IllegalArgumentException(errorMsg);
      }
    } else {
      Logger.log(compilerName + ": Usage :");
      Logger.log("Please pass a C0 File-Path to the " + compilerName + ".");
    }

    return parser;
  }

  private static void typeLinking(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.log("\n" + compilerName + ": Typ Linking startet");

    Logger.debug(compilerName + ": Typ Linking for Global Declarations startet");
    ast.getRoot().accept(new GlobalDeclarationAndDefinitionVisitor(symbolTable));
    Logger.debug(compilerName + ": Typ Linking for Global Declarations finished");

    Logger.log("");
    Logger.debug(compilerName + ": Typ Linking for Deep Declarations startet");
    ast.getRoot().accept(new DeepDeclarationVisitor(symbolTable));
    Logger.debug(compilerName + ": Typ Linking for Deep Declarations finished");

    Logger.log(compilerName + ": Typ Linking finished");
  }

  private static void nameLinking(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.log("\n" + compilerName + ": Name Linking startet");
    ast.getRoot().accept(new NameLinker(symbolTable));
    Logger.log(compilerName + ": Name Linking finished");
  }

  private static void typeChecking(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.log("\n" + compilerName + ": Type Checking startet");
    ast.getRoot().accept(new TypeChecker(symbolTable));
    Logger.log(compilerName + ": Type Checking finished");
  }

  private static void indexing(AST ast) throws Exception {
    Logger.log("\n" + compilerName + ": Indexing staret");
    ast.getRoot().accept(new IndexerVisitor());
    Logger.log(compilerName + ": Indexing finished");
  }

  private static void generateCode(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.log("\n" + compilerName + ": Code Generation started");
    ast.getRoot().accept(new CodeGenerator(symbolTable));
    Logger.log(compilerName + ": Code Generation finished");
  }
}