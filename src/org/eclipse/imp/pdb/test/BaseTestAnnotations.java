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
import org.eclipse.imp.pdb.facts.type.FactTypeError;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeDeclarationException;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public abstract class BaseTestAnnotations extends TestCase {
    private IValueFactory vf;
    private TypeFactory tf = TypeFactory.getInstance();
    private Type E;
    private Type N;
    
	protected void setUp(IValueFactory factory) throws Exception {
		super.setUp();
		vf = factory;
		E = tf.namedTreeType("E");
		N = tf.treeNodeType(E, "n", tf.integerType());
	}
	
	public void testDeclarationOnNonAllowedType() {
		try {
			tf.declareAnnotation(tf.integerType(), "a", tf.integerType());
		}
		catch (TypeDeclarationException e) {
			// this should happen
		}
		try {
			tf.declareAnnotation(tf.doubleType(), "a", tf.integerType());
		}
		catch (TypeDeclarationException e) {
			// this should happen
		}
	}
	
	public void testDoubleDeclaration() {
		try {
			tf.declareAnnotation(E, "size", tf.integerType());
		}
		catch (TypeDeclarationException e) {
			fail(e.toString());
		}
		catch (FactTypeError e) {
			fail(e.toString());
		}
		
		try {
			tf.declareAnnotation(E, "size", tf.doubleType());
			fail("double declaration is not allowed");
		}
		catch (TypeDeclarationException e) {
			// this should happen
		}
	}
	
	public void testSetAnnotation() {
		INode n = (INode) N.make(vf, vf.integer(0));
		tf.declareAnnotation(E, "size", tf.integerType());
		
		try {
			n.setAnnotation("size2", vf.integer(0));
			fail("can not set annotation that is not declared");
		}
		catch (FactTypeError e) {
			// this should happen
		}
		
		try {
			n.setAnnotation("size", vf.integer(0));
		}
		catch (TypeDeclarationException e) {
			fail(e.toString());
		}
		catch (FactTypeError e) {
			fail(e.toString());
		}
	}
	
	public void testGetAnnotation() {
		INode n = (INode) N.make(vf, vf.integer(0));
		tf.declareAnnotation(E, "size", tf.integerType());
		
		try {
			n.getAnnotation("size2");
			fail();
		}
		catch (FactTypeError e) {
			// this should happen
		}
		
		try {
			if (n.getAnnotation("size") != null) {
				fail("annotation should be null");
			}
		} catch (FactTypeError e) {
			fail(e.toString());
		}
		
		INode m = n.setAnnotation("size", vf.integer(1));
		IValue b = m.getAnnotation("size");
		if (!b.equals(vf.integer(1))) {
			fail();
		}
	}
	
	public void testImmutability() {
		INode n = (INode) N.make(vf, vf.integer(0));
		tf.declareAnnotation(E, "size", tf.integerType());
		
		INode m = n.setAnnotation("size", vf.integer(1));
		
		if (m == n) {
			fail("annotation setting should change object identity");
		}
		
		if (m.equals(n)) {
			fail("setting an annotation should change equality");
		}
	}
	
	public void testDeclaresAnnotation() {
		INode n = (INode) N.make(vf, vf.integer(0));
		tf.declareAnnotation(E, "size", tf.integerType());
		
		if (!n.declaresAnnotation("size")) {
			fail();
		}
		
		if (n.declaresAnnotation("size2")) {
			fail();
		}
	}
	public void testHasAnnotation() {
		INode n = (INode) N.make(vf, vf.integer(0));
		tf.declareAnnotation(E, "size", tf.integerType());
		
		try {
			n.hasAnnotation("size2");
			fail("can not set annotation that is not declared");
		}
		catch (FactTypeError e) {
			// this should happen
		}
		
	}

	
}
