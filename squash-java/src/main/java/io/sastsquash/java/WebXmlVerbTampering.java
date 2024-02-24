package io.sastsquash.java;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.xml.RemoveXmlTag;

public class WebXmlVerbTampering extends Recipe {
    @Override
    public String getDisplayName() {
        return "Web XML Verb Tampering";
    }

    @Override
    public String getDescription() {
        return "This recipe detects and fixes verb tampering in web.xml. " +
               "Verb tampering is a security vulnerability that allows attackers to bypass HTTP method restrictions.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new RemoveXmlTag(
                "//web-resource-collection/http-method",
                "**/web.xml"
        ).getVisitor();
    }
}
