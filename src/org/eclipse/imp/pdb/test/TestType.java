/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.pdb.test;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.imp.pdb.facts.type.DoubleType;
import org.eclipse.imp.pdb.facts.type.FactTypeError;
import org.eclipse.imp.pdb.facts.type.IntegerType;
import org.eclipse.imp.pdb.facts.type.ListType;
import org.eclipse.imp.pdb.facts.type.NamedType;
import org.eclipse.imp.pdb.facts.type.NumberType;
import org.eclipse.imp.pdb.facts.type.RelationType;
import org.eclipse.imp.pdb.facts.type.SetType;
import org.eclipse.imp.pdb.facts.type.SourceLocationType;
import org.eclipse.imp.pdb.facts.type.SourceRangeType;
import org.eclipse.imp.pdb.facts.type.StringType;
import org.eclipse.imp.pdb.facts.type.TupleType;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.ValueType;

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
			basic.add(ft.objectType(Integer.class));

			allTypes.add(ft.valueType());
			allTypes.add(ft.numberType());
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
			newTypes.add(ft.tupleTypeOf(t1));
			newTypes.add(ft.relTypeOf(t1));
			newTypes.add(ft.setTypeOf(t1));
			newTypes.add(ft.namedType("type_" + allTypes.size() + newTypes.size(), t1));
			int max2 = COMBINATION_UPPERBOUND;
			
			for (Type t2 : allTypes) {
				newTypes.add(ft.tupleTypeOf(t1, t2));
				newTypes.add(ft.relTypeOf(t1, t2));
				int max3 = COMBINATION_UPPERBOUND;
				
				for (Type t3 : allTypes) {
					newTypes.add(ft.tupleTypeOf(t1, t2, t3));
					newTypes.add(ft.relTypeOf(t1, t2, t3));
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

	public void testIsSubtypeOf() {
		for (Type t : allTypes) {
			if (!t.isSubtypeOf(t)) {
				fail("any type should be a subtype of itself");
			}
		}
		
		for (Type t1 : allTypes) {
			for (Type t2 : allTypes) {
				if (t1 != t2 && t1.isSubtypeOf(t2) && t2.isSubtypeOf(t1)) {
					
					if (t1.isRelationType() && ((RelationType) t1).toSet() == t2) {
						// rel[t1,...,tn] == set[<t1,...,tn>]
						continue;
					}
					if (t2.isRelationType() && ((RelationType) t2).toSet() == t1) {
						// set[<t1,...,tn>] == rel[t1,...,tn] 
						continue;
					}
					
					System.err.println("Failure:");
					System.err.println(t1 + " <= " + t2 + " && " + t2 + " <= " + t1);
					fail("subtype of should not be symmetric");
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
				fail("lub should be idempotent");
			}
		}
		
		for (Type t1 : allTypes) {
			for (Type t2 : allTypes) {
				Type lub1 = t1.lub(t2);
				Type lub2 = t2.lub(t1);
				
				
				if (lub1 != lub2) {
					if (lub1.isRelationType() && ((RelationType) lub1).toSet() == lub2) {
						continue;
					}
					if (lub2.isRelationType() && ((RelationType) lub2).toSet() == lub1) {
						continue;
					}
					
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
				if (t1.getTypeDescriptor().equals(t2.getTypeDescriptor())) {
					if (t1 != t2) {
						System.err.println("Type descriptors should be canonical:" + t1.getTypeDescriptor() + " == " + t2.getTypeDescriptor());
					}
				}
				if (count ++ > 10000) {
					return;
				}
			}
		}
	}

	public void testGetBaseType() {
		for (Type t : basic) {
			if (t.getBaseType() != t) {
				fail("getBaseType of basic types should be idempotent");
			}
		}
		for (Type t : allTypes) {
			Type base = t.getBaseType();
			
			if (base.isNamedType()) {
				fail("base types can not be named");
			}
			
			if (base != base.getBaseType()) {
				fail("getBaseType should be idempotent");
			}
		}
	}

	public void testIsRelationType() {
		for (Type t : allTypes) {
			if (t.isRelationType()) {
				if (!(t instanceof RelationType)) {
					fail("relation type should be class RelationType");
				}
			}
			if (t instanceof RelationType) {
				if (!(t.isRelationType())) {
					fail("relation type should be class RelationType");
				}
			}
		}
	}

	public void testIsSetType() {
		for (Type t : allTypes) {
			if (t.isSetType()) {
				if (!(t instanceof SetType)) {
					fail("set type should be class SetType");
				}
			}
			if (t instanceof SetType) {
				if (!(t.isSetType())) {
					fail("set type should be class SetType");
				}
			}
		}
	}

	public void testIsTupleType() {
		for (Type t : allTypes) {
			if (t.isTupleType()) {
				if (!(t instanceof TupleType)) {
					fail("tuple type should class TupleType");
				}
			}
			if (t instanceof TupleType) {
				if (!(t.isTupleType())) {
					fail("tuple type should class TupleType");
				}
			}
		}
	}

	public void testIsListType() {
		for (Type t : allTypes) {
			if (t.isListType()) {
				if (!(t instanceof ListType)) {
					fail("list type should class ListType");
				}
			}
			if (t instanceof ListType) {
				if (!(t.isListType())) {
					fail("list type should class ListType");
				}
			}
		}
	}

	public void testIsIntegerType() {
		for (Type t : allTypes) {
			if (t.isIntegerType()) {
				if (!(t instanceof IntegerType)) {
					fail("integer type should class IntegerType");
				}
			}
			if (t instanceof IntegerType) {
				if (!(t.isIntegerType())) {
					fail("integer type should class IntegerType");
				}
			}
		}
	}

	public void testIsNumberType() {
		for (Type t : allTypes) {
			if (t.isNumberType()) {
				if (!(t instanceof NumberType)) {
					fail("number type should class NumberType");
				}
			}
			if (t instanceof NumberType) {
				if (!t.isNumberType()) {
					fail("number type should class NumberType");
				}
			}
		}
	}

	public void testIsDoubleType() {
		for (Type t : allTypes) {
			if (t.isDoubleType()) {
				if (!(t instanceof DoubleType)) {
					fail("double type should class DoubleType");
				}
			}
			if (t instanceof DoubleType) {
				if (!(t.isDoubleType())) {
					fail("double type should class DoubleType");
				}
			}
		}
	}

	public void testIsStringType() {
		for (Type t : allTypes) {
			if (t.isStringType()) {
				if (!(t instanceof StringType)) {
					fail("string type should class StringType");
				}
			}
		}
		for (Type t : allTypes) {
			if (t instanceof StringType) {
				if (!(t.isStringType())) {
					fail("string type should class StringType");
				}
			}
		}
	}

	public void testIsSourceLocationType() {
		for (Type t : allTypes) {
			if (t.isSourceLocationType()) {
				if (!(t instanceof SourceLocationType)) {
					fail("sourceLocation type should class SourceLocationType");
				}
			}
		}
		for (Type t : allTypes) {
			if (t instanceof SourceLocationType) {
				if (!(t.isSourceLocationType())) {
					fail("sourceLocation type should class SourceLocationType");
				}
			}
		}
	}

	public void testIsSourceRangeType() {
		for (Type t : allTypes) {
			if (t.isSourceRangeType()) {
				if (!(t instanceof SourceRangeType)) {
					fail("source range type should class SourceRangeType");
				}
			}
			if (t instanceof SourceRangeType) {
				if (!t.isSourceRangeType()) {
					fail("source range type should class SourceRangeType");
				}
			}
		}
	}

	public void testIsNamedType() {
		for (Type t : allTypes) {
			if (t.isNamedType()) {
				if (!(t instanceof NamedType)) {
					fail("named type should class NamedType");
				}
			}
			if (t instanceof NamedType) {
				if (!t.isNamedType()) {
					fail("named type should class NamedType");
				}
			}
		}
	}

	public void testIsValueType() {
		for (Type t : allTypes) {
			if (t.isValueType()) {
				if (!(t instanceof ValueType)) {
					fail("value type should class ValueType");
				}
			}
			if (t instanceof ValueType) {
				if (!(t.isValueType())) {
					fail("value type should class ValueType");
				}
			}
		}
	}

}
