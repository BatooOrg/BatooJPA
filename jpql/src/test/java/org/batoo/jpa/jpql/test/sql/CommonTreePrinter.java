package org.batoo.jpa.jpql.test.sql;

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
