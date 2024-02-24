package io.sastsquash.java.ssl;

import fj.data.Option;
import org.openrewrite.*;
import org.openrewrite.analysis.InvocationMatcher;
import org.openrewrite.analysis.constantfold.ConstantFold;
import org.openrewrite.analysis.dataflow.DataFlowNode;
import org.openrewrite.analysis.search.UsesInvocation;
import org.openrewrite.analysis.trait.expr.Expr;
import org.openrewrite.analysis.trait.expr.Literal;
import org.openrewrite.analysis.trait.expr.VarAccess;
import org.openrewrite.analysis.trait.variable.Variable;
import org.openrewrite.java.ChangeLiteral;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.Space;
import org.openrewrite.marker.Markers;

import java.util.Collections;

import static io.sastsquash.java.ssl.SSLConstants.*;

public class UpgradeSSLContextTLSVersion extends Recipe {
    @Override
    public String getDisplayName() {
        return "Upgrade SSLContext TLS version";
    }

    @Override
    public String getDescription() {
        return "This change ensures that `SSLContext#getInstance()` uses a safe version of Transport Layer Security (TLS), which is necessary for safe SSL connections.\n" +
               "\n" +
               "`TLS v1.0` and `TLS v1.1` both have serious issues and are considered unsafe. Right now, the only safe version to use is `1.2`.\n" +
               "\n" +
               "This change involves modifying the arguments to `getInstance()` to return `TLSv1.2` when it can be confirmed to be another, less secure value.\n" +
               "\n" +
               "References:\n" +
               " - [Datatracker IETF: Deprecating TLS 1.0 and TLS 1.1](https://datatracker.ietf.org/doc/rfc8996/)\n" +
               " - [Digicert: Deprecating TLS 1.0 and TLS 1.1](https://www.digicert.com/blog/depreciating-tls-1-0-and-1-1)";
    }


    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(
                new UsesInvocation<>(UpgradeSSLContextTLSVersionVisitor.SSL_CONTEXT_GET_INSTANCE),
                new UpgradeSSLContextTLSVersionVisitor<>()
        );
    }

    private static class UpgradeSSLContextTLSVersionVisitor<P> extends JavaVisitor<P> {
        private static final InvocationMatcher SSL_CONTEXT_GET_INSTANCE =
                InvocationMatcher.fromMethodMatcher("javax.net.ssl.SSLContext getInstance(String)");

        @Override
        public J visitLiteral(J.Literal literal, P p) {
            if (SSL_CONTEXT_GET_INSTANCE.advanced().isFirstArgument(getCursor())) {
                //noinspection SuspiciousMethodCalls
                if (OUTDATED_TLS_VERSIONS.contains(literal.getValue())) {
                    doAfterVisit(new ChangeLiteral<>(literal, old -> "TLSv1.2"));
                }
            }
            return super.visitLiteral(literal, p);
        }

        @Override
        public J visitIdentifier(J.Identifier identifier, P p) {
            if (SSL_CONTEXT_GET_INSTANCE.advanced().isFirstArgument(getCursor())) {
                Option<String> staticValue =
                        ConstantFold.findConstantLiteralValue(getCursor(), String.class);

                if (staticValue.exists(OUTDATED_TLS_VERSIONS::contains)) {
                    return autoFormat(
                            createTLSVersion12StringLiteral(),
                            p
                    );
                }
                return super.visitIdentifier(identifier, p);
            }
            return super.visitIdentifier(identifier, p);
        }
    }
}
