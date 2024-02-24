package io.sastsquash.java.ssl;

import io.sastsquash.java.ssl.UpgradeSSLContextTLSVersion;
import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class UpgradeSSLContextTLSVersionTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new UpgradeSSLContextTLSVersion());
    }

    @DocumentExample
    @Test
    void upgradeSSLContextTLSVersion() {
        rewriteRun(
          java(
            """
              import javax.net.ssl.SSLContext;
                            
              class Test {
                  SSLContext sslContext1 = SSLContext.getInstance("TLSv1");
                  SSLContext sslContext11 = SSLContext.getInstance("TLSv1.1");
              }
              """,
            """
              import javax.net.ssl.SSLContext;
                              
              class Test {
                  SSLContext sslContext1 = SSLContext.getInstance("TLSv1.2");
                  SSLContext sslContext11 = SSLContext.getInstance("TLSv1.2");
              }
              """
          )
        );
    }

    @Test
    void upgradeSSLContextTLSVersionFromStatic() {
        rewriteRun(
          java(
            """
              import javax.net.ssl.SSLContext;
                            
              class Test {
                  private static final String TLSV1 = "TLSv1";
                  private static final String TLSV11 = "TLSv1.1";
              
                  SSLContext sslContext1 = SSLContext.getInstance(TLSV1);
                  SSLContext sslContext11 = SSLContext.getInstance(TLSV11);
              }
              """,
            """
              import javax.net.ssl.SSLContext;
                              
              class Test {
                  private static final String TLSV1 = "TLSv1";
                  private static final String TLSV11 = "TLSv1.1";
              
                  SSLContext sslContext1 = SSLContext.getInstance("TLSv1.2");
                  SSLContext sslContext11 = SSLContext.getInstance("TLSv1.2");
              }
              """
          )
        );
    }

    @Test
    void nonVulnerableUsageIsNotFixed() {
        rewriteRun(
          java(
            """
              import javax.net.ssl.SSLContext;
                            
              class Test {
                  private static final String TLSV1 = "TLSv1";
                  private static final String TLSV11 = "TLSv1.1";
              
                  {
                      useString(TLSV1);
                      useString(TLSV11);
                  }
                  
                  private static void useString(String s) {
                      // do nothing
                  }
              }
              """
          )
        );
    }
}
