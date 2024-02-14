/*
 * Copyright 2023 LightShield.
 * <p>
 * All Rights Reserved.
 */
package io.sastsquash.java.visitor;

import lombok.AllArgsConstructor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.*;
import org.openrewrite.marker.Markers;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.openrewrite.Tree.randomId;

@AllArgsConstructor
public class DeleteMethodArgumentVisitor extends JavaIsoVisitor<ExecutionContext> {
    private final Expression methodArgument;

    @Override
    public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
        J.MethodInvocation m = super.visitMethodInvocation(method, ctx);
        return (J.MethodInvocation) visitMethodCall(m);
    }

    @Override
    public J.NewClass visitNewClass(J.NewClass newClass, ExecutionContext ctx) {
        J.NewClass n = super.visitNewClass(newClass, ctx);
        return (J.NewClass) visitMethodCall(n);
    }

    private MethodCall visitMethodCall(MethodCall methodCall) {
        MethodCall m = methodCall;
        List<Expression> originalArgs = m.getArguments();
        if (!originalArgs.contains(methodArgument)) {
            return m;
        }
        int argumentIndex = originalArgs.indexOf(methodArgument);
        if (originalArgs.stream().filter(a -> !(a instanceof J.Empty)).count() >= argumentIndex + 1) {
            List<Expression> args = new ArrayList<>(originalArgs);

            args.remove(argumentIndex);
            if (args.isEmpty()) {
                args = singletonList(new J.Empty(randomId(), Space.EMPTY, Markers.EMPTY));
            }

            m = m.withArguments(args);

            JavaType.Method methodType = m.getMethodType();
            if (methodType != null) {
                List<String> parameterNames = new ArrayList<>(methodType.getParameterNames());
                parameterNames.remove(argumentIndex);
                List<JavaType> parameterTypes = new ArrayList<>(methodType.getParameterTypes());
                parameterTypes.remove(argumentIndex);

                m = m.withMethodType(methodType
                        .withParameterNames(parameterNames)
                        .withParameterTypes(parameterTypes));
                if (m instanceof J.MethodInvocation && ((J.MethodInvocation) m).getName().getType() != null) {
                    m = ((J.MethodInvocation) m).withName(((J.MethodInvocation) m).getName().withType(m.getMethodType()));
                }
            }
        }
        return m;
    }
}
