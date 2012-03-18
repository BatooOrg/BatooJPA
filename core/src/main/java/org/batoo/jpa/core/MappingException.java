/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import org.batoo.jpa.core.util.Pair;

/**
 * Exception to indicate a mapping exception
 * 
 * @since $version
 * @author hceylan
 */
public class MappingException extends BatooException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param member
	 * @param annotations
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private static String createMessage(String member, List<Annotation> annotations) {
		final StringBuffer sb = new StringBuffer(member + " has redundant annotations:");

		for (final Annotation annotation : annotations) {
			sb.append("\n\t");
			sb.append(annotation);
		}

		return sb.toString();
	}

	private static String createMessage(String javaMember, Set<Pair<Class<? extends Annotation>>> conflicts) {
		final StringBuffer sb = new StringBuffer(javaMember + " has conflicting annotations:");

		for (final Pair<Class<? extends Annotation>> conflict : conflicts) {
			sb.append("\n\t");
			sb.append(conflict);
		}

		return sb.toString();
	}

	/**
	 * @param message
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MappingException(String message) {
		super(message);
	}

	/**
	 * Creates a redundant annotation mapping exception
	 * 
	 * @param member
	 * @param annotations
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MappingException(String member, List<Annotation> annotations) {
		super(MappingException.createMessage(member, annotations));
	}

	/**
	 * Creates a conflicting annotation mapping exception
	 * 
	 * @param member
	 * @param conflicts
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MappingException(String member, Set<Pair<Class<? extends Annotation>>> conflicts) {
		super(MappingException.createMessage(member, conflicts));
	}

	/**
	 * Creates a generic {@link MappingException}
	 * 
	 * @param message
	 * @param e
	 *            the cause
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MappingException(String message, Throwable e) {
		super(message, e);
	}
}
