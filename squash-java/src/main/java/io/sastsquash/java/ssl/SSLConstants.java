package io.sastsquash.java.ssl;

import lombok.NoArgsConstructor;
import org.openrewrite.Tree;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.Space;
import org.openrewrite.marker.Markers;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
final class SSLConstants {
    static final String TLS_VERSION1 = "TLSv1";
    static final String TLS_VERSION11 = "TLSv1.1";

    static final Set<String> OUTDATED_TLS_VERSIONS =
            new HashSet<>(Arrays.asList(TLS_VERSION1, TLS_VERSION11));

    static J.Literal createTLSVersion12StringLiteral() {
        return new J.Literal(
                Tree.randomId(),
                Space.EMPTY,
                Markers.EMPTY,
                "TLSv1.2",
                "\"TLSv1.2\"",
                Collections.emptyList(),
                JavaType.Primitive.String
        );
    }
}
