package codegen;

import ast.*;
import ast.declaration.*;
import ast.definition.*;
import ast.statement.*;
import ast.visitor.ASTVisitor;
import logger.Logger;

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
 * Based on the indexes the CodeGenerator will place and handle the StackPointer
 *
 * Currently inefficiente local variables (declarations within a Block will be persistent)
 */
public class IndexerVisitor extends ASTVisitor {

	private int globals; /* index for functions */
	private int fields; /* index for struct-fields */
	private int parameters; /* index for parameters (negative) */
	private int locals; /* index for local variables */

	public IndexerVisitor() {
		this.globals = -1; /* global index begins at zero (will be incremented before used) */
		this.fields = -1; /* fields begin at index 0 (because of the addition with the register pointer) */
		this.parameters = -2; /* parameters begin at index -3 (will be decremented), previous EP and SP are stored */
		this.locals = 0; /* like fields */
	}

	/* Ignore function and struct declarations, the important parts are the definitions */
	@Override
	public void willVisit(ASTNode node) throws Exception {
		if (node instanceof FunctionDefinition || node instanceof StructDefinition) {
			globals++;
			Logger.debug("Set global index of " + node.getClass().getSimpleName() + " " + node.getIdentifier() + " to "
					+ globals);
			((Definition) node).setIndex(globals);
		}
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof FunctionDefinition) {
			// Set indexes for parameters
			for (VariableDeclaration param : ((FunctionDefinition) node).getParameters()) {
				parameters--;
				param.setIndex(parameters);
				Logger.debug("FunctionDefinition " + node.getIdentifier() + " param " + param.getIdentifier()
						+ " index: " + parameters);
			}
		} else if (node instanceof StructDefinition) {
			Logger.debug("StructDefinition " + node.getIdentifier() + " visit");
			// nothing to do ? yes first field has index 0
		} else if (node instanceof FieldDefinition) {
			fields++;
			((FieldDefinition) node).setIndex(fields);
			Logger.debug("Set local index of FieldDefinition " + node.getIdentifier() + " to " + fields);
		} else if (node instanceof VariableDeclaration) {
			// Do not overwrite indexes (e.g. parameters are also VariableDeclaration's)
			if (((VariableDeclaration) node).getIndex() == 0) {
				locals++;
				((VariableDeclaration) node).setIndex(locals);
				Logger.debug("Set local index of VariableDeclaration " + node.getIdentifier() + " to " + locals);
			}
		}
		return true;
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
		if (node instanceof FunctionDefinition) {
			Logger.debug("FunctionDefinition " + node.getIdentifier() + " contains " + (Math.abs(parameters) - 2)
					+ " parameters and " + locals + " local variables");
			((FunctionDefinition) node).setTotalLocalVariables(locals);
			// Reset parameter and local variable count
			locals = 0;
			parameters = 0;
		} else if (node instanceof StructDefinition) {
			Logger.debug("StructDefinition " + node.getIdentifier() + " contains " + fields + " fields");
			((StructDefinition) node).setTotalFields(fields);
			// Reset struct field count
			fields = -1;
		}
	}
}