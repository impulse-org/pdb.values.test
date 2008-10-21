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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.impl.hash.ValueFactory;
import org.eclipse.imp.pdb.facts.io.XMLReader;
import org.eclipse.imp.pdb.facts.io.XMLWriter;
import org.eclipse.imp.pdb.facts.type.FactTypeError;
import org.eclipse.imp.pdb.facts.type.TreeNodeType;
import org.eclipse.imp.pdb.facts.type.TreeSortType;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public class TestIO extends TestCase {
	private static TypeFactory tf = TypeFactory.getInstance();
	private static IValueFactory vf = ValueFactory.getInstance();
	private static TreeSortType Boolean = tf.treeSortType("Boolean");
	
	private static TreeSortType Name = tf.treeSortType("Name");
	private static TreeNodeType True = tf.treeType(Boolean, "true");
	private static TreeNodeType False= tf.treeType(Boolean, "false");
	private static TreeNodeType And= tf.treeType(Boolean, "and", Boolean, Boolean);
	private static TreeNodeType Or= tf.treeType(Boolean, "or", tf.listType(Boolean));
	private static TreeNodeType Not= tf.treeType(Boolean, "not", Boolean);
	private static TreeNodeType TwoTups = tf.treeType(Boolean, "twotups", tf.tupleTypeOf(Boolean, Boolean), tf.tupleTypeOf(Boolean, Boolean));
	private static TreeNodeType NameNode  = tf.treeType(Name, "name", tf.stringType());
	private static TreeNodeType Friends = tf.treeType(Boolean, "friends", tf.listType(Name));
	private static TreeNodeType Couples = tf.treeType(Boolean, "couples", tf.listType(tf.tupleTypeOf(Name, Name)));
	
	private IValue[] testValues = {
			vf.tree(True),
			vf.tree(And, vf.tree(True), vf.tree(False)),
			vf.tree(Not, vf.tree(And, vf.tree(True), vf.tree(False))),
			vf.tree(TwoTups, vf.tuple(vf.tree(True), vf.tree(False)),vf.tuple(vf.tree(True), vf.tree(False))),
			vf.tree(Or, vf.listWith(vf.tree(True), vf.tree(False), vf.tree(True))),
			vf.tree(Friends, vf.listWith(name("Hans"), name("Bob"))),
			vf.tree(Or, vf.list(Boolean)),
			vf.tree(Couples, vf.listWith(vf.tuple(name("A"), name("B")), vf.tuple(name("C"), name("D"))))
	};
	
	private String[] testXML = {
		"<true/>",
		"<and><true/><false/></and>",
	    "<not><and><true/><false/></and></not>",
	    "<twotups><true/><false/><true/><false/></twotups>",
	    "<or><true/><false/><true/></or>",
	    "<friends><name>Hans</name><name>Bob</name></friends>",
	    "<or/>",
	    "<couples><name>A</name><name>B</name><name>C</name><name>D</name></couples>"
	    };

	public void testXMLWriter() {
		XMLWriter testWriter = new XMLWriter();
		
		for (IValue test : testValues) {
			try {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				testWriter.write(test, stream);
				System.err.println(test + " -> " + stream.toString());
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}
	
	private static IValue name(String n) {
		return vf.tree(NameNode, vf.string(n));
	}
	
	public void testXMLReader() {
		XMLReader testReader = new XMLReader();
		
		try {
			for (int i = 0; i < testXML.length; i++) {
				IValue result = testReader.read(vf, Boolean, new ByteArrayInputStream(testXML[i].getBytes()));
				System.err.println(testXML[i] + " -> " + result);
				
				if (!result.equals(testValues[i])) {
					fail(testXML[i] + " did not parse correctly: " + result + " != " + testValues[i]);
				}
			}
		} catch (FactTypeError e) {
			fail();
		} catch (IOException e) {
			fail();
		}
	}

	
}
