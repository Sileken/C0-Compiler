package symboltable;

import ast.*;
import ast.expression.primary.name.*;

import logger.*;

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

	public String listSymbols() {
		String out = "";

		if (this instanceof BlockScope) {
			out += "\n\tParent Scope: " + ((BlockScope) this).parent.getName();
		} else if (this instanceof StructTypeScope) {
			out += "\n\tParent Scope: " + ((StructTypeScope) this).parent.getName();
		}
		out += "\n\tReferences to: " + this.referenceNode;
		out += "\n\tSymbols:";
		List<String> keys = new ArrayList<String>(this.symbols.keySet());
		for (String key : keys) {
			out += "\n\t\t" + this.symbols.get(key);
		}
		out += "\n";

		return out;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Symbol> getSymbols(){
		return  new ArrayList<Symbol>(this.symbols.values());
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