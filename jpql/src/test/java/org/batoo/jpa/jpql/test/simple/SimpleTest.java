/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.jpql.test.simple;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.batoo.common.util.BatooUtils;
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
