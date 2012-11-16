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
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.jpql.JpqlLexer;
import org.batoo.jpa.jpql.JpqlParser;
import org.batoo.jpa.jpql.JpqlParser.ql_statement_return;
import org.batoo.jpa.jpql.test.sql.CommonTreePrinter;
import org.junit.Test;

import com.google.common.base.Joiner;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SimpleTest {

	private static final BLogger LOG = BLoggerFactory.getLogger(SimpleTest.class);

	private CommonTree parse(String filename) {
		try {
			final String jpql = this.read(filename);

			final JpqlLexer lexer = new JpqlLexer(new ANTLRStringStream(jpql));
			final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
			final JpqlParser parser = new JpqlParser(tokenStream);

			final ql_statement_return ql_statement = parser.ql_statement();

			final List<String> errors = parser.getErrors();
			if (errors.size() > 0) {
				final String errorMsg = Joiner.on("\n\t").join(errors);

				SimpleTest.LOG.error("Cannot parse query: {0}", //
					SimpleTest.LOG.boxed(jpql, //
						new Object[] { "\n\t" + errorMsg, "\n\n" + ((CommonTree) ql_statement.getTree()).toStringTree() + "\n" }));

				throw new RuntimeException("Cannot parse the query:\n " + errorMsg + ".\n" + jpql);
			}

			return (CommonTree) ql_statement.getTree();
		}
		catch (final Exception e) {
			throw new RuntimeException("Cannot parse input: " + e.getMessage(), e);
		}
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
	 * @since 2.0.0
	 */
	@Test
	public void testTestSimpleSelect() {
		SimpleTest.LOG.debug(CommonTreePrinter.toString(this.parse("first.jpql")));
	}
}
