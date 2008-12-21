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
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public class TestIO extends TestCase {
	private static TypeFactory tf = TypeFactory.getInstance();
	private static IValueFactory vf = ValueFactory.getInstance();
	private static Type Boolean = tf.namedTreeType("Boolean");
	
	private static Type Name = tf.namedTreeType("Name");
	private static Type True = tf.treeNodeType(Boolean, "true");
	private static Type False= tf.treeNodeType(Boolean, "false");
	private static Type And= tf.treeNodeType(Boolean, "and", Boolean, Boolean);
	private static Type Or= tf.treeNodeType(Boolean, "or", tf.listType(Boolean));
	private static Type Not= tf.treeNodeType(Boolean, "not", Boolean);
	private static Type TwoTups = tf.treeNodeType(Boolean, "twotups", tf.tupleType(Boolean, Boolean), tf.tupleType(Boolean, Boolean));
	private static Type NameNode  = tf.treeNodeType(Name, "name", tf.stringType());
	private static Type Friends = tf.treeNodeType(Boolean, "friends", tf.listType(Name));
	private static Type Couples = tf.treeNodeType(Boolean, "couples", tf.listType(tf.tupleType(Name, Name)));
	
	private IValue[] testValues = {
			vf.tree(True),
			vf.tree(And, vf.tree(True), vf.tree(False)),
			vf.tree(Not, vf.tree(And, vf.tree(True), vf.tree(False))),
			vf.tree(TwoTups, vf.tuple(vf.tree(True), vf.tree(False)),vf.tuple(vf.tree(True), vf.tree(False))),
			vf.tree(Or, vf.list(vf.tree(True), vf.tree(False), vf.tree(True))),
			vf.tree(Friends, vf.list(name("Hans"), name("Bob"))),
			vf.tree(Or, vf.list(Boolean)),
			vf.tree(Couples, vf.list(vf.tuple(name("A"), name("B")), vf.tuple(name("C"), name("D"))))
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
		int i = 0;
		for (IValue test : testValues) {
			try {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				testWriter.write(test, stream);
				System.err.println(test + " -> " + stream.toString());
				
				if (strip(stream.toString()).equals(testXML[i])) {
					fail(stream.toString() + " != " + testXML[i]);
				}
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
		i++;
	}
	
	private String strip(String string) {
		string = string.substring(string.lastIndexOf("?>"));
		string = string.replaceAll(" ", "");
		return string;
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
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

	
}
