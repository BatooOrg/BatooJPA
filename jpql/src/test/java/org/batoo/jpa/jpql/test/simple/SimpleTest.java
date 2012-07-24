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
package org.batoo.jpa.jpql.test.simple;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.batoo.jpa.core.util.BatooUtils;
import org.batoo.jpa.jpql.JpqlLexer;
import org.batoo.jpa.jpql.JpqlParser;
import org.batoo.jpa.jpql.JpqlParser.ql_statement_return;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class SimpleTest {

	private CommonTree parse(String filename) {
		try {
			final String jpql = this.read(filename);

			final JpqlLexer lexer = new JpqlLexer(new ANTLRStringStream(jpql));
			final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
			final JpqlParser parser = new JpqlParser(tokenStream);

			final ql_statement_return ql_statement = parser.ql_statement();

			return (CommonTree) ql_statement.getTree();
		}
		catch (final Exception e) {
			throw new RuntimeException("Cannot parse input: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private String print(CommonTree tree) {
		final StringBuilder builder = new StringBuilder();
		if (tree.getText() != null) {
			builder.append(" ").append(tree.getText());
		}
		else {
			builder.append("->");
		}

		final List<CommonTree> children = tree.getChildren();
		if (children != null) {
			builder.append("\n");
			builder.append(Joiner.on("\n").join(Lists.transform(children, new Function<CommonTree, String>() {

				@Override
				public String apply(CommonTree input) {
					return BatooUtils.tree(SimpleTest.this.print(input));
				}
			})));
		}

		return builder.toString();
	}

	private String read(String filename) throws Exception {
		final StringBuffer fileData = new StringBuffer(1000);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(filename)));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			final String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();

		return fileData.toString();
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testTestSimpleSelect() {
		final CommonTree tree = this.parse("first.jpql");
		System.out.println(this.print(tree));
	}
}
