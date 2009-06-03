/*******************************************************************************
* Copyright (c) 2009 Centrum Wiskunde en Informatica (CWI)
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Arnold Lankamp - interfaces and implementation
*******************************************************************************/
package org.eclipse.imp.pdb.test.fast;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Arnold Lankamp
 */
public class AllTests{
	public static Test suite(){
		TestSuite suite = new TestSuite("Test for fast PDB implementation.");
	
		suite.addTestSuite(TestEquality.class);
		suite.addTestSuite(TestAnnotations.class);
		suite.addTestSuite(TestSet.class);
		suite.addTestSuite(TestRelation.class);
		suite.addTestSuite(TestList.class);
		suite.addTestSuite(TestValueFactory.class);
		
		return suite;
	}

}
