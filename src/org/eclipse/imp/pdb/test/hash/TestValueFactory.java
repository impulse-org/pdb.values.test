package org.eclipse.imp.pdb.test.hash;

import org.eclipse.imp.pdb.facts.impl.hash.ValueFactory;
import org.eclipse.imp.pdb.test.BaseTestValueFactory;


public class TestValueFactory extends BaseTestValueFactory {

	@Override
	protected void setUp() throws Exception {
		super.setUp(ValueFactory.getInstance());
	}
}
