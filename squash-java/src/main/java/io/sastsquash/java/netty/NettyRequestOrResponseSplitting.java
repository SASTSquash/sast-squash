/*
 * Copyright 2023 LightShield.
 * <p>
 * All Rights Reserved.
 */
package io.sastsquash.java.netty;

import io.sastsquash.java.util.Literals;
import io.sastsquash.java.visitor.DeleteMethodArgumentVisitor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.analysis.InvocationMatcher;
import org.openrewrite.analysis.search.UsesInvocation;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.J;

import java.util.Collections;
import java.util.Set;

public class NettyRequestOrResponseSplitting extends Recipe {
    private static final InvocationMatcher DEFAULT_HTTP_HEADERS = InvocationMatcher.fromMethodMatcher(
            "io.netty.handler.codec.http.DefaultHttpHeaders <constructor>(boolean)"
    );
    private static final InvocationMatcher COMBINED_HTTP_HEADERS = InvocationMatcher.fromMethodMatcher(
            "io.netty.handler.codec.http.CombinedHttpHeaders <constructor>(boolean)"
    );
    private static final InvocationMatcher DEFAULT_HTTP_REQUEST = InvocationMatcher.fromMethodMatcher(
            "io.netty.handler.codec.http.DefaultHttpRequest <constructor>(io.netty.handler.codec.http.HttpVersion, io.netty.handler.codec.http.HttpMethod, String, boolean)"
    );
    private static final InvocationMatcher DEFAULT_HTTP_RESPONSE = InvocationMatcher.fromMethodMatcher(
            "io.netty.handler.codec.http.DefaultHttpResponse <constructor>(io.netty.handler.codec.http.HttpVersion, io.netty.handler.codec.http.HttpResponseStatus, boolean)"
    );
    private static final InvocationMatcher DEFAULT_FULL_HTTP_REQUEST = InvocationMatcher.fromMethodMatchers(
            new MethodMatcher("io.netty.handler.codec.http.DefaultFullHttpRequest <constructor>(io.netty.handler.codec.http.HttpVersion, io.netty.handler.codec.http.HttpMethod, String, boolean)"),
            new MethodMatcher("io.netty.handler.codec.http.DefaultFullHttpRequest <constructor>(io.netty.handler.codec.http.HttpVersion, io.netty.handler.codec.http.HttpMethod, String, io.netty.buffer.ByteBuf, boolean)")

    );
    private static final InvocationMatcher DEFAULT_FULL_HTTP_RESPONSE = InvocationMatcher.fromMethodMatchers(
            new MethodMatcher("io.netty.handler.codec.http.DefaultFullHttpResponse <constructor>(io.netty.handler.codec.http.HttpVersion, io.netty.handler.codec.http.HttpResponseStatus, boolean)"),
            new MethodMatcher("io.netty.handler.codec.http.DefaultFullHttpResponse <constructor>(io.netty.handler.codec.http.HttpVersion, io.netty.handler.codec.http.HttpResponseStatus, io.netty.buffer.ByteBuf, boolean)"),
            new MethodMatcher("io.netty.handler.codec.http.DefaultFullHttpResponse <constructor>(io.netty.handler.codec.http.HttpVersion, io.netty.handler.codec.http.HttpResponseStatus, io.netty.buffer.ByteBuf, boolean, boolean)")
    );

    @Override
    public String getDisplayName() {
        return "Netty HTTP Request or Response Splitting (CRLF Injection)";
    }

    @Override
    public String getDescription() {
        return ".";
    }

    @Override
    public Set<String> getTags() {
        return Collections.singleton("CWE-113");
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(
                Preconditions.or(
                        new UsesInvocation<>(DEFAULT_HTTP_HEADERS),
                        new UsesInvocation<>(COMBINED_HTTP_HEADERS),
                        new UsesInvocation<>(DEFAULT_HTTP_REQUEST),
                        new UsesInvocation<>(DEFAULT_HTTP_RESPONSE),
                        new UsesInvocation<>(DEFAULT_FULL_HTTP_REQUEST),
                        new UsesInvocation<>(DEFAULT_FULL_HTTP_RESPONSE)
                ),
                new NettyRequestSplittingVisitor()
        );
    }

    private static class NettyRequestSplittingVisitor extends JavaIsoVisitor<ExecutionContext> {

        @Override
        public J.Literal visitLiteral(J.Literal literal, ExecutionContext executionContext) {
            if (Literals.isLiteral(false, literal) &&
                (DEFAULT_HTTP_HEADERS.advanced().isArgument(getCursor(), 0) ||
                 COMBINED_HTTP_HEADERS.advanced().isArgument(getCursor(), 0) ||
                 DEFAULT_HTTP_REQUEST.advanced().isArgument(getCursor(), 3) ||
                 DEFAULT_HTTP_RESPONSE.advanced().isArgument(getCursor(), 2) ||
                 DEFAULT_FULL_HTTP_REQUEST.advanced().isArgument(getCursor(), 3) ||
                 DEFAULT_FULL_HTTP_REQUEST.advanced().isArgument(getCursor(), 4) ||
                 DEFAULT_FULL_HTTP_RESPONSE.advanced().isArgument(getCursor(), 2) ||
                 DEFAULT_FULL_HTTP_RESPONSE.advanced().isArgument(getCursor(), 3))) {
                doAfterVisit(new DeleteMethodArgumentVisitor(literal));
                return literal;
            }
            return literal;
        }
    }
}
