/*
 * Copyright 2023 LightShield.
 * <p>
 * All Rights Reserved.
 */
package io.sastsquash.java;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.*;
import org.openrewrite.analysis.InvocationMatcher;
import org.openrewrite.analysis.search.UsesInvocation;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.ChangeLiteral;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TypeUtils;

import java.util.Collections;
import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = true)
public class RelativePathCommand extends Recipe {

    @Option(
            displayName = "Base Path",
            description = "The base path to use for relative paths. If not provided, `/usr/bin` is used.",
            example = "/usr/bin",
            required = false
    )
    @Nullable
    String basePath;

    @Override
    public String getDisplayName() {
        return "Relative Path Command";
    }

    @Override
    public String getDescription() {
        return "Executing a command with a relative path\n" +
               "When a command is executed with a relative path, the runtime uses the PATH environment variable to find which executable to run. " +
               "Therefore, any user who can change the PATH environment variable can cause the software to run a different, malicious executable.";
    }

    @Override
    public Set<String> getTags() {
        return Collections.singleton("CWE-428");
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        String basePath = this.basePath == null ? "/usr/bin" : this.basePath;
        return Preconditions.check(
                Preconditions.or(
                        new UsesInvocation<>(RelativePathCommandVisitor.PROCESS_BUILDER),
                        new UsesInvocation<>(RelativePathCommandVisitor.RUNTIME_EXEC),
                        new UsesInvocation<>(RelativePathCommandVisitor.ANT_EXECUTE)
                ),
                new RelativePathCommandVisitor(basePath)
        );
    }


    @AllArgsConstructor
    static class RelativePathCommandVisitor extends JavaIsoVisitor<ExecutionContext> {
        static final InvocationMatcher PROCESS_BUILDER = InvocationMatcher.fromMethodMatcher(
                "java.lang.ProcessBuilder <constructor>(..)"
        );

        static final InvocationMatcher RUNTIME_EXEC = InvocationMatcher.fromMethodMatcher(
                "java.lang.Runtime exec(..)"
        );
        static final InvocationMatcher ANT_EXECUTE = InvocationMatcher.fromMethodMatcher(
                "org.apache.tools.ant.taskdefs.Execute runCommand(..)"
        );

        private final String basePath;


        @Override
        public J.Literal visitLiteral(J.Literal literal, ExecutionContext executionContext) {
            if ((PROCESS_BUILDER.advanced().isFirstArgument(getCursor()) ||
                 RUNTIME_EXEC.advanced().isFirstArgument(getCursor()) ||
                 ANT_EXECUTE.advanced().isArgument(getCursor(), 1))
                && TypeUtils.isString(literal.getType())) {
                String value = (String) literal.getValue();
                assert value != null : "value is null";
                if (!value.startsWith("/")) {
                    doAfterVisit(new ChangeLiteral<>(literal, old -> basePath + "/" + value));
                }
            }
            return super.visitLiteral(literal, executionContext);
        }
    }
}
