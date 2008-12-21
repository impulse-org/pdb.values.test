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

import junit.framework.TestCase;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.impl.hash.ValueFactory;
import org.eclipse.imp.pdb.facts.type.FactTypeError;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeDeclarationException;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public class TestTypeFactory extends TestCase {
	private TypeFactory ft = TypeFactory.getInstance();

	private ValueFactory ff = ValueFactory.getInstance();

	private Type[] types = new Type[] { ft.integerType(), ft.doubleType(),
			ft.sourceLocationType(), ft.sourceRangeType(), ft.valueType(),
			ft.listType(ft.integerType()), ft.setType(ft.doubleType()) };

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetInstance() {
		if (TypeFactory.getInstance() != ft) {
			fail("getInstance did not return the same reference");
		}
	}

	public void testGetTypeByDescriptor() {
		// TODO: needs to be tested, after we've implemented it
	}

	public void testValueType() {
		if (ft.valueType() != ft.valueType()) {
			fail("valueType should be canonical");
		}
	}

	public void testIntegerType() {
		if (ft.integerType() != ft.integerType()) {
			fail("integerType should be canonical");
		}
	}

	public void testDoubleType() {
		if (ft.doubleType() != ft.doubleType()) {
			fail("doubleType should be canonical");
		}
	}

	public void testStringType() {
		if (ft.stringType() != ft.stringType()) {
			fail("stringType should be canonical");
		}
	}

	public void testSourceRangeType() {
		if (ft.sourceRangeType() != ft.sourceRangeType()) {
			fail("sourceRangeType should be canonical");
		}
	}

	public void testSourceLocationType() {
		if (ft.sourceLocationType() != ft.sourceLocationType()) {
			fail("sourceLocationType should be canonical");
		}
	}

	public void testTupleTypeOfType() {
		Type t = ft.tupleType(types[0]);

		if (t != ft.tupleType(types[0])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 1);
	}

	public void testTupleTypeOfTypeType() {
		Type t = ft.tupleType(types[0], types[1]);

		if (t != ft.tupleType(types[0], types[1])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 2);
	}

	public void testTupleTypeOfTypeTypeType() {
		Type t = ft.tupleType(types[0], types[1], types[2]);

		if (t != ft.tupleType(types[0], types[1], types[2])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 3);
	}

	public void testTupleTypeOfTypeTypeTypeType() {
		Type t = ft.tupleType(types[0], types[1], types[2], types[3]);

		if (t != ft.tupleType(types[0], types[1], types[2], types[3])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 4);
	}

	public void testTupleTypeOfTypeTypeTypeTypeType() {
		Type t = ft.tupleType(types[0], types[1], types[2], types[3],
				types[4]);

		if (t != ft.tupleType(types[0], types[1], types[2], types[3],
				types[4])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 5);
	}

	public void testTupleTypeOfTypeTypeTypeTypeTypeType() {
		Type t = ft.tupleType(types[0], types[1], types[2], types[3],
				types[4], types[5]);

		if (t != ft.tupleType(types[0], types[1], types[2], types[3],
				types[4], types[5])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 6);
	}

	public void testTupleTypeOfTypeTypeTypeTypeTypeTypeType() {
		Type t = ft.tupleType(types[0], types[1], types[2], types[3],
				types[4], types[5], types[6]);

		if (t != ft.tupleType(types[0], types[1], types[2], types[3],
				types[4], types[5], types[6])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 7);
	}

	private void testTupleTypeOf(Type t, int width) {

		if (t.getArity() != width) {
			fail("tuple arity broken");
		}

		for (int i = 0; i < t.getArity(); i++) {
			if (t.getFieldType(i) != types[i % types.length]) {
				fail("Tuple field type unexpected");
			}
		}
	}

	private void testRelationTypeOf(Type t, int width) {

		if (t.getArity() != width) {
			fail("relation arity broken");
		}

		for (int i = 0; i < t.getArity(); i++) {
			if (t.getFieldType(i) != types[i % types.length]) {
				fail("Relation field type unexpected");
			}
		}
	}

	public void testTupleTypeOfIValueArray() {
		// a and b shadow the 'types' field
		IValue[] a = new IValue[] { ff.integer(1), ff.dubble(1.0),
				ff.sourceLocation("bla", ff.sourceRange(0, 0, 0, 0, 0, 0)) };
		IValue[] b = new IValue[] { ff.integer(1), ff.dubble(1.0),
				ff.sourceLocation("bla", ff.sourceRange(0, 0, 0, 0, 0, 0)) };
		Type t = ft.tupleType(a);

		if (t != ft.tupleType(b)) {
			fail("tuples should be canonical");
		}

		testTupleTypeOf(t, 3);
	}

	public void testSetTypeOf() {
		Type type = ft.setType(ft.integerType());

		if (type != ft.setType(ft.integerType())) {
			fail("set should be canonical");
		}
	}

	public void testRelTypeType() {
		try {
			Type namedType = ft.namedType("myTuple", ft.tupleType(ft.integerType(), ft.integerType()));
			// note that the declared type of namedType needs to be Type
			Type type = ft.relTypeFromTuple(namedType);
		
			Type namedType2 = ft.namedType("myTuple", ft.tupleType(ft.integerType(), ft.integerType()));
			
			if (type != ft.relTypeFromTuple(namedType2)) {
				fail("relation types should be canonical");
			}
			
			if (type.getFieldType(0) != ft.integerType() &&
					type.getFieldType(1) != ft.integerType()) {
				fail("relation should mimick tuple field types");
			}
		} catch (FactTypeError e) {
			fail("type error for correct relation");
		}
	}

	public void testRelTypeNamedType() {
		try {
			Type namedType = ft.namedType("myTuple", ft.tupleType(ft.integerType(), ft.integerType()));
			// note that the declared type of namedType needs to be NamedType
			Type type = ft.relTypeFromTuple(namedType);
		
			Type namedType2 = ft.namedType("myTuple", ft.tupleType(ft.integerType(), ft.integerType()));
			
			if (type != ft.relTypeFromTuple(namedType2)) {
				fail("relation types should be canonical");
			}
		} catch (FactTypeError e) {
			fail("type error for correct relation");
		}
	}

	public void testRelTypeTupleType() {
			Type tupleType = ft
				.tupleType(ft.integerType(), ft.integerType());
		// note that the declared type of tupleType needs to be TupleType
		Type type = ft.relTypeFromTuple(tupleType);

		Type tupleType2 = ft.tupleType(ft.integerType(), ft
				.integerType());

		if (type != ft.relTypeFromTuple(tupleType2)) {
			fail("relation types should be canonical");
		}
	}

	public void testRelTypeOfType() {
		Type type = ft.relType(types[0]);

		if (type != ft.relType(types[0])) {
			fail("relation types should be canonical");
		}

		testRelationTypeOf(type, 1);
	}

	public void testRelTypeOfTypeType() {
		Type type = ft.relType(types[0], types[1]);

		if (type != ft.relType(types[0], types[1])) {
			fail("relation types should be canonical");
		}

		testRelationTypeOf(type, 2);
	}

	public void testRelTypeOfTypeTypeType() {
		Type type = ft.relType(types[0], types[1], types[2]);

		if (type != ft.relType(types[0], types[1], types[2])) {
			fail("relation types should be canonical");
		}

		testRelationTypeOf(type, 3);
	}

	public void testRelTypeOfTypeTypeTypeType() {
		Type type = ft.relType(types[0], types[1], types[2], types[3]);

		if (type != ft.relType(types[0], types[1], types[2], types[3])) {
			fail("relation types should be canonical");
		}
		testRelationTypeOf(type, 4);
	}

	public void testRelTypeOfTypeTypeTypeTypeType() {
		Type type = ft.relType(types[0], types[1], types[2], types[3], types[4]);

		if (type != ft.relType(types[0], types[1], types[2], types[3], types[4])) {
			fail("relation types should be canonical");
		}
		testRelationTypeOf(type, 5);
	}

	public void testRelTypeOfTypeTypeTypeTypeTypeType() {
		Type type = ft.relType(types[0], types[1], types[2], types[3], types[4], types[5]);

		if (type != ft.relType(types[0], types[1], types[2], types[3], types[4], types[5])) {
			fail("relation types should be canonical");
		}
		testRelationTypeOf(type, 6);
	}

	public void testRelTypeOfTypeTypeTypeTypeTypeTypeType() {
		Type type = ft.relType(types[0], types[1], types[2], types[3], types[4], types[5], types[6]);

		if (type != ft.relType(types[0], types[1], types[2], types[3], types[4], types[5], types[6])) {
			fail("relation types should be canonical");
		}
		testRelationTypeOf(type, 7);
	}

	public void testNamedType() {
		try {
			Type t1 = ft.namedType("myType", ft.integerType());
			Type t2 = ft.namedType("myType", ft.integerType());

			if (t1 != t2) {
				fail("named types should be canonical");
			}

			try {
				ft.namedType("myType", ft.doubleType());
				fail("Should not be allowed to redeclare a type name");
			} catch (TypeDeclarationException e) {
				// this should happen
			}
		} catch (TypeDeclarationException e) {
			fail("the above should be type correct");
		}
	}

	public void testListType() {
		Type t1 = ft.listType(ft.integerType());
		Type t2 = ft.listType(ft.integerType());
		
		if (t1 != t2) {
			fail("named types should be canonical");
		}
	}
}
