/*
 * Copyright 2023 LightShield.
 * <p>
 * All Rights Reserved.
 */
package io.sastsquash.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class RelativePathCommandTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RelativePathCommand(null));
    }

    @Test
    void processBuilder() {
        // This case has other vulnerabilities. It's not great practice to use bash -c with user input.
        rewriteRun(
          java("""
              import java.io.IOException;
              import java.lang.ProcessBuilder;
                            
              class Test {
                  private static void runCmd(String cmd) throws IOException {
                      final ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                      final Process p = pb.start();
                  }
              }
              """,
            """
              import java.io.IOException;
              import java.lang.ProcessBuilder;
                            
              class Test {
                  private static void runCmd(String cmd) throws IOException {
                      final ProcessBuilder pb = new ProcessBuilder("/usr/bin/bash", "-c", cmd);
                      final Process p = pb.start();
                  }
              }
              """
          )
        );
    }

    @Test
    void antExecute() {
        rewriteRun(
          spec -> spec.parser(JavaParser.fromJavaVersion().classpath("ant")),
          java(
            """
              import org.apache.tools.ant.taskdefs.Execute;
                          
              class Test {
                  private static void runCmd(String cmd) {
                      Execute.runCommand(null, "rm", null);
                  }
              }
              """,
            """
              import org.apache.tools.ant.taskdefs.Execute;
                          
              class Test {
                  private static void runCmd(String cmd) {
                      Execute.runCommand(null, "/usr/bin/rm", null);
                  }
              }
              """
          )
        );
    }

    @Test
    void runtime() {
        rewriteRun(
          java(
            """
            class Test {
                private static void runRm() {
                    Runtime.getRuntime().exec("rm");
                }
            }
            """,
            """
            class Test {
                private static void runRm() {
                    Runtime.getRuntime().exec("/usr/bin/rm");
                }
            }
            """
          )
        );
    }
}
