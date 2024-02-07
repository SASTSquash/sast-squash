package io.sastsquash.java;

import org.openrewrite.Recipe;

public class StackTraceExposure extends Recipe {
    @Override
    public String getDisplayName() {
        return "Stack Trace Exposure";
    }

    @Override
    public String getDescription() {
        return "Software developers often add stack traces to error messages, as a debugging aid. " +
               "Whenever that error message occurs for an end user, the developer can use the stack trace to help identify how to fix the problem. " +
               "In particular, stack traces can tell the developer more about the sequence of events that led to a failure, as opposed to merely the final state of the software when the error occurred.\n" +
               "\n" +
               "Unfortunately, the same information can be useful to an attacker. " +
               "The sequence of class names in a stack trace can reveal the structure of the application as well as any internal components it relies on. " +
               "Furthermore, the error message at the top of a stack trace can include information such as server-side file names and SQL code that the application relies on, allowing an attacker to fine-tune a subsequent injection attack.\n";
    }
}
