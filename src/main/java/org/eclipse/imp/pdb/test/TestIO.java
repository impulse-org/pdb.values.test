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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.exceptions.FactTypeUseException;
import org.eclipse.imp.pdb.facts.impl.reference.ValueFactory;
import org.eclipse.imp.pdb.facts.io.StandardTextReader;
import org.eclipse.imp.pdb.facts.io.StandardTextWriter;
import org.eclipse.imp.pdb.facts.io.XMLReader;
import org.eclipse.imp.pdb.facts.io.XMLWriter;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;

public class TestIO extends TestCase {
	private static TypeStore ts = new TypeStore();
	private static TypeFactory tf = TypeFactory.getInstance();
	private static IValueFactory vf = ValueFactory.getInstance();
	private static Type Boolean = tf.abstractDataType(ts,"Boolean");
	
	private static Type Name = tf.abstractDataType(ts,"Name");
	private static Type True = tf.constructor(ts,Boolean, "true");
	private static Type False= tf.constructor(ts,Boolean, "false");
	private static Type And= tf.constructor(ts,Boolean, "and", Boolean, Boolean);
	private static Type Or= tf.constructor(ts,Boolean, "or", tf.listType(Boolean));
	private static Type Not= tf.constructor(ts,Boolean, "not", Boolean);
	private static Type TwoTups = tf.constructor(ts,Boolean, "twotups", tf.tupleType(Boolean, Boolean), tf.tupleType(Boolean, Boolean));
	private static Type NameNode  = tf.constructor(ts,Name, "name", tf.stringType());
	private static Type Friends = tf.constructor(ts,Boolean, "friends", tf.listType(Name));
	private static Type Couples = tf.constructor(ts,Boolean, "couples", tf.lrelType(Name, Name));
	
	private IValue[] testValues = {
			vf.constructor(True),
			vf.constructor(And, vf.constructor(True), vf.constructor(False)),
			vf.constructor(Not, vf.constructor(And, vf.constructor(True), vf.constructor(False))),
			vf.constructor(TwoTups, vf.tuple(vf.constructor(True), vf.constructor(False)),vf.tuple(vf.constructor(True), vf.constructor(False))),
			vf.constructor(Or, vf.list(vf.constructor(True), vf.constructor(False), vf.constructor(True))),
			vf.constructor(Friends, vf.list(name("Hans"), name("Bob"))),
			vf.constructor(Or, vf.list(Boolean)),
			vf.constructor(Couples, vf.listRelation(vf.tuple(name("A"), name("B")), vf.tuple(name("C"), name("D"))))
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
				StringWriter stream = new StringWriter();
				testWriter.write(test, stream);
				System.err.println(test + " -> " + stream.toString());
				
				if (!strip(stream.toString()).equals(testXML[i])) {
					fail(strip(stream.toString()) + " != " + testXML[i]);
				}
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			i++;
		}
	}
	
	private String strip(String string) {
		string = string.substring(string.lastIndexOf("?>")+2);
		string = string.replaceAll("\\s", "");
		return string;
	}

	private static IValue name(String n) {
		return vf.constructor(NameNode, vf.string(n));
	}
	
	public void testXMLReader() {
		XMLReader testReader = new XMLReader();
		
		try {
			for (int i = 0; i < testXML.length; i++) {
				IValue result = testReader.read(vf, ts, Boolean, new StringReader(testXML[i]));
				System.err.println(testXML[i] + " -> " + result);
				
				if (!result.isEqual(testValues[i])) {
					fail(testXML[i] + " did not parse correctly: " + result + " != " + testValues[i]);
				}
			}
		} catch (FactTypeUseException | IOException e) {
			e.printStackTrace();
			fail();
		}
    }
	
	public void testStandardReader() {
		StandardTextReader reader = new StandardTextReader();
		
		try {
		  IValue s = reader.read(vf,  new StringReader("\"a b c\""));
		  assertEquals(s, vf.string("a b c"));
		  
			IValue v = reader.read(vf, new StringReader("\"f\"(\"a b c\")"));
			assertEquals(v, vf.node("f", vf.string("a b c")));
			
			IValue r = reader.read(vf, new StringReader("[1.7976931348623157E+308]"));
			System.err.println(r);
			assertEquals(r, vf.list(vf.real("1.7976931348623157E+308")));
			
			
			IValue m = reader.read(vf, new StringReader("()"));
			System.err.println(m);
			assertEquals(m, vf.mapWriter().done());
			
			IValue t = reader.read(vf, new StringReader("<()>"));
			System.err.println(t);
			assertEquals(t, vf.tuple(vf.mapWriter().done()));
			
			StringWriter w = new StringWriter();
			new StandardTextWriter().write(vf.tuple(), w);
			IValue u = reader.read(vf, new StringReader(w.toString()));
      System.err.println(u);
      assertEquals(u, vf.tuple());
			
		} catch (FactTypeUseException | IOException e) {
			fail(e.getMessage());
		}
    }

	
}
