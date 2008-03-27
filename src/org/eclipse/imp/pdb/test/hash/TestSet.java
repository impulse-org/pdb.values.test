package org.eclipse.imp.pdb.test.hash;

import org.eclipse.imp.pdb.facts.impl.hash.ValueFactory;
import org.eclipse.imp.pdb.test.BaseTestSet;

public class TestSet extends BaseTestSet {

	@Override
	protected void setUp() throws Exception {
		super.setUp(ValueFactory.getInstance());
	}
}
