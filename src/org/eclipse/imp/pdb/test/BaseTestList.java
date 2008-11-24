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

import org.eclipse.imp.pdb.facts.IList;
import org.eclipse.imp.pdb.facts.IListWriter;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.FactTypeError;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public abstract class BaseTestList extends TestCase {
    private IValueFactory vf;
    private TypeFactory tf = TypeFactory.getInstance();
    
    private IValue[] integers;
    private IList integerList;
    
	protected void setUp(IValueFactory factory) throws Exception {
		super.setUp();
		vf = factory;
		
		integers = new IValue[20];
		IListWriter w = vf.listWriter(tf.integerType());
		
		for (int i = 0; i < integers.length; i++) {
			integers[i] = vf.integer(i);
		}
		
		for (int i = integers.length - 1; i >= 0; i--) {
			w.insert(vf.integer(i));
		}
		
		integerList = w.done();
	}

	public void testGetElementType() {
		if (integerList.getElementType() != tf.integerType()) {
			fail("funny getElementType");
		}
		
		try {
			IList namedList = (IList) tf.namedType("myList", tf.listType(tf.integerType())).make(vf);
			if (namedList.getElementType() != tf.integerType()) {
				fail("named list has wrong elementtype");
			}
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
		
	}

	public void testAppend() {
		try {
			IValue newValue = vf.integer(integers.length);
			IList longer = integerList.append(newValue);
			
			if (longer.length() != integerList.length() + 1) {
				fail("append failed");
			}
			
			if (!longer.get(integerList.length()).equals(newValue)) {
				fail("element was not appended");
			}
			
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
		
		try {
			if (!integerList.append(vf.dubble(2)).getElementType().isValueType()) {
			  fail("append should lub the element type");
			}
		} catch (FactTypeError e) {
			// this should happen
		}
	}

	public void testGet() {
		for (int i = 0; i < integers.length; i++) {
			if (!integerList.get(i).equals(integers[i])) {
				fail("get failed");
			}
		}
	}

	public void testInsert() {
		try {
			IValue newValue = vf.integer(integers.length);
			IList longer = integerList.insert(newValue);
			
			if (longer.length() != integerList.length() + 1) {
				fail("append failed");
			}
			
			if (!longer.get(0).equals(newValue)) {
				fail("element was not insrrted");
			}
			
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
		
		try {
			if (!integerList.insert(vf.dubble(2)).getElementType().isValueType()) {
			  fail("insert should lub the element type");
			}
		} catch (FactTypeError e) {
			// this should happen
		}
	}

	public void testLength() {
		if (vf.list(tf.integerType()).length() != 0) {
			fail("empty list should be size 0");
		}
		
		if (integerList.length() != integers.length) {
			fail("length does not count amount of elements");
		}
	}

	public void testReverse() {
		IList reverse = integerList.reverse();
		
		if (reverse.getType() != integerList.getType()) {
			fail("reverse should keep type");
		}
		
		if (reverse.length() != integerList.length()) {
			fail("length of reverse is different");
		}
		
		for (int i = 0; i < integers.length; i++) {
			if (!reverse.get(i).equals(integers[integers.length - i - 1])) {
				fail("reverse did something funny: " + reverse + " is not reverse of " + integerList);
			}
		}
	}

	public void testIterator() {
		Iterator<IValue> it = integerList.iterator();
		
		int i;
		for (i = 0; it.hasNext(); i++) {
			IValue v = it.next();
			if (!v.equals(integers[i])) {
				fail("iterator does not iterate in order");
			}
		}
	}

}
