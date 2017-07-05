package codegen;

import ast.*;
import ast.declaration.*;
import ast.definition.*;
import ast.statement.*;
import symboltable.*;
import symboltable.semanticsvisitor.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Set indexes for Definitions and Declarations:
 *  - FunctionDefinition
 *  - StructDefinition
 *  - FieldDefinition
 *  - VariableDeclaration
 * 
 * Based on the indexes the CodeGenerator will place and handle the Stackpointer
 */
public class IndexerVisitor extends SemanticsVisitor {
	
    private static final boolean DEBUG = true;

    int globals = 0;
	int fields = 0;
	int locals = 0;

	private int parameters = -1;

	Map<Integer, Scope> globalList = new HashMap<Integer, Scope>();

	public IndexerVisitor(SymbolTable table) {
		super(table);
    }

    @Override
	public void willVisit(ASTNode node) throws SymbolTableException {
		super.willVisit(node);
		if (node instanceof FunctionDefinition) {
            if (DEBUG) System.out.println("  [DEBUG] Set global index of FunctionDefinition " + node.getIdentifier() + " to " + globals);
            globalList.put(globals, this.getCurrentScope());
            ((FunctionDefinition) node).setIndex(globals);
            globals++;
        }
		else if (node instanceof StructDefinition) {
            if (DEBUG) System.out.println("  [DEBUG] Set global index of StructDefinition " + node.getIdentifier() + " to " + globals);
            globalList.put(globals, this.getCurrentScope());
            ((StructDefinition) node).setIndex(globals);
            globals++;
        }
	}

    @Override
	public boolean visit(ASTNode node) throws SymbolTableException {

		if (node instanceof FunctionDefinition) {
            
            FunctionDefinition functionDefinition = (FunctionDefinition) node;
            
            // Get Parameters
			for (VariableDeclaration param : functionDefinition.getParameters()) {
                if (DEBUG) System.out.println("  [DEBUG] FunctionDefinition " + node.getIdentifier() + " param " + param.getIdentifier() + " index: " + parameters);
				param.setIndex(parameters); 
				parameters--;
			}
			Block body = functionDefinition.getFunctionBlock();
			if (body != null) {
                // getLocalVariable not supported... maybe not needed (codegen reserve space for all local variables)?
                // will be set in didVisit ?

				//List<VariableDeclaration> vars = functionDefinition.getFunctionBlock().getLocalVariable();
				//if (vars != null)
				//	functionDefinition.setTotalVariables(vars.size()); // TODO
																					// REMOVE
			}
		}  else if (node instanceof StructDefinition) {
            if (DEBUG) System.out.println("  [DEBUG] StructDefinition " + node.getIdentifier() + " visit");
            // ...
        } else if (node instanceof FieldDefinition) {
				if (DEBUG) System.out.println("  [DEBUG] Set local Index of FieldDefinition " + node.getIdentifier() + " to " + fields);
                ((FieldDefinition) node).setIndex(fields);
				fields++;
		}
        // Problem with Parameters, will be double-checked for example, set index if it's not already set?
        else if (node instanceof VariableDeclaration) {
            if (DEBUG) System.out.println("  [DEBUG] Set local Index of VariableDeclaration " + node.getIdentifier() + " to " + locals);
			((VariableDeclaration) node).setIndex(locals);
			locals++;
		}
		return true;
    }

    
	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {

		if (node instanceof FunctionDefinition) {
            if (DEBUG) System.out.println("  [DEBUG] Finished FunctionDefinition " + node.getIdentifier() + " with " + locals + " local variables");
			((FunctionDefinition) node).setTotalLocalVariables(locals);
			locals = 0;
			parameters = -1;
		} else if (node instanceof StructDefinition) {
            if (DEBUG) System.out.println("  [DEBUG] Finished StructDefinition " + node.getIdentifier() + " with " + fields + " local fields");
            fields = 1;
        }

		super.didVisit(node);
    }


}