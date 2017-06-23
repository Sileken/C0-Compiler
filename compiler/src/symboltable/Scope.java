package symboltable;

import ast.*;
import ast.expression.primary.name.*;

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

	public void listSymbols() {
		if (this instanceof BlockScope) {
			System.out.println("\tParent Scope: " + ((BlockScope) this).parent.getName());
		} else if (this instanceof StructTypeScope) {
			System.out.println("\tParent Scope: " + ((StructTypeScope) this).parent.getName());
		}
		System.out.println("\tReferences to: " + this.referenceNode);
		System.out.println("\tSymbols:");
		List<String> keys = new ArrayList<String>(this.symbols.keySet());
		//Collections.sort(keys);
		for (String key : keys) {
			System.out.println("\t\t" + this.symbols.get(key));
		}
		System.out.println();
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