package org.eclipse.imp.pdb.test.hash;

import org.eclipse.imp.pdb.facts.impl.hash.ValueFactory;
import org.eclipse.imp.pdb.test.BaseTestList;

public class TestList extends BaseTestList {

	@Override
	protected void setUp() throws Exception {
		super.setUp(ValueFactory.getInstance());
	}
}
