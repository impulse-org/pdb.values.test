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
import org.eclipse.imp.pdb.facts.exceptions.FactTypeUseException;
import org.eclipse.imp.pdb.facts.impl.reference.ValueFactory;
import org.eclipse.imp.pdb.facts.io.XMLReader;
import org.eclipse.imp.pdb.facts.io.XMLWriter;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public class TestIO extends TestCase {
	private static TypeFactory tf = TypeFactory.getInstance();
	private static IValueFactory vf = ValueFactory.getInstance();
	private static Type Boolean = tf.abstractDataType("Boolean");
	
	private static Type Name = tf.abstractDataType("Name");
	private static Type True = tf.constructor(Boolean, "true");
	private static Type False= tf.constructor(Boolean, "false");
	private static Type And= tf.constructor(Boolean, "and", Boolean, Boolean);
	private static Type Or= tf.constructor(Boolean, "or", tf.listType(Boolean));
	private static Type Not= tf.constructor(Boolean, "not", Boolean);
	private static Type TwoTups = tf.constructor(Boolean, "twotups", tf.tupleType(Boolean, Boolean), tf.tupleType(Boolean, Boolean));
	private static Type NameNode  = tf.constructor(Name, "name", tf.stringType());
	private static Type Friends = tf.constructor(Boolean, "friends", tf.listType(Name));
	private static Type Couples = tf.constructor(Boolean, "couples", tf.listType(tf.tupleType(Name, Name)));
	
	private IValue[] testValues = {
			vf.constructor(True),
			vf.constructor(And, vf.constructor(True), vf.constructor(False)),
			vf.constructor(Not, vf.constructor(And, vf.constructor(True), vf.constructor(False))),
			vf.constructor(TwoTups, vf.tuple(vf.constructor(True), vf.constructor(False)),vf.tuple(vf.constructor(True), vf.constructor(False))),
			vf.constructor(Or, vf.list(vf.constructor(True), vf.constructor(False), vf.constructor(True))),
			vf.constructor(Friends, vf.list(name("Hans"), name("Bob"))),
			vf.constructor(Or, vf.list(Boolean)),
			vf.constructor(Couples, vf.list(vf.tuple(name("A"), name("B")), vf.tuple(name("C"), name("D"))))
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
		return vf.constructor(NameNode, vf.string(n));
	}
	
	public void testXMLReader() {
		XMLReader testReader = new XMLReader();
		
		try {
			for (int i = 0; i < testXML.length; i++) {
				IValue result = testReader.read(vf, Boolean, new ByteArrayInputStream(testXML[i].getBytes()));
				System.err.println(testXML[i] + " -> " + result);
				
				if (!result.isEqual(testValues[i])) {
					fail(testXML[i] + " did not parse correctly: " + result + " != " + testValues[i]);
				}
			}
		} catch (FactTypeUseException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

	
}
