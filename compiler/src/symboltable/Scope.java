package symboltable;

import ast.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

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

	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() + "> " + this.name;
	}
}