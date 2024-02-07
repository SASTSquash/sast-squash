package io.sastsquash.java;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.analysis.InvocationMatcher;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;

public class HardenedLDAP extends Recipe {

    @Override
    public String getDisplayName() {
        return "Harden LDAP Calls Against Deserialization Attack";
    }

    @Override
    public String getDescription() {
        return "Using Java's deserialization APIs on untrusted data [is dangerous](https://cheatsheetseries.owasp.org/cheatsheets/Deserialization_Cheat_Sheet.html) because side effects from a type's reconstitution logic can be chained together to execute arbitrary code." +
               "This very serious and very common [bug class](https://github.com/GrrrDog/Java-Deserialization-Cheat-Sheet) has resulted in some high profile vulnerabilities, including the [log4shell vulnerability](https://en.wikipedia.org/wiki/Log4Shell) that rocked the development and security world (and is [_still_ present in organizations](https://www.wired.com/story/log4j-log4shell-one-year-later/).)\n" +
               "\n" +
               "The `DirContext#search(SearchControls)` API is used to send LDAP queries." +
               "If the `SearchControls` has the `retobj` set to `true`, the API will try to deserialize a piece of the response from the LDAP server with Java's deserialization API." +
               "This means that if the LDAP server could influenced to return malicious data (or is outright controlled by an attacker) then they could [execute arbitrary on the API client's JVM](https://www.blackhat.com/docs/us-16/materials/us-16-Munoz-A-Journey-From-JNDI-LDAP-Manipulation-To-RCE.pdf).";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new HardenedLDAPVisitor<>();
    }

    static class HardenedLDAPVisitor<P> extends JavaIsoVisitor<P> {
        private static final InvocationMatcher SEARCH_CONTROLS_CONSTRUCTOR = InvocationMatcher.fromMethodMatcher(
                "javax.naming.directory.SearchControls <constructor>(..)"
        );

        @Override
        public J.Literal visitLiteral(J.Literal literal, P p) {
            if (literal.getValue() instanceof Boolean && (Boolean) literal.getValue()) {
                if (SEARCH_CONTROLS_CONSTRUCTOR.advanced().isArgument(getCursor(), 4)) {
                    return literal.withValue(false).withValueSource("false");
                }
            }
            return literal;
        }
    }
}
