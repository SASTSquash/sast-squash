package io.sastsquash.java.ssl;

import fj.data.Option;
import org.openrewrite.Cursor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.analysis.InvocationMatcher;
import org.openrewrite.analysis.dataflow.DataFlowNode;
import org.openrewrite.analysis.trait.expr.Expr;
import org.openrewrite.analysis.trait.expr.VarAccess;
import org.openrewrite.analysis.trait.variable.Variable;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;

import static io.sastsquash.java.ssl.SSLConstants.OUTDATED_TLS_VERSIONS;
import static io.sastsquash.java.ssl.SSLConstants.createTLSVersion12StringLiteral;

public class UpgradeSSLEngineTLSVersion extends Recipe {
    @Override
    public String getDisplayName() {
        return "Upgrade SSL Engine TLS Version";
    }

    @Override
    public String getDescription() {
        return "This change ensures that `SSLEngine#setEnabledProtocols()` retrieves a safe version of Transport Layer Security (TLS), which is necessary for safe SSL connections.\n" +
               "\n" +
               "TLS v1.0 and TLS v1.1 both have serious issues and are considered unsafe. Right now, the only safe version to use is 1.2.\n" +
               "\n" +
               "Our change involves modifying the arguments to `setEnabledProtocols()` to return TLSv1.2 when it can be confirmed to be another, less secure value.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new UpgradeSSLEngineTLSVersionVisitor<>();
    }

    static class UpgradeSSLEngineTLSVersionVisitor<P> extends JavaVisitor<P> {
        private static final InvocationMatcher SSLENGINE_SETENABLEDPROTOCOLS = InvocationMatcher.fromMethodMatcher(
                "javax.net.ssl.SSLEngine setEnabledProtocols(..)"
        );

        @Override
        public J visitExpression(Expression expression, P p) {
            if (SSLENGINE_SETENABLEDPROTOCOLS.advanced().isFirstArgument(getCursor())) {
                if (expression.unwrap() instanceof J.NewArray) {
                    return maybeAutoFormat(
                            expression,
                            cleanUpOutdatedTLSVersions((J.NewArray) expression.unwrap()),
                            p
                    );
                }
            }
            return super.visitExpression(expression, p);
        }

        public J.NewArray cleanUpOutdatedTLSVersions(J.NewArray array) {
            J.NewArray modified = array.withInitializer(
                    ListUtils.map(array.getInitializer(), (Expression v) -> {
                        if (v instanceof J.Literal) {
                            String value = (String) ((J.Literal) v).getValue();
                            if (OUTDATED_TLS_VERSIONS.contains(value)) {
                                return null;
                            }
                        }
                        return v;
                    })
            );

            if (modified.getInitializer() == null || modified.getInitializer().isEmpty()) {
                return modified.withInitializer(
                        ListUtils.concat(
                                modified.getInitializer(),
                                createTLSVersion12StringLiteral()
                        )
                );
            }
            return modified;
        }
    }
}
