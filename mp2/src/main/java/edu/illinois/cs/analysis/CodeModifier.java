package edu.illinois.cs.analysis;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CodeModifier extends VoidVisitorAdapter
{

	// Add a new method that check all the null constraints
	public boolean checkConstraints(BinaryExpr n) {
		if (!(n.getLeft().toString().equals("null") || n.getRight().toString().equals("null")))
			return false;
		if (!(n.getOperator() == BinaryExpr.Operator.NOT_EQUALS || n.getOperator() == BinaryExpr.Operator.EQUALS))
			return false;
		return true;
	}

	/**
	 * This visit function will be automatically applied to all binary
	 * expressions in the given Java file
	 */
	@Override
	public void visit(BinaryExpr n, Object arg) {
		super.visit(n, arg);
		// TODO: complete this visit function to automatically switch the two
		// operands for all null checks specified in the homework assignment
		if(checkConstraints(n)) {
			Expression left = n.getLeft();
			Expression right = n.getRight();
			n.setLeft(right);
			n.setRight(left);
		}
	}

	// The following three visit functions are used to remove all comments to
	// facilicate the comparison with the expected version; please do not change
	@Override
	public void visit(LineComment n, Object arg) {
		n.remove();
	}

	@Override
	public void visit(BlockComment n, Object arg) {
		n.remove();
	}

	@Override
	public void visit(JavadocComment n, Object arg) {
		n.remove();
	}

}
