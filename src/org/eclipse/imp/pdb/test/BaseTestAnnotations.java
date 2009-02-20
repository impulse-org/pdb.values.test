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

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.exceptions.FactTypeUseException;
import org.eclipse.imp.pdb.facts.exceptions.FactTypeDeclarationException;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;

public abstract class BaseTestAnnotations extends TestCase {
    private IValueFactory vf;
    private TypeFactory tf = TypeFactory.getInstance();
    private TypeStore ts = new TypeStore();
    private Type E;
    private Type N;
    
	protected void setUp(IValueFactory factory) throws Exception {
		super.setUp();
		vf = factory;
		E = tf.abstractDataType("E");
		N = tf.constructor(E, "n", tf.integerType());
	}
	
	public void testDeclarationOnNonAllowedType() {
		try {
			ts.declareAnnotation(tf.integerType(), "a", tf.integerType());
		}
		catch (FactTypeDeclarationException e) {
			// this should happen
		}
		try {
			ts.declareAnnotation(tf.doubleType(), "a", tf.integerType());
		}
		catch (FactTypeDeclarationException e) {
			// this should happen
		}
	}
	
	public void testDoubleDeclaration() {
		try {
			ts.declareAnnotation(E, "size", tf.integerType());
		}
		catch (FactTypeDeclarationException e) {
			fail(e.toString());
		}
		catch (FactTypeUseException e) {
			fail(e.toString());
		}
		
		try {
			ts.declareAnnotation(E, "size", tf.doubleType());
			fail("double declaration is not allowed");
		}
		catch (FactTypeDeclarationException e) {
			// this should happen
		}
	}
	
	public void testSetAnnotation() {
		IConstructor n = (IConstructor) N.make(vf, vf.integer(0));
		ts.declareAnnotation(E, "size", tf.integerType());
		
		try {
			n.setAnnotation(ts, "size2", vf.integer(0));
			fail("can not set annotation that is not declared");
		}
		catch (FactTypeUseException e) {
			// this should happen
		}
		
		try {
			n.setAnnotation("size", vf.integer(0));
		}
		catch (FactTypeDeclarationException e) {
			fail(e.toString());
		}
		catch (FactTypeUseException e) {
			fail(e.toString());
		}
	}
	
	public void testGetAnnotation() {
		IConstructor n = (IConstructor) N.make(vf, vf.integer(0));
		ts.declareAnnotation(E, "size", tf.integerType());
		
		try {
			n.getAnnotation(ts, "size2");
			fail();
		}
		catch (FactTypeUseException e) {
			// this should happen
		}
		
		try {
			if (n.getAnnotation("size") != null) {
				fail("annotation should be null");
			}
		} catch (FactTypeUseException e) {
			fail(e.toString());
		}
		
		IConstructor m = n.setAnnotation("size", vf.integer(1));
		IValue b = m.getAnnotation("size");
		if (!b.isEqual(vf.integer(1))) {
			fail();
		}
	}
	
	public void testImmutability() {
		IConstructor n = (IConstructor) N.make(vf, vf.integer(0));
		ts.declareAnnotation(E, "size", tf.integerType());
		
		IConstructor m = n.setAnnotation("size", vf.integer(1));
		
		if (m == n) {
			fail("annotation setting should change object identity");
		}
		
		if (m.isEqual(n)) {
			fail("setting an annotation should change equality");
		}
	}
	
	public void testDeclaresAnnotation() {
		IConstructor n = (IConstructor) N.make(vf, vf.integer(0));
		ts.declareAnnotation(E, "size", tf.integerType());
		
		if (!n.declaresAnnotation(ts, "size")) {
			fail();
		}
		
		if (n.declaresAnnotation(ts, "size2")) {
			fail();
		}
	}
	public void testHasAnnotation() {
		IConstructor n = (IConstructor) N.make(vf, vf.integer(0));
		ts.declareAnnotation(E, "size", tf.integerType());
		
		try {
			n.hasAnnotation(ts, "size2");
			fail("can not set annotation that is not declared");
		}
		catch (FactTypeUseException e) {
			// this should happen
		}
		
	}

	
}
