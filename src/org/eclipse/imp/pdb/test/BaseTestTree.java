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

import org.eclipse.imp.pdb.facts.INode;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public abstract class BaseTestTree extends TestCase {
    private IValueFactory vf;
    private TypeFactory tf = TypeFactory.getInstance();
    
    Type Boolean = tf.abstractDataType("Boolean");
    Type BoolBinOp = tf.tupleType(Boolean, Boolean);
    Type BoolOp = tf.tupleType(Boolean);
    Type True = tf.constructor(Boolean, "true");
    Type False = tf.constructor(Boolean, "false");
    Type And = tf.constructor(Boolean, "and", BoolBinOp);
    Type Or = tf.constructor(Boolean, "and", BoolBinOp);
    Type Not = tf.constructor(Boolean, "not", BoolOp);
    
	protected void setUp(IValueFactory factory) throws Exception {
		super.setUp();
		vf = factory;
		
	}

	public void testImmutability() {
		INode test = genAndTree(2);
		INode ref = test;
		
		// do a modification
		test.set(0, vf.constructor(False, new IValue[0]));
		
		if (!ref.isEqual(test)) {
			fail("set modified a tree, which should be immutable");
		}
	}
	
	public void testSet() {
        INode test1 = genAndTree(5);
		INode test2 = genAndTree(2);
		INode refTest2 = genAndTree(2);
		INode test3 = test2.set(0, test1);
		
		if (test2.isEqual(test3)) {
			fail("set did not have an effect");
		}
		
		if (!refTest2.isEqual(test2)) {
			fail("set changed immutable tree");
		}
	}
	
	public void testEquality() {
		INode test1 = genAndTree(2);
		INode test2 = genAndTree(2);
		
		if (!test1.isEqual(test2)) {
			fail("trees should be equal");
		}
	}
	
	public INode genAndTree(int depth) {
		if (depth == 0) {
			return vf.constructor(True, new IValue[0]);
		}
		else {
			INode child = genAndTree(depth - 1);
			return vf.constructor(And, new IValue[] { child, child });
		}
	}
}
