package org.eclipse.imp.pdb.test;

import junit.framework.TestCase;

import org.eclipse.imp.pdb.facts.IValueFactory;

abstract public class BaseTestBasicValues extends TestCase {
	protected IValueFactory vf;

	// TODO add more test cases
	protected void setUp(IValueFactory factory) throws Exception {
		super.setUp();
		vf = factory;
	}
	
	public void testInt() {
		assertTrue(vf.integer(1).add(vf.integer(1)).isEqual(vf.integer(2)));
	}
	
	public void testReal() {
		assertTrue(vf.real("1.5").floor().isEqual(vf.real("1")));
		assertTrue(vf.real("1.5").round().isEqual(vf.real("2")));
	}
}
