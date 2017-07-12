package symboltable;

import ast.*;
import ast.expression.primary.name.*;

import utils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/** A Scope contains a list of symbol and provides functions to create and find specific symbols */
public class Scope {
	protected String name;
	protected LinkedHashMap<String, Symbol> symbols;
	protected ASTNode referenceNode;

	public Scope(String name, ASTNode referenceNode) {
		this.name = name;
		this.symbols = new LinkedHashMap<String, Symbol>();
		this.referenceNode = referenceNode;
	}

	public void putSymbol(String key, Symbol symbol) {
		Logger.debug("Put symbol \"" + symbol.getName() + "\" to scope: " + this.getName());
		this.symbols.put(key, symbol);
	}

	public void listSymbols() {
		if (this instanceof BlockScope) {
			Logger.log("\tParent Scope: " + ((BlockScope) this).parent.getName());
		} else if (this instanceof StructTypeScope) {
			Logger.log("\tParent Scope: " + ((StructTypeScope) this).parent.getName());
		}
		Logger.log("\tReferences to: " + this.referenceNode);
		Logger.log("\tSymbols:");
		List<String> keys = new ArrayList<String>(this.symbols.keySet());
		for (String key : keys) {
			Logger.log("\t\t" + this.symbols.get(key));
		}
		Logger.log("");
	}

	public String getName() {
		return name;
	}

	protected List<Symbol> findEntriesWithSuffix(Collection<Symbol> symbols, String suffix) {
		List<Symbol> matchedSymbols = new ArrayList<Symbol>();
		for (Symbol entry : symbols) {
			if (entry.getName().endsWith(suffix)) {
				matchedSymbols.add(entry);
			}
		}
		return matchedSymbols;
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() + "> " + this.name;
	}
}