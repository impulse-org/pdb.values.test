package org.eclipse.imp.pdb.test;

import junit.framework.TestCase;

import org.eclipse.imp.pdb.facts.INumber;
import org.eclipse.imp.pdb.facts.IReal;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

abstract public class BaseTestBasicValues extends TestCase {
	protected IValueFactory vf;
	protected TypeFactory tf = TypeFactory.getInstance();

	// TODO add more test cases
	protected void setUp(IValueFactory factory) throws Exception {
		super.setUp();
		vf = factory;
	}
	
	protected void assertEqual(IValue l, IValue r) {
		assertTrue(l.isEqual(r));
	}
	
	public void testIntAddition() {
		assertTrue(vf.integer(1).add(vf.integer(1)).isEqual(vf.integer(2)));
	}
	
	public void testReal() {
		assertTrue(vf.real("1.5").floor().isEqual(vf.real("1")));
		assertTrue(vf.real("1.5").round().isEqual(vf.real("2")));
	}
	
	public void testNumberMakeInt() {
		assertTrue(tf.numberType().make(vf, 1).isEqual(vf.integer(1)));
	}
	
	public void testNumberMakeReal() {
		assertTrue(tf.numberType().make(vf, 1.0).isEqual(vf.real(1.0)));
	}

	public void testNumberSubTypes() {
		assertTrue(tf.integerType().isSubtypeOf(tf.numberType()));
		assertFalse(tf.numberType().isSubtypeOf(tf.integerType()));
		assertTrue(tf.realType().isSubtypeOf(tf.numberType()));
		assertFalse(tf.numberType().isSubtypeOf(tf.realType()));
		
		assertTrue(tf.integerType().lub(tf.realType()).equivalent(tf.numberType()));
		assertTrue(tf.integerType().lub(tf.numberType()).equivalent(tf.numberType()));
		assertTrue(tf.realType().lub(tf.numberType()).equivalent(tf.numberType()));
	}
	
	public void testNumberArithmatic() {
		INumber i1 = (INumber) tf.numberType().make(vf, 1);
		INumber i2 = (INumber) tf.numberType().make(vf, 2);
		INumber r1 = (INumber) tf.numberType().make(vf, 1.0);
		INumber r2 = (INumber) tf.numberType().make(vf, 2.0);
		
		assertEqual(i1.add(i2),vf.integer(3));
		assertEqual(i1.add(r2),vf.real(3));
		assertEqual(r1.add(r2),vf.real(3));
		assertEqual(r1.add(i2),vf.real(3));
		
		assertEqual(i1.subtract(i2),vf.integer(-1));
		assertEqual(i1.subtract(r2),vf.real(-1));
		assertEqual(r1.subtract(r2),vf.real(-1));
		assertEqual(r1.subtract(i2),vf.real(-1));
		
		INumber i5 =  (INumber) tf.numberType().make(vf, 5);
		assertEqual(i5.divide(i2, 80*80),vf.real(2.5));
	}
	
	
	public void testPreciseRealDivision() {
		IReal e100 = vf.real("1E100");
		IReal maxDiff = vf.real("1E-6300");
		IReal r9 = vf.real("9");
		assertTrue(e100.subtract(e100.divide(r9,80*80).multiply(r9)).lessEqual(maxDiff).getValue());
	}
}
