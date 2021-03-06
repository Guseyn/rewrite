/*
 * Copyright 2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java;

import org.openrewrite.Cursor;
import org.openrewrite.SourceVisitor;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.tree.*;

import java.util.concurrent.atomic.AtomicBoolean;

public interface JavaSourceVisitor<R> extends SourceVisitor<R> {

    default J.CompilationUnit enclosingCompilationUnit() {
        J.CompilationUnit cu = getCursor().firstEnclosing(J.CompilationUnit.class);
        if (cu == null) {
            throw new IllegalStateException("Expected to find a J.CompilationUnit in " + this);
        }
        return cu;
    }

    default J.Block<?> enclosingBlock() {
        return getCursor().firstEnclosing(J.Block.class);
    }

    @Nullable
    default J.MethodDecl enclosingMethod() {
        return getCursor().firstEnclosing(J.MethodDecl.class);
    }

    default J.ClassDecl enclosingClass() {
        return getCursor().firstEnclosing(J.ClassDecl.class);
    }

    default boolean isInSameNameScope(Cursor higher, Cursor lower) {
        AtomicBoolean takeWhile = new AtomicBoolean(true);
        return higher.getPathAsStream()
                .filter(t -> t instanceof J.Block ||
                        t instanceof J.MethodDecl ||
                        t instanceof J.Try ||
                        t instanceof J.ForLoop ||
                        t instanceof J.ForEachLoop).findAny()
                .map(higherNameScope -> lower.getPathAsStream()
                        .filter(t -> {
                            takeWhile.set(takeWhile.get() && (
                                    !(t instanceof J.ClassDecl) ||
                                            (((J.ClassDecl) t).getKind() instanceof J.ClassDecl.Kind.Class &&
                                                    !((J.ClassDecl) t).hasModifier("static"))));
                            return takeWhile.get();
                        })
                        .anyMatch(higherNameScope::equals))
                .orElse(false);
    }

    /**
     * @param lower The cursor of the lower scoped tree element to check.
     * @return Whether this cursor shares the same name scope as {@code scope}.
     */
    default boolean isInSameNameScope(Cursor lower) {
        return isInSameNameScope(getCursor(), lower);
    }

    default R visitExpression(Expression expr) {
        if (expr.getType() instanceof JavaType.FullyQualified) {
            JavaType.FullyQualified exprType = (JavaType.FullyQualified) expr.getType();
            if (expr instanceof J.FieldAccess) {
                if (((J.FieldAccess) expr).getSimpleName().equals(exprType.getClassName())) {
                    return reduce(defaultTo(expr), visitTypeName((NameTree) expr));
                }
            } else if (expr instanceof J.Ident) {
                if (((J.Ident) expr).getSimpleName().equals(exprType.getClassName())) {
                    return reduce(defaultTo(expr), visitTypeName((NameTree) expr));
                }
            }
        }
        return defaultTo(expr);
    }

    R visitStatement(Statement statement);
    R visitTypeName(NameTree name);
    R visitAnnotatedType(J.AnnotatedType annotatedType);
    R visitAnnotation(J.Annotation annotation);
    R visitArrayAccess(J.ArrayAccess arrayAccess);
    R visitArrayType(J.ArrayType arrayType);
    R visitAssert(J.Assert azzert);
    R visitAssign(J.Assign assign);
    R visitAssignOp(J.AssignOp assignOp);
    R visitBinary(J.Binary binary);
    R visitBlock(J.Block<J> block);
    R visitBreak(J.Break breakStatement);
    R visitCase(J.Case caze);
    R visitCatch(J.Try.Catch catzh);
    R visitClassDecl(J.ClassDecl classDecl);
    R visitCompilationUnit(J.CompilationUnit cu);
    R visitContinue(J.Continue continueStatement);
    R visitDoWhileLoop(J.DoWhileLoop doWhileLoop);
    R visitEmpty(J.Empty empty);
    R visitEnumValue(J.EnumValue enoom);
    R visitEnumValueSet(J.EnumValueSet enums);
    R visitFinally(J.Try.Finally finallie);
    R visitFieldAccess(J.FieldAccess fieldAccess);
    R visitForEachLoop(J.ForEachLoop forEachLoop);
    R visitForLoop(J.ForLoop forLoop);
    R visitIdentifier(J.Ident ident);
    R visitIf(J.If iff);
    R visitElse(J.If.Else elze);
    R visitImport(J.Import impoort);
    R visitInstanceOf(J.InstanceOf instanceOf);
    R visitLabel(J.Label label);
    R visitLambda(J.Lambda lambda);
    R visitLiteral(J.Literal literal);
    R visitMemberReference(J.MemberReference memberRef);
    R visitMethod(J.MethodDecl method);
    R visitMethodInvocation(J.MethodInvocation method);
    R visitMultiCatch(J.MultiCatch multiCatch);
    R visitMultiVariable(J.VariableDecls multiVariable);
    R visitNewArray(J.NewArray newArray);
    R visitNewClass(J.NewClass newClass);
    R visitPackage(J.Package pkg);
    R visitParameterizedType(J.ParameterizedType type);
    <T extends J> R visitParentheses(J.Parentheses<T> parens);
    R visitPrimitive(J.Primitive primitive);
    R visitReturn(J.Return retrn);
    R visitSwitch(J.Switch switzh);
    R visitSynchronized(J.Synchronized synch);
    R visitTernary(J.Ternary ternary);
    R visitThrow(J.Throw thrown);
    R visitTry(J.Try tryable);
    R visitTypeCast(J.TypeCast typeCast);
    R visitTypeParameter(J.TypeParameter typeParam);
    R visitTypeParameters(J.TypeParameters typeParams);
    R visitUnary(J.Unary unary);
    R visitUnparsedSource(J.UnparsedSource unparsed);
    R visitVariable(J.VariableDecls.NamedVar variable);
    R visitWhileLoop(J.WhileLoop whileLoop);
    R visitWildcard(J.Wildcard wildcard);
}
