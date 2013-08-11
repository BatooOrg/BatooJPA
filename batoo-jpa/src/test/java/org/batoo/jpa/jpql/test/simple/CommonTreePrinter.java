/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

import org.antlr.runtime.tree.Tree;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CommonTreePrinter {

	private static void print(StringBuilder builder, Tree node, String prefix, boolean isTail) {
		final String name = node.getText() == null ? "" : node.getText().replaceAll("\r\n|\r|\n", "\u23CE");

		builder.append(prefix).append(isTail ? "└─ " : "├─ ") //
		.append(String.format("%04d", node.getLine())) //
		.append(':').append(String.format("%04d", node.getCharPositionInLine())).append(' ') //
		.append(String.format("%02d", node.getType())).append(": ").append(name) //
		.append("\n");

		for (int i = 0; i < (node.getChildCount() - 1); i++) {
			CommonTreePrinter.print(builder, node.getChild(i), prefix + (isTail ? "    " : "│   "), false);
		}

		if (node.getChildCount() >= 1) {
			CommonTreePrinter.print(builder, node.getChild(node.getChildCount() - 1), prefix + (isTail ? "    " : "│   "), true);
		}
	}

	/**
	 * Returns the string representation of a common tree.
	 * 
	 * @param root
	 *            the root of the tree
	 * @return the string representation of a common tree
	 * 
	 * @since 2.0.0
	 */
	public static String toString(Tree root) {
		final StringBuilder builder = new StringBuilder();

		CommonTreePrinter.print(builder, root, "", true);

		return builder.toString();
	}
}
