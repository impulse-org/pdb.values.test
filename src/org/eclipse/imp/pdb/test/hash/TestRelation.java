package org.eclipse.imp.pdb.test.hash;

import org.eclipse.imp.pdb.facts.impl.hash.ValueFactory;
import org.eclipse.imp.pdb.test.BaseTestRelation;

public class TestRelation extends BaseTestRelation {
	@Override
	protected void setUp() throws Exception {
		super.setUp(ValueFactory.getInstance());
	}

}
