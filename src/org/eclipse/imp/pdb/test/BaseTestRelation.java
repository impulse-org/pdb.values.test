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

import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.imp.pdb.facts.IRelation;
import org.eclipse.imp.pdb.facts.IRelationWriter;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.ISetWriter;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.FactTypeError;
import org.eclipse.imp.pdb.facts.type.RelationType;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public abstract class BaseTestRelation extends TestCase {
    private IValueFactory vf;
	private TypeFactory tf;
	private IValue[] integers;
	private ITuple[] integerTuples;
	private ISet setOfIntegers;
	private IRelation integerRelation;
	private IValue[] doubles;
	private ISet setOfDoubles;
	private IRelation doubleRelation;
	private ITuple[] doubleTuples;
    
	protected void setUp(IValueFactory factory) throws Exception {
		super.setUp();
		vf = factory;
		tf = TypeFactory.getInstance();
		
		integers = new IValue[5];
		setOfIntegers = vf.set(tf.integerType());
		ISetWriter sw = setOfIntegers.getWriter();
		
		for (int i = 0; i < integers.length; i++) {
			IValue iv = vf.integer(i);
			integers[i] = iv;
			sw.insert(iv);
		}
		sw.done();
		
		doubles = new IValue[10];
		setOfDoubles = vf.set(tf.doubleType());
		ISetWriter sw2 = setOfDoubles.getWriter();
		
		for (int i = 0; i < doubles.length; i++) {
			IValue iv = vf.dubble(i);
			doubles[i] = iv;
			sw2.insert(iv);
		}
		sw2.done();
		
		integerRelation = vf.relation(tf.tupleTypeOf(tf.integerType(), tf.integerType()));
		IRelationWriter rw = integerRelation.getWriter();
		integerTuples = new ITuple[integers.length * integers.length];
		
		for (int i = 0; i < integers.length; i++) {
			for (int j = 0; j < integers.length; j++) {
				ITuple t = vf.tuple(integers[i], integers[j]);
				integerTuples[i * integers.length + j] = t;
				rw.insert(t);
			}
		}
		rw.done();
		
		doubleRelation = vf.relation(tf.tupleTypeOf(tf.doubleType(), tf.doubleType()));
		IRelationWriter rw2 = doubleRelation.getWriter();
		doubleTuples = new ITuple[doubles.length * doubles.length];
		
		for (int i = 0; i < doubles.length; i++) {
			for (int j = 0; j < doubles.length; j++) {
				ITuple t = vf.tuple(doubles[i], doubles[j]);
				doubleTuples[i * doubles.length + j] = t;
				rw2.insert(t);
			}
		}
		rw2.done();
	}

	public void testIsEmpty() {
		if (integerRelation.isEmpty()) {
			fail("integerRelation is not empty");
		}
		
		if (!vf.relation(tf.tupleTypeOf(tf.integerType())).isEmpty()) {
			fail("this relation should be empty");
		}
	}

	public void testSize() {
		if (integerRelation.size() != integerTuples.length) {
			fail("relation size is not correct");
		}
	}

	public void testArity() {
		if (integerRelation.arity() != 2) {
			fail("arity should be 2");
		}
	}

	public void testProductIRelation() {
		IRelation prod = integerRelation.product(integerRelation);
		
		if (prod.arity() != 2 * integerRelation.arity()) {
			fail("arity of product should be 4");
		}
		
		if (prod.size() != integerRelation.size() * integerRelation.size()) {
			fail("size of product should be square of size of integerRelation");
		}
		
		try {
			prod.getWriter();
			fail("prod should return an immutable value");
		}
		catch (IllegalStateException e) {
			// this should happen
		}
	}

	public void testProductISet() {
		IRelation prod = integerRelation.product(setOfIntegers);
		
		if (prod.arity() != 3) {
			fail("arity of product should be 3");
		}
		
		if (prod.size() != integerRelation.size() * setOfIntegers.size()) {
			fail("size of product should be square of size of integerRelation");
		}
		
		try {
			prod.getWriter();
			fail("prod should return an immutable value");
		}
		catch (IllegalStateException e) {
			// this should happen
		}
	}

	public void testClosure() {
		try {
			if (!integerRelation.closure().equals(integerRelation)) {
				fail("closure adds extra tuples?");
			}
		} catch (FactTypeError e) {
			fail("integerRelation is reflexive, so why an error?");
		}
		
		try {
			IRelation rel = vf.relation(tf.tupleTypeOf(tf.integerType(), tf.integerType()));
			rel.closure();
		}
		catch (FactTypeError e) {
			fail("reflexivity with subtyping is allowed");
		}
		
		try {
			IRelation rel = vf.relation(tf.tupleTypeOf(tf.integerType(), tf.doubleType()));
			rel.closure();
			fail("relation is not reflexive but no type error thrown");
		}
		catch (FactTypeError e) {
			// this should happen
		}
		
		
		
		try {
			ITuple t1 = vf.tuple(integers[0], integers[1]);
			ITuple t2 = vf.tuple(integers[1], integers[2]);
			ITuple t3 = vf.tuple(integers[2], integers[3]);
			ITuple t4 = vf.tuple(integers[0], integers[2]);
			ITuple t5 = vf.tuple(integers[1], integers[3]);
			ITuple t6 = vf.tuple(integers[0], integers[3]);
			
			IRelation test = vf.relationWith(t1, t2, t3);
			IRelation closed = test.closure();
			
			try {
				closed.getWriter();
				fail("closure should return an immutable relation");
			}
			catch (IllegalStateException e) {
				// this should happen
			}
			
			if (closed.arity() != test.arity()) {
				fail("closure should produce relations of same arity");
			}
			
			if (closed.size() != 6) {
				fail("closure contains too few elements");
			}
			
			if (!closed.intersect(test).equals(test)) {
				fail("closure should contain all original elements");
			}
			
			if (!closed.contains(t4) || !closed.contains(t5) || !closed.contains(t6)) {
				fail("closure does not contain required elements");
			}
		
		} catch (FactTypeError e) {
			fail("this should all be type correct");
		}
	}

	public void testCompose() {
		try {
			IRelation comp = integerRelation.compose(integerRelation);
			
			if (comp.arity() != integerRelation.arity() * 2 - 2) {
				fail("composition is a product with the last column of the first relation and the first column of the last relation removed");
			}
			
			if (comp.size() != integerRelation.size()) {
				fail("numner of expected tuples is off");
			}
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
		
		try {
			ITuple t1 = vf.tuple(integers[0], doubles[0]);
			ITuple t2 = vf.tuple(integers[1], doubles[1]);
			ITuple t3 = vf.tuple(integers[2], doubles[2]);
			IRelation rel1 = vf.relationWith(t1, t2, t3);

			ITuple t4 = vf.tuple(doubles[0], integers[0]);
			ITuple t5 = vf.tuple(doubles[1], integers[1]);
			ITuple t6 = vf.tuple(doubles[2], integers[2]);
			IRelation rel2 = vf.relationWith(t4, t5, t6);
			
			ITuple t7 = vf.tuple(integers[0], integers[0]);
			ITuple t8 = vf.tuple(integers[1], integers[1]);
			ITuple t9 = vf.tuple(integers[2], integers[2]);
			IRelation rel3 = vf.relationWith(t7, t8, t9);
			
			try {
			  vf.relationWith(vf.tuple(doubles[0],doubles[0])).compose(rel1);
			  fail("relations should not be composable");
			}
			catch (FactTypeError e) {
				// this should happen
			}
			
			IRelation comp = rel1.compose(rel2);
			
			if (!comp.equals(rel3)) {
				fail("composition does not produce expected result");
			}
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
	}

	public void testContains() {
		try {
			for (ITuple t : integerTuples) {
				if (!integerRelation.contains(t)) {
					fail("contains returns false instead of true");
				}
			}
		} catch (FactTypeError e) {
			fail("this should be type correct");
		}
		
		try {
			integerRelation.contains(vf.tuple(doubles[0], doubles[0]));
			fail("should not be able to check for containment of doubles in integer rel");
		} catch (FactTypeError e) {
			// this should happen
		}
	}

	public void testInsert() {
		try {
			IRelation rel = integerRelation.insert(vf.tuple(integers[0], integers[0]));
			
			if (!rel.equals(integerRelation)) {
				fail("insert into a relation of an existing tuple should not change the relation");
			}
			
			try {
				rel.getWriter();
				fail("insert should return an immutable relation");
			}
			catch (IllegalStateException e) {
				// this should happen
			}
			
			IRelation rel2 = vf.relationWith(vf.tuple(integers[0], integers[0]));
			rel2.getWriter().insertAll(integerRelation);
			rel2.getWriter().done();
			final ITuple tuple = vf.tuple(vf.integer(100), vf.integer(100));
			IRelation rel3 = rel2.insert(tuple);
			
			try {
				rel3.getWriter();
				fail("insert should return an immutable relation");
			}
			catch (IllegalStateException e) {
				// this should happen
			}
			
			if (rel3.size() != integerRelation.size() + 1) {
				fail("insert failed");
			}
			
			if (!rel3.contains(tuple)) {
				fail("insert failed");
			}
			
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
	}

	public void testIntersectIRelation() {
		IRelation empty1 = vf.relation(tf.tupleTypeOf(tf.integerType()));
		IRelation empty2 = vf.relation(tf.tupleTypeOf(tf.doubleType()));
		
		try {
			final IRelation intersection = empty1.intersect(empty2);
			if (!intersection.isEmpty()) {
				fail("empty intersection failed");
			}
			
			RelationType type = (RelationType) intersection.getType();
			if (!type.getFieldType(0).isNumberType()) {
				fail("intersection should produce lub types");
			}
		} catch (FactTypeError e) {
		    fail("intersecting types which have a lub should be possible");
		}
		
		try {
			if (!integerRelation.intersect(doubleRelation).isEmpty()) {
				fail("non-intersecting relations should produce empty intersections");
			}

			IRelation oneTwoThree = vf.relationWith(integerTuples[0],
					integerTuples[1], integerTuples[2]);
			IRelation threeFourFive = vf.relationWith(integerTuples[2],
					integerTuples[3], integerTuples[4]);
			IRelation result = vf.relationWith(integerTuples[2]);

			if (!oneTwoThree.intersect(threeFourFive).equals(result)) {
				fail("intersection failed");
			}
			if (!threeFourFive.intersect(oneTwoThree).equals(result)) {
				fail("intersection should be commutative");
			}
			
			if (!oneTwoThree.intersect(vf.relation(tf.tupleTypeOf(tf.integerType(),tf.integerType()))).isEmpty()) {
				fail("intersection with empty set should produce empty");
			}

		} catch (FactTypeError e) {
			fail("the above should all be type safe");
		} 
	}

	public void testIntersectISet() {
		IRelation empty1 = vf.relation(tf.tupleTypeOf(tf.integerType()));
		ISet empty2 = vf.set(tf.tupleTypeOf(tf.doubleType()));
		
		try {
			final IRelation intersection = empty1.intersect(empty2);
			if (!intersection.isEmpty()) {
				fail("empty intersection failed");
			}
			
			RelationType type = (RelationType) intersection.getType();
			if (!type.getFieldType(0).isNumberType()) {
				fail("intersection should produce lub types");
			}
		} catch (FactTypeError e) {
		    fail("intersecting types which have a lub should be possible");
		}
		
		try {
			if (!integerRelation.intersect(doubleRelation).isEmpty()) {
				fail("non-intersecting relations should produce empty intersections");
			}

			IRelation oneTwoThree = vf.relationWith(integerTuples[0],
					integerTuples[1], integerTuples[2]);
			ISet threeFourFive = vf.setWith(integerTuples[2],
					integerTuples[3], integerTuples[4]);
			IRelation result = vf.relationWith(integerTuples[2]);

			if (!oneTwoThree.intersect(threeFourFive).equals(result)) {
				fail("intersection failed");
			}
			if (!threeFourFive.intersect(oneTwoThree).equals(result)) {
				fail("intersection should be commutative");
			}
			
			if (!oneTwoThree.intersect(vf.relation(tf.tupleTypeOf(tf.integerType(),tf.integerType()))).isEmpty()) {
				fail("intersection with empty set should produce empty");
			}

		} catch (FactTypeError e) {
			fail("the above should all be type safe");
		} 
	}

	public void testInvertIRelation() {
		IRelation test = vf.relation(tf.tupleTypeOf(tf.integerType(), tf
				.integerType()));
	
		final int amount = integers.length / 2;
		try {
			IRelationWriter rw = test.getWriter();

			for (int i = 0; i < amount; i++) {
				for (int j = 0; j < amount; j++) {
					ITuple t = vf.tuple(integers[i], integers[j]);
					rw.insert(t);
				}
			}
			rw.done();
		} catch (FactTypeError e) {
			fail("creation of test data should be type correct");
		}
		
		try {
			IRelation inverted = test.invert(integerRelation);
			
			if (inverted.size() != integerRelation.size() - (amount * amount)) {
				fail("inversion failed");
			}
			
			for (ITuple t : test) {
				if (inverted.contains(t)) {
					fail("inversion failed");
				}
			}
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
		
		
		try {
			int notInThere = integers.length;
			IRelation test2 = test.insert(vf.tuple(vf.integer(notInThere), vf.integer(notInThere)));
			
			try {
				test2.invert(integerRelation);
				fail("integerRelation is not a superset of test2, and should not be used as a universe");
			} catch (FactTypeError e) {
				// this should happen
			}
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
	}

	public void testInvertISet() {
		ISet test = vf.set(tf.tupleTypeOf(tf.integerType(), tf
				.integerType()));
	
		final int amount = integers.length / 2;
		try {
			ISetWriter rw = test.getWriter();

			for (int i = 0; i < amount; i++) {
				for (int j = 0; j < amount; j++) {
					ITuple t = vf.tuple(integers[i], integers[j]);
					rw.insert(t);
				}
			}
			rw.done();
		} catch (FactTypeError e) {
			fail("creation of test data should be type correct");
		}
		
		try {
			IRelation inverted = test.invert(integerRelation);
			
			if (inverted.size() != integerRelation.size() - (amount * amount)) {
				fail("inversion failed");
			}
			
			for (IValue t : test) {
				if (inverted.contains((ITuple) t)) {
					fail("inversion failed");
				}
			}
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
		
		try {
			int notInThere = integers.length;
			ISet test2 = test.insert(vf.tuple(vf.integer(notInThere), vf.integer(notInThere)));
			
			try {
				test2.invert(integerRelation);
				fail("integerRelation is not a superset of test2, and should not be used as a universe");
			} catch (FactTypeError e) {
				// this should happen
			}
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
	}

	public void testSubtractIRelation() {
		IRelation empty1 = vf.relation(tf.tupleTypeOf(tf.integerType()));
		IRelation empty2 = vf.relation(tf.tupleTypeOf(tf.doubleType()));
		
		try {
			final IRelation diff = empty1.subtract(empty2);
			if (!diff.isEmpty()) {
				fail("empty diff failed");
			}
			
			RelationType type = (RelationType) diff.getType();
			if (!type.getFieldType(0).isNumberType()) {
				fail("diff should produce lub types");
			}
		} catch (FactTypeError e) {
		    fail("subtracting types which have a lub should be possible");
		}
		
		try {
			if (!integerRelation.subtract(doubleRelation).equals(integerRelation)) {
				fail("subtracting non-intersection relations should have no effect");
			}

			IRelation oneTwoThree = vf.relationWith(integerTuples[0],
					integerTuples[1], integerTuples[2]);
			IRelation threeFourFive = vf.relationWith(integerTuples[2],
					integerTuples[3], integerTuples[4]);
			IRelation result1 = vf.relationWith(integerTuples[0],integerTuples[1]);
			IRelation result2 = vf.relationWith(integerTuples[3],integerTuples[4]);

			if (!oneTwoThree.subtract(threeFourFive).equals(result1)) {
				fail("subtraction failed");
			}
			if (!threeFourFive.subtract(oneTwoThree).equals(result2)) {
				fail("subtraction failed");
			}
			
			IRelation empty3 = vf.relation(tf.tupleTypeOf(tf.integerType(),tf.integerType()));
			if (!empty3.subtract(threeFourFive).isEmpty()) {
				fail("subtracting from empty set should produce empty");
			}

		} catch (FactTypeError e) {
			fail("the above should all be type safe");
		} 
	}

	public void testSubtractISet() {
		IRelation empty1 = vf.relation(tf.tupleTypeOf(tf.integerType()));
		ISet empty2 = vf.set(tf.tupleTypeOf(tf.doubleType()));
		
		try {
			final IRelation diff = empty1.subtract(empty2);
			if (!diff.isEmpty()) {
				fail("empty diff failed");
			}
			
			RelationType type = (RelationType) diff.getType();
			if (!type.getFieldType(0).isNumberType()) {
				fail("diff should produce lub types");
			}
		} catch (FactTypeError e) {
		    fail("subtracting types which have a lub should be possible");
		}
		
		try {
			IRelation oneTwoThree = vf.relationWith(integerTuples[0],
					integerTuples[1], integerTuples[2]);
			ISet threeFourFive = vf.setWith(integerTuples[2],
					integerTuples[3], integerTuples[4]);
			IRelation result1 = vf.relationWith(integerTuples[0],integerTuples[1]);

			if (!oneTwoThree.subtract(threeFourFive).equals(result1)) {
				fail("subtraction failed");
			}
			
			IRelation empty3 = vf.relation(tf.tupleTypeOf(tf.integerType(),tf.integerType()));
			if (!empty3.subtract(threeFourFive).isEmpty()) {
				fail("subtracting from empty set should produce empty");
			}

		} catch (FactTypeError e) {
			fail("the above should all be type safe");
		}
	}

	public void testUnionIRelation() {
		IRelation empty1 = vf.relation(tf.tupleTypeOf(tf.integerType()));
		IRelation empty2 = vf.relation(tf.tupleTypeOf(tf.doubleType()));
		
		try {
			final IRelation union = empty1.intersect(empty2);
			if (!union.isEmpty()) {
				fail("empty union failed");
			}
			
			RelationType type = (RelationType) union.getType();
			if (!type.getFieldType(0).isNumberType()) {
				fail("union should produce lub types");
			}
		} catch (FactTypeError e) {
		    fail("union types which have a lub should be possible");
		}
		
		try {
			if (integerRelation.union(doubleRelation).size() != integerRelation.size() + doubleRelation.size())  {
				fail("non-intersecting non-intersectiopn relations should produce relation that is the sum of the sizes");
			}

			IRelation oneTwoThree = vf.relationWith(integerTuples[0],
					integerTuples[1], integerTuples[2]);
			IRelation threeFourFive = vf.relationWith(integerTuples[2],
					integerTuples[3], integerTuples[4]);
			IRelation result = vf.relationWith(integerTuples[0],
					integerTuples[1], integerTuples[2], integerTuples[3], integerTuples[4]);

			if (!oneTwoThree.union(threeFourFive).equals(result)) {
				fail("union failed");
			}
			if (!threeFourFive.union(oneTwoThree).equals(result)) {
				fail("union should be commutative");
			}
			
			if (!oneTwoThree.union(vf.relation(tf.tupleTypeOf(tf.integerType(),tf.integerType()))).equals(oneTwoThree)) {
				fail("union with empty set should produce same set");
			}

		} catch (FactTypeError e) {
			fail("the above should all be type safe");
		} 
	}

	public void testUnionISet() {
		IRelation empty1 = vf.relation(tf.tupleTypeOf(tf.integerType()));
		ISet empty2 = vf.set(tf.tupleTypeOf(tf.doubleType()));
		
		try {
			final IRelation union = empty1.intersect(empty2);
			if (!union.isEmpty()) {
				fail("empty union failed");
			}
			
			RelationType type = (RelationType) union.getType();
			if (!type.getFieldType(0).isNumberType()) {
				fail("union should produce lub types");
			}
		} catch (FactTypeError e) {
		    fail("union types which have a lub should be possible");
		}
		
		try {
			if (integerRelation.union(doubleRelation).size() != integerRelation.size() + doubleRelation.size())  {
				fail("non-intersecting non-intersectiopn relations should produce relation that is the sum of the sizes");
			}

			IRelation oneTwoThree = vf.relationWith(integerTuples[0],
					integerTuples[1], integerTuples[2]);
			ISet threeFourFive = vf.setWith(integerTuples[2],
					integerTuples[3], integerTuples[4]);
			IRelation result = vf.relationWith(integerTuples[0],
					integerTuples[1], integerTuples[2], integerTuples[3], integerTuples[4]);

			if (!oneTwoThree.union(threeFourFive).equals(result)) {
				fail("union failed");
			}
			if (!threeFourFive.union(oneTwoThree).equals(result)) {
				fail("union should be commutative");
			}
			
			if (!oneTwoThree.union(vf.set(tf.tupleTypeOf(tf.integerType(),tf.integerType()))).equals(oneTwoThree)) {
				fail("union with empty set should produce same set");
			}

		} catch (FactTypeError e) {
			fail("the above should all be type safe");
		} 
	}

	public void testIterator() {
		try {
			Iterator<ITuple> it = integerRelation.iterator();

			int i;
			for (i = 0; it.hasNext(); i++) {
				ITuple t = it.next();

				if (!integerRelation.contains(t)) {
					fail("iterator produces strange elements?");
				}
			}
			
			if (i != integerRelation.size()) {
				fail("iterator skipped elements");
			}
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
	}

	public void testToSet() {
		try {
			if (!integerRelation.toSet().toRelation().equals(integerRelation)) {
				fail("toSet and toRelation are supposed to be eachother's inverse");
			}
		} catch (FactTypeError e) {
			fail("should be type correct");
		}
		
		if (integerRelation.size() != integerRelation.toSet().size()) {
			fail("toSet should produce set of same size");
		}
	}

	public void testCarrier() {
		ISet carrier = integerRelation.carrier();
		
		if (!carrier.equals(setOfIntegers)) {
			fail("carrier should be equal to this set");
		}
	
		try {
			ITuple t1 = vf.tuple(integers[0], doubles[0]);
			ITuple t2 = vf.tuple(integers[1], doubles[1]);
			ITuple t3 = vf.tuple(integers[2], doubles[2]);
			IRelation rel1 = vf.relationWith(t1, t2, t3);
			
			ISet carrier1 = rel1.carrier();
			
			if (carrier1.getElementType() != tf.integerType()) {
				fail("expected number type on carrier");
			}
			
			if (carrier1.size() != 6) {
				fail("carrier does not contain all elements");
			}
			
			if (carrier1.intersect(setOfIntegers).size() != 3) {
				fail("integers should be in there still");
			}
			
			if (carrier1.intersect(setOfDoubles).size() != 3) {
				fail("doubles should be in there still");
			}
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
		
	}

	public void testGetWriter() {
		IRelation rel = vf.relation(tf.tupleTypeOf(tf.integerType(), tf.integerType()));
		IRelationWriter w = rel.getWriter();
		
		if (w == null) {
			fail("getWriter should never return null");
		}
		
		try {
			IRelationWriter w2 = rel.getWriter();
			
			if (w != w2) {
				fail("every value should have a single writer");
			}
		}
		catch (IllegalStateException e) {
			fail("should be able to get the same writer twice");
		}
		
		try {
			w.insert(vf.tuple(integers[0],integers[0]));
		} catch (FactTypeError e1) {
			fail("this should work");
		}
		
		w.done();
		
		try {
		  rel.getWriter();
		  fail("should not be able to get a writer after done was called");
		}
		catch (IllegalStateException e) {
			// this should happen
		}
	}
}
