package org.eclipse.imp.pdb.test;

import java.util.Iterator;

import org.eclipse.imp.pdb.facts.IList;
import org.eclipse.imp.pdb.facts.IListWriter;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.FactTypeError;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

import junit.framework.TestCase;

public abstract class BaseTestList extends TestCase {
    private IValueFactory vf;
    private TypeFactory tf = TypeFactory.getInstance();
    
    private IValue[] integers;
    private IList integerList;
    
	protected void setUp(IValueFactory factory) throws Exception {
		super.setUp();
		vf = factory;
		
		integers = new IValue[20];
		integerList = vf.list(tf.integerType());
		IListWriter w = integerList.getWriter();
		
		for (int i = 0; i < integers.length; i++) {
			integers[i] = vf.integer(i);
		}
		
		for (int i = integers.length - 1; i >= 0; i--) {
			w.insert(vf.integer(i));
		}
	}

	public void testGetElementType() {
		if (integerList.getElementType() != tf.integerType()) {
			fail("funny getElementType");
		}
		
		try {
			IList namedList = vf.list(tf.namedType("myList", tf.listType(tf.integerType())));
			
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
			
			try {
				longer.getWriter();
				fail("append should return an immutable value");
			}
			catch (IllegalStateException e) {
				// this should happen
			}
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
		
		try {
			integerList.append(vf.dubble(2));
			fail("should not be able to insert a double into an integer list");
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
			
			try {
				longer.getWriter();
				fail("insert should return an immutable value");
			}
			catch (IllegalStateException e) {
				// this should happen
			}
		} catch (FactTypeError e) {
			fail("the above should be type correct");
		}
		
		try {
			integerList.insert(vf.dubble(2));
			fail("should not be able to insert a double into an integer list");
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
				fail("reverse did something funny");
			}
		}
		
		try {
		  reverse.getWriter();
		  fail("reverse should return an immutable list");
		}
		catch (IllegalStateException e) {
			// this should happen
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

	public void testGetWriter() {
		try {
			IListWriter w1 = integerList.getWriter();
			IListWriter w2 = integerList.getWriter();
			
			if (w1 != w2) {
				fail("writer should be single for a single value");
			}
			
			w1.done();
			
			try {
				w2.insert(integers[0]);
			} catch (IllegalStateException e) {
				// this should happen
			} catch (FactTypeError e) {
				fail("the insertion should be type safe");
			}
		} catch (IllegalStateException e) {
			fail("should be able to get writer more than once");
		}
	}
}
