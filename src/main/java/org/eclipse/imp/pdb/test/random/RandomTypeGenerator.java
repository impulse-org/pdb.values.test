/*******************************************************************************
 * Copyright (c) 2009-2013 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   * Wietse Venema - wietsevenema@gmail.com - CWI
 *   * Jurgen Vinju - Jurgen.Vinju@cwi.nl - CWI
 *******************************************************************************/
package org.eclipse.imp.pdb.test.random;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;

public class RandomTypeGenerator {
	private final TypeFactory tf = TypeFactory.getInstance();
	private final LinkedList<Type> atomicTypes;
	private final Random random;

	public RandomTypeGenerator() {
		atomicTypes = new LinkedList<Type>();
		atomicTypes.add(tf.realType());
		atomicTypes.add(tf.integerType());
		atomicTypes.add(tf.rationalType());
		atomicTypes.add(tf.numberType());
		atomicTypes.add(tf.sourceLocationType());
		atomicTypes.add(tf.stringType());
		atomicTypes.add(tf.nodeType());
		atomicTypes.add(tf.boolType());
		atomicTypes.add(tf.dateTimeType());
		this.random = new Random();
	}
	
  public Type next(int maxDepth) {
		int cntRecursiveTypes = 4; // list, set, map, tuple
		int cntAtomicTypes = atomicTypes.size();

		if (maxDepth <= 0
				|| random.nextInt(cntAtomicTypes + cntRecursiveTypes) < cntAtomicTypes) {
			return getAtomicType();
		} else {
			return getRecursiveType(maxDepth - 1);
		}

	}

	private Type getRecursiveType(int maxDepth) {
		// list, set, map, relation, list relation, tuple
		switch (random.nextInt(4)) {
		case 0:
			return tf.listType(next(maxDepth));
		case 1:
			return tf.setType(next(maxDepth));
		case 2:
			return tf.mapType(next(maxDepth), next(maxDepth));
		case 3:
			return getTupleType(maxDepth);
		}
		return null;
	}

	private Type getTupleType(int maxDepth) {
		List<Type> l = getTypeList(maxDepth, 1);
		return tf.tupleType(l.toArray(new Type[l.size()]));
	}

	private List<Type> getTypeList(int maxLength, int minLength) {
		if (random.nextInt(2) == 0 || maxLength <= 0) {
			LinkedList<Type> l = new LinkedList<Type>();
			for (int i = 0; i < minLength; i++) {
				l.add(next(maxLength - 1));
			}
			return l;
		} 
		else {
			List<Type> l = getTypeList(maxLength - 1,
					Math.max(0, minLength - 1));
			l.add(next(maxLength - 1));
			return l;
		}
	}

	private Type getAtomicType() {
		return this.atomicTypes.get(random.nextInt(atomicTypes.size()));
	}
}



