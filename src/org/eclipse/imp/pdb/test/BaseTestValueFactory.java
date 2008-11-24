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

import org.eclipse.imp.pdb.facts.IList;
import org.eclipse.imp.pdb.facts.IRelation;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.ISourceRange;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.FactTypeError;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public abstract class BaseTestValueFactory extends TestCase {
    private IValueFactory ff;
    private TypeFactory ft = TypeFactory.getInstance();
    private IValue[] integers;
	
	protected void setUp(IValueFactory factory) throws Exception {
		ff = factory;
		
		integers = new IValue[100];
		for (int i = 0; i < integers.length; i++) {
			integers[i] = ff.integer(i);
		}
	}

	public void testRelationNamedType() {
		try {
			Type type = ft.namedType("myType2", ft.relType(ft.integerType(), ft.integerType()));
			IRelation r = (IRelation) type.make(ff);
			
			if (!r.getType().getBaseType().isRelationType()) {
				fail("relation does not have a relation type");
			}
		} catch (FactTypeError e) {
			fail("type error on the construction of a valid relation: " + e);
		}
	}

	public void testRelationTupleType() {
		IRelation r = ff.relation(ft.tupleType(ft.integerType()));

		if (r.size() != 0) {
			fail("empty set is not empty");
		}

		if (r.getType() != ft.relType(ft.tupleType(ft.integerType()))) {
			fail("should be a rel of unary int tuples");
		}
	}

	public void testRelationWith() {
		IRelation[] relations = new IRelation[7];
		ITuple[] tuples = new ITuple[7];
		
		for (int i = 0; i < 7; i++) {
			tuples[i] = ff.tuple(ff.integer(i), ff.dubble(i));
		}

		try {
			relations[0] = ff.relation(tuples[0]);
			relations[1] = ff.relation(tuples[0], tuples[1]);
			relations[2] = ff.relation(tuples[0], tuples[1], tuples[2]);
			relations[3] = ff.relation(tuples[0], tuples[1], tuples[2],
					tuples[3]);
			relations[4] = ff.relation(tuples[0], tuples[1], tuples[2],
					tuples[3], tuples[4]);
			relations[5] = ff.relation(tuples[0], tuples[1], tuples[2],
					tuples[3], tuples[4], tuples[5]);
			relations[6] = ff.relation(tuples[0], tuples[1], tuples[2],
					tuples[3], tuples[4], tuples[5], tuples[6]);

			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < i; j++) {
					if (!relations[i].contains(tuples[j])) {
						fail("tuple creation is weird");
					}
				}
			}
		} catch (FactTypeError e) {
			System.err.println(e);
			fail("this should all be type correct");
		}
	}

	public void testSetNamedType() {
		ISet l;
		try {
			l = (ISet) ft.namedType("mySet", ft.setType(ft.integerType())).make(ff);

			if (l.getType().isSubtypeOf(ft.namedType("mySet", ft.setType(ft.integerType())))) {
				fail("should be a set of integers");
			}

			if (l.getElementType() != ft.integerType()) {
				fail("elements should be integers");
			}

			if (l.size() != 0) {
				fail("empty list not empty");
			}
		} catch (FactTypeError e1) {
			fail("this was a correct type");
		}
		
		try {
			ft.namedType("notASet", ft.integerType()).make(ff);
			fail("should not be possible to make a set that is not a set");
		}
		catch (FactTypeError e) {
			// should happen
		}
	}

	public void testSetType() {
        ISet s = ff.set(ft.doubleType());
		
		if (s.size() != 0) {
			fail("empty set is not empty");
		}
		
		if (s.getType() != ft.setType(ft.doubleType())) {
			fail("should be a list of doubles");
		}

		if (s.getElementType() != ft.doubleType()) {
			fail("should be a list of doubles");
		}
	}

	public void testSetWith() {
        ISet[] sets = new ISet[7];
		
		sets[0] = ff.set(integers[0]);
		sets[1] = ff.set(integers[0],integers[1]);
		sets[2] = ff.set(integers[0],integers[1],integers[2]);
		sets[3] = ff.set(integers[0],integers[1],integers[2],integers[3]);
		sets[4] = ff.set(integers[0],integers[1],integers[2],integers[3],integers[4]);
		sets[5] = ff.set(integers[0],integers[1],integers[2],integers[3],integers[4],integers[5]);
		sets[6] = ff.set(integers[0],integers[1],integers[2],integers[3],integers[4],integers[5],integers[6]);

		try {
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j <= i; j++) {
					if (!sets[i].contains(integers[j])) {
						fail("set creation is weird");
					}
				}
				for (int j = 8; j < 100; j++) {
					if (sets[i].contains(integers[j])) {
						fail("set creation contains weird values");
					}
				}
			}
		} catch (FactTypeError e) {
			System.err.println(e);
			fail("this should all be type correct");
		}
	}

	public void testListNamedType() {
		IList l;
		try {
			l = (IList) ft.namedType("myList", ft.listType(ft.integerType())).make(ff);

			if (l.getType().isSubtypeOf(ft.namedType("myList", ft.listType(ft
					.integerType())))) {
				fail("should be a list of integers");
			}

			if (l.getElementType() != ft.integerType()) {
				fail("elements should be integers");
			}

			if (l.length() != 0) {
				fail("empty list not empty");
			}
		} catch (FactTypeError e1) {
			fail("this was a correct type");
		}
		
		try {
			ft.namedType("notAList", ft.integerType()).make(ff);
			fail("should not be possible to make a list that is not a list");
		}
		catch (FactTypeError e) {
			// should happen
		}
	}

	public void testListType() {
		IList l = ff.list(ft.doubleType());
		
		if (l.length() != 0) {
			fail("empty list is not empty");
		}

		if (l.getElementType() != ft.doubleType()) {
			fail("should be a list of doubles");
		}
	}

	public void testListWith() {
		IList[] lists = new IList[7];
		
		lists[0] = ff.list(integers[0]);
		lists[1] = ff.list(integers[0],integers[1]);
		lists[2] = ff.list(integers[0],integers[1],integers[2]);
		lists[3] = ff.list(integers[0],integers[1],integers[2],integers[3]);
		lists[4] = ff.list(integers[0],integers[1],integers[2],integers[3],integers[4]);
		lists[5] = ff.list(integers[0],integers[1],integers[2],integers[3],integers[4],integers[5]);
		lists[6] = ff.list(integers[0],integers[1],integers[2],integers[3],integers[4],integers[5],integers[6]);

		for (int i = 0; i < 7; i++) {
			for (int j = 0; j <= i; j++) {
				if (lists[i].get(j) != integers[j]) {
					fail("list creation is weird");
				}
			}
		}
		
	}

	public void testTupleIValue() {
		ITuple[] tuples = new ITuple[7];
		
		tuples[0] = ff.tuple(integers[0]);
		tuples[1] = ff.tuple(integers[0],integers[1]);
		tuples[2] = ff.tuple(integers[0],integers[1],integers[2]);
		tuples[3] = ff.tuple(integers[0],integers[1],integers[2],integers[3]);
		tuples[4] = ff.tuple(integers[0],integers[1],integers[2],integers[3],integers[4]);
		tuples[5] = ff.tuple(integers[0],integers[1],integers[2],integers[3],integers[4],integers[5]);
		tuples[6] = ff.tuple(integers[0],integers[1],integers[2],integers[3],integers[4],integers[5],integers[6]);

		for (int i = 0; i < 7; i++) {
			for (int j = 0; j <= i; j++) {
				if (tuples[i].get(j) != integers[j]) {
					fail("tuple creation is weird");
				}
			}
		}
	}

	public void testInteger() {
		if (ff.integer(42).getValue() != 42) {
			fail("integer creation is weird");
		}
	}

	public void testDubble() {
		if (ff.dubble(84.5).getValue() != 84.5) {
			fail("double creation is weird");
		}
	}

	public void testString() {
		if (!ff.string("hello").getValue().equals("hello")) {
			fail("string creation is weird");
		}
	}

	public void testSourceLocation() {
		ISourceRange range = ff.sourceRange(1, 2, 3, 4, 5, 6);
		ISourceLocation sl = ff.sourceLocation("/dev/null", range);
		if (!sl.getPath().equals("/dev/null") ||
				!sl.getRange().equals(range)) {
			fail("source location creation is weird");
		}
	}

	public void testSourceRange() {
		ISourceRange range = ff.sourceRange(1, 2, 3, 4, 5, 6);
		if (range.getStartOffset() != 1 || range.getLength() != 2
				|| range.getStartColumn() != 5 || range.getStartLine() != 3
				|| range.getEndLine() != 4 || range.getEndColumn() != 6) {
			fail("source range creation is weird");
		}
	}
}
