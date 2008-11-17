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

import org.eclipse.imp.pdb.facts.ITree;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.TreeNodeType;
import org.eclipse.imp.pdb.facts.type.NamedTreeType;
import org.eclipse.imp.pdb.facts.type.TupleType;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public abstract class BaseTestTree extends TestCase {
    private IValueFactory vf;
    private TypeFactory tf = TypeFactory.getInstance();
    
    NamedTreeType Boolean = tf.namedTreeType("Boolean");
    TupleType BoolBinOp = tf.tupleType(Boolean, Boolean);
    TupleType BoolOp = tf.tupleType(Boolean);
    TreeNodeType True = tf.treeNodeType(Boolean, "true");
    TreeNodeType False = tf.treeNodeType(Boolean, "false");
    TreeNodeType And = tf.treeNodeType(Boolean, "and", BoolBinOp);
    TreeNodeType Or = tf.treeNodeType(Boolean, "and", BoolBinOp);
    TreeNodeType Not = tf.treeNodeType(Boolean, "not", BoolOp);
    
	protected void setUp(IValueFactory factory) throws Exception {
		super.setUp();
		vf = factory;
		
	}

	public void testImmutability() {
		ITree test = genAndTree(2);
		ITree ref = test;
		
		// do a modification
		test.set(0, vf.tree(False, new IValue[0]));
		
		if (!ref.equals(test)) {
			fail("set modified a tree, which should be immutable");
		}
	}
	
	public void testSet() {
        ITree test1 = genAndTree(5);
		ITree test2 = genAndTree(2);
		ITree refTest2 = genAndTree(2);
		ITree test3 = test2.set(0, test1);
		
		if (test2.equals(test3)) {
			fail("set did not have an effect");
		}
		
		if (!refTest2.equals(test2)) {
			fail("set changed immutable tree");
		}
	}
	
	public void testEquality() {
		ITree test1 = genAndTree(2);
		ITree test2 = genAndTree(2);
		
		if (!test1.equals(test2)) {
			fail("trees should be equal");
		}
	}
	
	public ITree genAndTree(int depth) {
		if (depth == 0) {
			return vf.tree(True, new IValue[0]);
		}
		else {
			ITree child = genAndTree(depth - 1);
			return vf.tree(And, new IValue[] { child, child });
		}
	}
}
