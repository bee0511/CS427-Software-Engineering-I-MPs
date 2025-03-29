package edu.illinois.cs.analysis;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CodeParser extends VoidVisitorAdapter
{
	int methNum = 0;

	// Add a new method that check all the constraints
	public boolean checkConstraints(MethodDeclaration n) {
		if (!n.getBody().isPresent())
			return false;
		if (n.getParameters().size() <= 0)
			return false;
		if (!n.getModifiers().toString().contains("public"))
			return false;
		if (n.getModifiers().toString().contains("static"))
			return false;
		if (n.getType().toString().equals("void"))
			return false;
		return true;
	}

	/**
	 * Following the visitor pattern design, this visit function will be
	 * automatically applied to all method declarations within the given
	 * compilation unit (i.e., Java files).
	 */
	@Override
	public void visit(MethodDeclaration n, Object arg) {
		super.visit(n, arg);
		
		// TODO: add your implementation here so that it counts the methods
		// satisfying the listed constriants rather than all possible methods
		if (checkConstraints(n)){
			methNum++;
		}
	}

}
