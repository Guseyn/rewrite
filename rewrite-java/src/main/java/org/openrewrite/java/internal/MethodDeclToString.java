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
package org.openrewrite.java.internal;

import org.openrewrite.java.tree.J;

public class MethodDeclToString {
    public static String toString(J.MethodDecl method) {
        return METHOD_PRINTER.visit(method);
    }

    private static final PrintJava METHOD_PRINTER = new PrintJava() {
        @Override
        public String visitMethod(J.MethodDecl method) {
            String modifiers = visitModifiers(method.getModifiers()).trim();
            String params = "(" + visit(method.getParams().getParams(), ",") + ")";
            String thrown = method.getThrows() == null ? "" :
                    "throws" + visit(method.getThrows().getExceptions(), ",");

            return (modifiers.isEmpty() ? "" : modifiers + " ") +
                    (method.getTypeParameters() == null ? "" : method.getTypeParameters() + " ") +
                    (method.getReturnTypeExpr() == null ? "" : method.getReturnTypeExpr().printTrimmed() + " ") +
                    method.getName().printTrimmed() +
                    params +
                    thrown;
        }
    };
}
