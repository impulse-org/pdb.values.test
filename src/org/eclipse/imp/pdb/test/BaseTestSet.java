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

import org.eclipse.imp.pdb.facts.IRelation;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.ISetWriter;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.FactTypeError;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

import junit.framework.TestCase;

public abstract class BaseTestSet extends TestCase {
	private IValueFactory vf;
	private TypeFactory tf;
	private IValue[] integers;
	private IValue[] doubles;
	private ISet integerUniverse;

	protected void setUp(IValueFactory factory) throws Exception {
		super.setUp();
		this.vf = factory;
		this.tf = TypeFactory.getInstance();
		
		integers = new IValue[100];
		for (int i = 0; i < integers.length; i++) {
			integers[i] = vf.integer(i);
		}
		
		doubles = new IValue[100];
		for (int i = 0; i < doubles.length; i++) {
			doubles[i] = vf.dubble(i);
		}
		
		integerUniverse = vf.set(tf.integerType());
		ISetWriter w = integerUniverse.getWriter();
		
		try {
			for (IValue v : integers) {
				w.insert(v);
			}
		} catch (FactTypeError e) {
			fail("this should be type correct");
		}
	}

	public void testGetWriter() {
		ISet set = vf.set(tf.integerType());
		ISetWriter w = set.getWriter();
		
		if (w == null) {
			fail("getWriter should never return null");
		}
		
		try {
			ISetWriter w2 = set.getWriter();
			
			if (w != w2) {
				fail("every value should have a single writer");
			}
		}
		catch (IllegalStateException e) {
			fail("should be able to get the same writer twice");
		}
		
		try {
			w.insert(integers[0]);
		} catch (FactTypeError e1) {
			fail("this should work");
		}
		
		w.done();
		
		try {
		  set.getWriter();
		  fail("should not be able to get a writer after done was called");
		}
		catch (IllegalStateException e) {
			// this should happen
		}
	}

	public void testInsert() {
		ISet set1 = vf.set(tf.integerType());
		ISet set2;
		
		try {
			set2 = set1.insert(integers[0]);

			if (set2.size() != 1) {
				fail("insertion failed");
			}
			
			if (!set2.contains(integers[0])) {
				fail("insertion failed");
			}
			
			try {
				set2.getWriter();
				fail("should return immutable values");
			} catch (IllegalStateException e) {
				// should happen
			}
		} catch (FactTypeError e1) {
			fail("type checking error");
		}
		
		try {
			set2 = set1.insert(doubles[0]);
			fail("should not be able to insert doubles");
		} catch (FactTypeError e) {
			// this should happen
		}
		
		ISet numberSet = vf.set(tf.numberType());
		
		try {
			numberSet.getWriter().insert(integers[0]);
			numberSet.getWriter().insert(doubles[0]);
		} catch (FactTypeError e) {
			fail("should be able to insert subtypes");
		}
	}

	public void testContains() {
		ISet set1 = vf.setWith(integers[0], integers[1]);
		
		try {
			set1.contains(doubles[0]);
			fail("should not be able to check for type that can not be contained");
		} catch (FactTypeError e) {
			// this should happen
		}
		
		try {
			set1.contains(integers[0]);
		} catch (FactTypeError e) {
			fail("should be able to check for containment of integers");
		}
	}

	public void testIntersect() {
		ISet set1 = vf.set(tf.integerType());
		ISet set2 = vf.set(tf.integerType());
		ISet set3 = vf.setWith(integers[0], integers[1], integers[2]);
		ISet set4 = vf.setWith(integers[2], integers[3], integers[4]);
		ISet set5 = vf.setWith(integers[3], integers[4], integers[5]);
		
		try {
			if (!set1.intersect(set2).isEmpty()) {
				fail("intersect of empty sets");
			}
			
			if (!set1.intersect(set3).isEmpty()) {
				fail("intersect with empty set");
			}
			
			if (!set3.intersect(set1).isEmpty()) {
				fail("insersect with empty set");
			}
			
			if (set3.intersect(set4).size() != 1) {
				fail("insersect failed");
			}
			
			if (!set4.intersect(set3).contains(integers[2])) {
				fail("intersect failed");
			}
			
			if (set4.intersect(set5).size() != 2) {
				fail("insersect failed");
			}
			
			if (!set5.intersect(set4).contains(integers[3]) 
					|| !set5.intersect(set4).contains(integers[4])) {
				fail("intersect failed");
			}
			
			if (!set5.intersect(set3).isEmpty()) {
				fail("non-intersection sets");
			}
			
			try {
			  set1.intersect(set2).getWriter();
			  fail("intersection should return immutable set");
			}
			catch (IllegalStateException e) {
		      // this should happen
			}
			
		} catch (FactTypeError et) {
			fail("this shouls all be typesafe");
		}
		
		ISet dSet = vf.set(tf.doubleType());
		
		try {
			if (dSet.intersect(set1).getElementType() != tf.numberType()) {
				fail("intersect should produce lub types");
			}
		} catch (FactTypeError e) {
			// this should not happen
		}
	}

	public void testInvert() {
		ISet set1 = vf.setWith(integers[0], integers[1], integers[2]);
		
		try {
			if (set1.invert(integerUniverse).size() != integerUniverse.size() - set1.size()) {
				fail("universe failed");
			}
			
			if (!set1.invert(integerUniverse).intersect(set1).isEmpty()) {
				fail("invert is funny");
			}
			
			ISet set2 = set1.insert(vf.integer(integers.length));
			
			try {
				set2.invert(integerUniverse);
				fail("integerUniverse is not a universe for set, since it is not a superset");
			}
			catch (FactTypeError e) {
				// this should happen
			}
		} catch (FactTypeError e) {
			fail("should be type correct");
		}
	}

	public void testIsEmpty() {
		if (integerUniverse.isEmpty()) {
			fail("an empty universe is not so cosy");
		}
		
		if (!vf.set(tf.integerType()).isEmpty()) {
			fail("what's in an empty set?");
		}
	}

	public void testSize() {
		if (vf.set(tf.integerType()).size() != 0) {
			fail("empty sets have size 0");
		}
		
		if (vf.setWith(integers[0]).size() != 1) {
			fail("singleton set should have size 1");
		}
		
		if (integerUniverse.size() != integers.length) {
			fail("weird size of universe");
		}
	}

	public void testSubtract() {
		ISet set1 = vf.set(tf.integerType());
		ISet set2 = vf.set(tf.integerType());
		ISet set3 = vf.setWith(integers[0], integers[1], integers[2]);
		ISet set4 = vf.setWith(integers[2], integers[3], integers[4]);
		ISet set5 = vf.setWith(integers[3], integers[4], integers[5]);
		
		try {
			if (!set1.subtract(set2).isEmpty()) {
				fail("subtract of empty sets");
			}
			
			if (!set1.subtract(set3).isEmpty()) {
				fail("subtract with empty set");
			}
			
			if (!set3.subtract(set1).equals(set3)) {
				fail("subtract with empty set");
			}
			
			if (!set1.subtract(set3).equals(set1)) {
				fail("subtract with empty set");
			}
			
			if (set3.subtract(set4).size() != 2) {
				fail("subtract failed");
			}
			
			if (set4.subtract(set3).contains(integers[2])) {
				fail("subtract failed");
			}
			
			if (set4.subtract(set5).size() != 1) {
				fail("insersect failed");
			}
			
			if (set5.subtract(set4).contains(integers[3]) 
					|| set5.subtract(set4).contains(integers[4])) {
				fail("subtract failed");
			}
			
			try {
			  set1.subtract(set2).getWriter();
			  fail("subtraction should return immutable set");
			}
			catch (IllegalStateException e) {
		      // this should happen
			}
			
		} catch (FactTypeError et) {
			fail("this shouls all be typesafe");
		}
		
		ISet dSet = vf.set(tf.doubleType());
		
		try {
			if (dSet.subtract(set1).getElementType() != tf.numberType()) {
				fail("subtract should produce lub types");
			}
		} catch (FactTypeError e) {
			// this should not happen
		}
	}

	public void testUnion() {
		ISet set1 = vf.set(tf.integerType());
		ISet set2 = vf.set(tf.integerType());
		ISet set3 = vf.setWith(integers[0], integers[1], integers[2]);
		ISet set4 = vf.setWith(integers[2], integers[3], integers[4]);
		ISet set5 = vf.setWith(integers[3], integers[4], integers[5]);
		
		try {
			if (!set1.union(set2).isEmpty()) {
				fail("union of empty sets");
			}
			
			if (!set1.union(set3).equals(set3)) {
				fail("union with empty set");
			}
			
			if (!set3.union(set1).equals(set3)) {
				fail("union with empty set");
			}
			
			if (!set1.union(set3).equals(set3)) {
				fail("union with empty set");
			}
			
			if (set3.union(set4).size() != 5) {
				fail("union failed");
			}
			
			if (!set4.union(set3).contains(integers[0])
					|| !set4.union(set3).contains(integers[1])
					|| !set4.union(set3).contains(integers[2])
					|| !set4.union(set3).contains(integers[3])
					|| !set4.union(set3).contains(integers[4])
					) {
				fail("union failed");
			}
			
			if (set4.union(set5).size() != 4) {
				fail("union failed");
			}
			
			try {
			  set1.union(set2).getWriter();
			  fail("unionion should return immutable set");
			}
			catch (IllegalStateException e) {
		      // this should happen
			}
			
		} catch (FactTypeError et) {
			fail("this shouls all be typesafe");
		}
		
		ISet dSet = vf.set(tf.doubleType());
		
		try {
			if (dSet.union(set1).getElementType() != tf.numberType()) {
				fail("union should produce lub types");
			}
		} catch (FactTypeError e) {
			// this should not happen
		}
	}

	public void testIterator() {
		try {
			Iterator<IValue> it = integerUniverse.iterator();
			int i;
			for (i = 0; it.hasNext(); i++) {
				if (!integerUniverse.contains(it.next())) {
					fail("iterator produces something weird");
				}
			}
			if (i != integerUniverse.size()) {
				fail("iterator did not iterate over everything");
			}
		} catch (FactTypeError e) {
			fail("should be type correct");
		} 
	}

	public void testToRelation() {
		try {
		  integerUniverse.toRelation();
		  fail("should not be able to cast set that has no tuples");
		} catch (FactTypeError e) {
			// this should happen
		} 
		
		ISet set = vf.setWith(vf.tuple(integers[0], doubles[0]));
		
		try {
			IRelation rel = set.toRelation();
			
			try {
				rel.getWriter();
				fail("toRelation should return an immutable set");
			} catch (IllegalStateException e) {
				// this should happen
			}
			
			for (ITuple v : rel) {
				if (!set.contains(v)) {
					fail("toRel invented elements");
				}
			}
			
			for (IValue v : set) {
				if (!rel.contains((ITuple) v)) {
					fail("toRel forgot elements");
				}
			}
			
		} catch (FactTypeError e) {
			fail("this toRelation is allowed");
		}
	}

	public void testGetElementType() {
		if (!integerUniverse.getElementType().isIntegerType()) {
			fail("elementType is broken");
		}
	}

	public void testProductISet() {
		ISet test = vf.setWith(integers[0], integers[1], integers[2],integers[3]);
		IRelation prod = test.product(test);
		
		if (prod.arity() != 2) {
			fail("product's arity should be 2");
		}
		
		if (prod.size() != test.size() *  test.size()) {
			fail("product's size should be square of size");
		}
		
		try {
			prod.getWriter();
			fail("prod should return an immutable value");
		}
		catch (IllegalStateException e) {
			// this should happen
		}
	}
	
	public void testProductIRelation() {
		ISet test = vf.setWith(integers[0], integers[1], integers[2],integers[3]);
		IRelation prod = test.product(test);
		IRelation prod2 = test.product(prod);
		
		if (prod2.arity() != 3) {
			fail("product's arity should be 3");
		}
		
		if (prod2.size() != prod.size() * test.size()) {
			fail("product's size should be multiplication of arguments' sizes");
		}
		
		try {
			prod.getWriter();
			fail("prod should return an immutable value");
		}
		catch (IllegalStateException e) {
			// this should happen
		}
	}
}
