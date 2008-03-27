package org.eclipse.imp.pdb.test;

import org.eclipse.imp.pdb.test.hash.TestList;
import org.eclipse.imp.pdb.test.hash.TestRelation;
import org.eclipse.imp.pdb.test.hash.TestSet;
import org.eclipse.imp.pdb.test.hash.TestValueFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	// TODO: this test suite tests the basic functionality of sets, relations and lists;
	// it also checks the functionality of the type factory and the computation of 
	// the least upperbound of types and the isSubtypeOf method. It needs more tests
	// for named types and the way they are checked and produced by the implementations
	// of IRelation, ISet and IList.
	
	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.imp.pdb");
	
		suite.addTestSuite(TestType.class);
		suite.addTestSuite(TestTypeFactory.class);
		suite.addTestSuite(TestSet.class);
		suite.addTestSuite(TestRelation.class);
		suite.addTestSuite(TestList.class);
		suite.addTestSuite(TestValueFactory.class);
		
		return suite;
	}

}
