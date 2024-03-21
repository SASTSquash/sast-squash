/*
 * Copyright 2023 LightShield.
 * <p>
 * All Rights Reserved.
 */
package io.sastsquash.java;

import org.openrewrite.*;
import org.openrewrite.analysis.InvocationMatcher;
import org.openrewrite.analysis.search.UsesInvocation;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.security.PathTraversalGuardInsertionVisitor;

public class SanitizeApacheMultipartFilename extends Recipe {
    private static final InvocationMatcher APACHE_MULTIPART_FILENAME = InvocationMatcher.fromMethodMatchers(
            new MethodMatcher("org.apache.commons.fileupload.FileItem getName()", true),
            new MethodMatcher("org.apache.commons.fileupload2.core.FileItem getName()", true)
    );

    @Override
    public String getDisplayName() {
        return "Sanitize Apache Multipart Filename";
    }

    @Override
    public String getDescription() {
        return "This change guards Apache Common's popular multipart request and [file uploading library](https://commons.apache.org/proper/commons-fileupload/) and prevents arbitrary file upload attacks.\n" +
               "\n" +
               "Although end users uploading a file through the browser can't fully control the file name, attackers armed with HTTP proxies, scripts or `curl` can manipulate the file to contain directory escape sequences and " +
               "send in values like `../../../../../etc/passwd`. " +
               "This is a common place that developers forget to distrust user input and end up including the attacker's " +
               "file name in the path the process ends up writing to." +
               "\n" +
               "Additional Resources:\n" +
               " - [OWASP: Unrestricted File Upload](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload)\n" +
               " - [portswigger.net/web-security/file-upload](https://portswigger.net/web-security/file-upload)";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(new UsesInvocation<>(APACHE_MULTIPART_FILENAME),
                Repeat.repeatUntilStable(new PathTraversalGuardInsertionVisitor<>(
                        APACHE_MULTIPART_FILENAME,
                        "fileItem",
                        "Bad uploaded file name",
                        true
                ))
        );
    }
}
