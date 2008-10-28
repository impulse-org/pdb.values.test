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
import org.eclipse.imp.pdb.facts.type.ListType;
import org.eclipse.imp.pdb.facts.type.NamedType;
import org.eclipse.imp.pdb.facts.type.RelationType;
import org.eclipse.imp.pdb.facts.type.SetType;
import org.eclipse.imp.pdb.facts.type.TupleType;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeDeclarationException;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public class TestTypeFactory extends TestCase {
	private TypeFactory ft = TypeFactory.getInstance();

	private ValueFactory ff = ValueFactory.getInstance();

	private Type[] types = new Type[] { ft.integerType(), ft.doubleType(),
			ft.sourceLocationType(), ft.sourceRangeType(), ft.valueType(),
			ft.listType(ft.integerType()), ft.setTypeOf(ft.doubleType()) };

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

	public void testObjectType() {
		if (ft.objectType(Integer.class) != ft.objectType(Integer.class)) {
			fail("objectType should be canonical");
		}
		
		if (ft.objectType(TestTypeFactory.class) != ft.objectType(TestTypeFactory.class)) {
			fail("objectType should be canonical");
		}
		
		NamedType type = ft.namedType("myObjectType", ft.objectType(Double.class));
        
        try {
          ff.object(type, new Integer(1));
          fail("should not be able to store an integer object in a double value");
        }
        catch (FactTypeError e) {
        	// this should happen
        }
        
        try {
        	ff.object(type, new Object());
        	fail("should not be able to store an object object in a double value");
        } catch (FactTypeError e) {
        	// this should happen
        }
        
        NamedType type2 = ft.namedType("myObjectType2", ft
        		.objectType(Object.class));
        try {
        	ff.object(type2, new Integer(1));
        	fail("Java subtyping does not carry over to our type system");
        } catch (FactTypeError e) {
        	// this should happen
        }
		
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
		TupleType t = ft.tupleTypeOf(types[0]);

		if (t != ft.tupleTypeOf(types[0])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 1);
	}

	public void testTupleTypeOfTypeType() {
		TupleType t = ft.tupleTypeOf(types[0], types[1]);

		if (t != ft.tupleTypeOf(types[0], types[1])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 2);
	}

	public void testTupleTypeOfTypeTypeType() {
		TupleType t = ft.tupleTypeOf(types[0], types[1], types[2]);

		if (t != ft.tupleTypeOf(types[0], types[1], types[2])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 3);
	}

	public void testTupleTypeOfTypeTypeTypeType() {
		TupleType t = ft.tupleTypeOf(types[0], types[1], types[2], types[3]);

		if (t != ft.tupleTypeOf(types[0], types[1], types[2], types[3])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 4);
	}

	public void testTupleTypeOfTypeTypeTypeTypeType() {
		TupleType t = ft.tupleTypeOf(types[0], types[1], types[2], types[3],
				types[4]);

		if (t != ft.tupleTypeOf(types[0], types[1], types[2], types[3],
				types[4])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 5);
	}

	public void testTupleTypeOfTypeTypeTypeTypeTypeType() {
		TupleType t = ft.tupleTypeOf(types[0], types[1], types[2], types[3],
				types[4], types[5]);

		if (t != ft.tupleTypeOf(types[0], types[1], types[2], types[3],
				types[4], types[5])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 6);
	}

	public void testTupleTypeOfTypeTypeTypeTypeTypeTypeType() {
		TupleType t = ft.tupleTypeOf(types[0], types[1], types[2], types[3],
				types[4], types[5], types[6]);

		if (t != ft.tupleTypeOf(types[0], types[1], types[2], types[3],
				types[4], types[5], types[6])) {
			fail("tuple types should be canonical");
		}

		testTupleTypeOf(t, 7);
	}

	private void testTupleTypeOf(TupleType t, int width) {

		if (t.getArity() != width) {
			fail("tuple arity broken");
		}

		for (int i = 0; i < t.getArity(); i++) {
			if (t.getFieldType(i) != types[i % types.length]) {
				fail("Tuple field type unexpected");
			}
		}
	}

	private void testRelationTypeOf(RelationType t, int width) {

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
		TupleType t = ft.tupleTypeOf(a);

		if (t != ft.tupleTypeOf(b)) {
			fail("tuples should be canonical");
		}

		testTupleTypeOf(t, 3);
	}

	public void testSetTypeOf() {
		SetType type = ft.setTypeOf(ft.integerType());

		if (type != ft.setTypeOf(ft.integerType())) {
			fail("set should be canonical");
		}
	}

	public void testRelTypeType() {
		try {
			ft.relType(ft.namedType("myInt", ft.integerType()));
			fail("just created a bogus relation type");
		} catch (FactTypeError e) {
			// test succeeded
		}

		try {
			ft.relType(ft.integerType());
			fail("just created a bogus relation type");
		} catch (FactTypeError e) {
			// test succeeded
		}
		
		try {
			Type namedType = ft.namedType("myTuple", ft.tupleTypeOf(ft.integerType(), ft.integerType()));
			// note that the declared type of namedType needs to be Type
			RelationType type = ft.relType(namedType);
		
			Type namedType2 = ft.namedType("myTuple", ft.tupleTypeOf(ft.integerType(), ft.integerType()));
			
			if (type != ft.relType(namedType2)) {
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
			NamedType namedType = ft.namedType("myTuple", ft.tupleTypeOf(ft.integerType(), ft.integerType()));
			// note that the declared type of namedType needs to be NamedType
			RelationType type = ft.relType(namedType);
		
			NamedType namedType2 = ft.namedType("myTuple", ft.tupleTypeOf(ft.integerType(), ft.integerType()));
			
			if (type != ft.relType(namedType2)) {
				fail("relation types should be canonical");
			}
		} catch (FactTypeError e) {
			fail("type error for correct relation");
		}
	}

	public void testRelTypeTupleType() {
			TupleType tupleType = ft
				.tupleTypeOf(ft.integerType(), ft.integerType());
		// note that the declared type of tupleType needs to be TupleType
		RelationType type = ft.relType(tupleType);

		TupleType tupleType2 = ft.tupleTypeOf(ft.integerType(), ft
				.integerType());

		if (type != ft.relType(tupleType2)) {
			fail("relation types should be canonical");
		}
	}

	public void testRelTypeOfType() {
		RelationType type = ft.relTypeOf(types[0]);

		if (type != ft.relTypeOf(types[0])) {
			fail("relation types should be canonical");
		}

		testRelationTypeOf(type, 1);
	}

	public void testRelTypeOfTypeType() {
		RelationType type = ft.relTypeOf(types[0], types[1]);

		if (type != ft.relTypeOf(types[0], types[1])) {
			fail("relation types should be canonical");
		}

		testRelationTypeOf(type, 2);
	}

	public void testRelTypeOfTypeTypeType() {
		RelationType type = ft.relTypeOf(types[0], types[1], types[2]);

		if (type != ft.relTypeOf(types[0], types[1], types[2])) {
			fail("relation types should be canonical");
		}

		testRelationTypeOf(type, 3);
	}

	public void testRelTypeOfTypeTypeTypeType() {
		RelationType type = ft.relTypeOf(types[0], types[1], types[2], types[3]);

		if (type != ft.relTypeOf(types[0], types[1], types[2], types[3])) {
			fail("relation types should be canonical");
		}
		testRelationTypeOf(type, 4);
	}

	public void testRelTypeOfTypeTypeTypeTypeType() {
		RelationType type = ft.relTypeOf(types[0], types[1], types[2], types[3], types[4]);

		if (type != ft.relTypeOf(types[0], types[1], types[2], types[3], types[4])) {
			fail("relation types should be canonical");
		}
		testRelationTypeOf(type, 5);
	}

	public void testRelTypeOfTypeTypeTypeTypeTypeType() {
		RelationType type = ft.relTypeOf(types[0], types[1], types[2], types[3], types[4], types[5]);

		if (type != ft.relTypeOf(types[0], types[1], types[2], types[3], types[4], types[5])) {
			fail("relation types should be canonical");
		}
		testRelationTypeOf(type, 6);
	}

	public void testRelTypeOfTypeTypeTypeTypeTypeTypeType() {
		RelationType type = ft.relTypeOf(types[0], types[1], types[2], types[3], types[4], types[5], types[6]);

		if (type != ft.relTypeOf(types[0], types[1], types[2], types[3], types[4], types[5], types[6])) {
			fail("relation types should be canonical");
		}
		testRelationTypeOf(type, 7);
	}

	public void testNamedType() {
		try {
			NamedType t1 = ft.namedType("myType", ft.integerType());
			NamedType t2 = ft.namedType("myType", ft.integerType());

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
		ListType t1 = ft.listType(ft.integerType());
		ListType t2 = ft.listType(ft.integerType());
		
		if (t1 != t2) {
			fail("named types should be canonical");
		}
	}
}
