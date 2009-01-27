/*******************************************************************************
* Copyright (c) 2007 IBM Corporation, 2008 CWI
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*    Jurgen Vinju (jurgen@vinju.org)

*******************************************************************************/

package org.eclipse.imp.pdb.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.imp.pdb.facts.type.FactTypeError;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeDeclarationException;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public class TestType extends TestCase {
	private static final int COMBINATION_UPPERBOUND = 5;

	private static TypeFactory ft = TypeFactory.getInstance();

	private static List<Type> basic = new LinkedList<Type>();
    private static List<Type> allTypes = new LinkedList<Type>();
    
	static {
		try {
			basic.add(ft.integerType());
			basic.add(ft.doubleType());
			basic.add(ft.sourceLocationType());
			basic.add(ft.sourceRangeType());
			basic.add(ft.stringType());

			allTypes.add(ft.valueType());
			allTypes.addAll(basic);

			for (int i = 0; i < 2; i++) {
				recombine();
			}
		} catch (FactTypeError e) {
			throw new RuntimeException("fact type error in setup", e);
		}
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	private static void recombine() throws FactTypeError {
		List<Type> newTypes = new LinkedList<Type>();
		int max1 = COMBINATION_UPPERBOUND;
		
		for (Type t1 : allTypes) {
			newTypes.add(ft.tupleType(t1));
			newTypes.add(ft.relType(t1));
			newTypes.add(ft.setType(t1));
			newTypes.add(ft.aliasType("type_" + allTypes.size() + newTypes.size(), t1));
			int max2 = COMBINATION_UPPERBOUND;
			
			for (Type t2 : allTypes) {
				newTypes.add(ft.tupleType(t1, t2));
				newTypes.add(ft.relType(t1, t2));
				int max3 = COMBINATION_UPPERBOUND;
				
				for (Type t3 : allTypes) {
					newTypes.add(ft.tupleType(t1, t2, t3));
					newTypes.add(ft.relType(t1, t2, t3));
					if (max3-- == 0) {
						break;
					}
				}
				if (max2-- == 0) {
					break;
				}
			}
		
			if (max1-- == 0) {
				break;
			}
		}
	
		allTypes.addAll(newTypes);
	}
	
	public void testRelations() {
		for (Type t : allTypes) {
			if (t.isSetType() && t.getElementType().isTupleType() && !t.isRelationType()) {
				fail("Sets of tuples should be relations");
			}
			if (t.isRelationType() && !t.getElementType().isTupleType()) {
				fail("Relations should contain tuples");
			}
		}
	}
	
	public void testADT() {
		Type E = ft.abstractDataType("E");
		
		assertFalse("Abstract data-types may be composed of other things than tree nodes",
				E.isSubtypeOf(ft.nodeType()));
		
		assertTrue(E.isSubtypeOf(ft.valueType()));
		
		Type i = ft.define(E, ft.integerType(), "i");
		Type s = ft.define(E, ft.stringType(), "s");

		assertFalse("define should simply return the adt type", 
				i != E || s != E);
		
		assertFalse("anonymous extensions should not be nodes", 
				i.isSubtypeOf(ft.nodeType()) || s.isSubtypeOf(ft.nodeType()));
		
		assertTrue("ints should now be subtypes of E", ft.integerType().isSubtypeOf(E));
		assertTrue("strings should now be subtypes of E", ft.stringType().isSubtypeOf(E));
		
		assertTrue("lub of two anonymous types should skip adt", 
				ft.integerType().lub(ft.stringType()) == ft.valueType());
		assertTrue(ft.integerType().lub(E) == E);
		assertTrue(E.lub(ft.integerType()) == E);
		
		Type f = ft.constructor(E, "f", ft.integerType(), "i");
		Type g = ft.constructor(E, "g", ft.integerType(), "j");

		Type a = ft.aliasType("a", ft.integerType());
		
		assertFalse(f.isSubtypeOf(ft.integerType()) || f.isSubtypeOf(ft.stringType()) || f.isSubtypeOf(a));
		assertFalse(g.isSubtypeOf(ft.integerType()) || g.isSubtypeOf(ft.stringType()) || g.isSubtypeOf(a));
		assertFalse("constructors are subtypes of the adt", !f.isSubtypeOf(E) || !g.isSubtypeOf(E));
		
		assertFalse ("alternative constructors should be incomparable", f.isSubtypeOf(g) || g.isSubtypeOf(f));
		
		assertTrue("A constructor should be a node", f.isSubtypeOf(ft.nodeType()));
		assertTrue("A constructor should be a node", g.isSubtypeOf(ft.nodeType()));
		
		try {
			ft.define(E, ft.abstractDataType("F"), "f");
			fail("nesting of ADT's should not be allowed");
		}
		catch (TypeDeclarationException e) {
			// should happen
		}
		
	}

	public void testIsSubtypeOf() {
		for (Type t : allTypes) {
			if (!t.isSubtypeOf(t)) {
				fail("any type should be a subtype of itself: " + t);
			}
			
			if (t.isSetType() && t.getElementType().isTupleType() && !t.isRelationType()) {
				fail("Sets of tuples should be relations");
			}
		}
		
		for (Type t1 : allTypes) {
			for (Type t2 : allTypes) {
				if (t1 != t2 && t1.isSubtypeOf(t2) && t2.isSubtypeOf(t1)) {
					if (!t1.isAliasType() && !t2.isAliasType()) {
						System.err.println("Failure:");
						System.err.println(t1 + " <= " + t2 + " && " + t2 + " <= " + t1);
						fail("subtype of should not be symmetric");
					}
				}
			}
		}
		
		for (Type t1 : allTypes) {
			for (Type t2 : allTypes) {
				if (t1.isSubtypeOf(t2)) {
					for (Type t3 : allTypes) {
						if (t2.isSubtypeOf(t3)) {
							if (!t1.isSubtypeOf(t3)) {
								System.err.println("FAILURE");
								System.err.println("\t" + t1 + " <= " + t2 + " <= " + t3);
								System.err.println("\t" + t1 + " !<= " + t3);
								fail("subtype should be transitive");
							}
						}
					}
				}
			}
		}
	}

	public void testLub() {
		for (Type t : allTypes) {
			if (t.lub(t) != t) {
				fail("lub should be idempotent: " + t + " != " + t.lub(t));
			}
		}
		
		for (Type t1 : allTypes) {
			for (Type t2 : allTypes) {
				Type lub1 = t1.lub(t2);
				Type lub2 = t2.lub(t1);
				
				
				if (lub1 != lub2) {
					System.err.println("Failure:");
					System.err.println(t1 + ".lub(" + t2 + ") = " + lub1);
					System.err.println(t2 + ".lub(" + t1 + ") = " + lub2);
					fail("lub should be commutative");
				}
			}
		}
	}

	
	public void testGetTypeDescriptor() {
		int count = 0;
		for (Type t1 : allTypes) {
			for (Type t2 : allTypes) {
				if (t1.toString().equals(t2.toString())) {
					if (t1 != t2) {
						System.err.println("Type descriptors should be canonical:" + t1.toString() + " == " + t2.toString());
					}
				}
				if (count ++ > 10000) {
					return;
				}
			}
		}
	}

	public void testMatchAndInstantiate() {
		Type X = ft.parameterType("X");
		Map<Type, Type> bindings = new HashMap<Type, Type>();
		
		Type subject = ft.integerType();
		X.match(subject, bindings);
		
		if (!bindings.get(X).equals(subject)) {
			fail("simple match failed");
		}
		
		if (!X.instantiate(bindings).equals(subject)) {
			fail("instantiate failed");
		}
		
		Type relXX = ft.relType(X, X);
		bindings.clear();
		subject = ft.relType(ft.integerType(), ft.integerType());
		relXX.match(subject, bindings);
		
		if (!bindings.get(X).equals(ft.integerType())) {
			fail("relation match failed");
		}
		
		if (!relXX.instantiate(bindings).equals(subject)) {
			fail("instantiate failed");
		}
		
		bindings.clear();
		subject = ft.relType(ft.integerType(), ft.doubleType());
		relXX.match(subject, bindings);
		
		Type lub = ft.integerType().lub(ft.doubleType());
		if (!bindings.get(X).equals(lub)) {
			fail("lubbing during matching failed");
		}
		
		if (!relXX.instantiate(bindings).equals(ft.relType(lub, lub))) {
			fail("instantiate failed");
		}

	}
	
}
