package org.eclipse.imp.pdb.test;

import junit.framework.TestCase;

import org.eclipse.imp.pdb.facts.IReal;
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
	
	public void testPreciseRealDivision() {
		IReal e100 = vf.real("1E100");
		IReal maxDiff = vf.real("1E-6300");
		IReal r9 = vf.real("9");
		assertTrue(e100.subtract(e100.divide(r9,80*80).multiply(r9)).lessEqual(maxDiff).getValue());
	}
}
