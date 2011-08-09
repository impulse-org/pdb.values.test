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
		suite.addTestSuite(TestIO.class);
		suite.addTestSuite(TestBinaryIO.class);

		addReferenceTests(suite);
		addFastTests(suite);
		addSharedTests(suite);
		
		return suite;
	}

	private static void addReferenceTests(TestSuite suite) {
		suite.addTestSuite(org.eclipse.imp.pdb.test.reference.TestAnnotations.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.reference.TestBasicValues.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.reference.TestRandomValues.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.reference.TestEquality.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.reference.TestList.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.reference.TestRelation.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.reference.TestSet.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.reference.TestValueFactory.class);
	}

	private static void addFastTests(TestSuite suite) {
		suite.addTestSuite(org.eclipse.imp.pdb.test.fast.TestRandomValues.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.fast.TestEquality.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.fast.TestAnnotations.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.fast.TestSet.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.fast.TestRelation.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.fast.TestList.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.fast.TestValueFactory.class);
	}

	private static void addSharedTests(TestSuite suite) {
		// these don't work
		// suite.addTestSuite(org.eclipse.imp.pdb.test.shared.TestRandomValues.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.shared.TestEquality.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.shared.TestAnnotations.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.shared.TestSet.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.shared.TestRelation.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.shared.TestList.class);
		suite.addTestSuite(org.eclipse.imp.pdb.test.shared.TestValueFactory.class);
	}

}
