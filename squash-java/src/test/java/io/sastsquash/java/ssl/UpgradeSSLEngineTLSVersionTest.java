package io.sastsquash.java.ssl;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class UpgradeSSLEngineTLSVersionTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new UpgradeSSLEngineTLSVersion());
    }

    @DocumentExample
    @Test
    void upgradeSSLEngineTLSVersion() {
        rewriteRun(
          java(
            """
              import javax.net.ssl.SSLEngine;
                            
              class Test {
                  Test(SSLEngine sslEngine) {
                      sslEngine.setEnabledProtocols(new String[]{"TLSv1"});
                      sslEngine.setEnabledProtocols(new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"});
                  }
              }
              """,
            """
              import javax.net.ssl.SSLEngine;
                              
              class Test {
                  Test(SSLEngine sslEngine) {
                      sslEngine.setEnabledProtocols(new String[]{"TLSv1.2"});
                      sslEngine.setEnabledProtocols(new String[]{"TLSv1.2"});
                  }
              }
              """
          )
        );
    }

    @Test
    void upgradeSSLEngineTLSVersionInConstantArray() {
        rewriteRun(
          java(
            """
              import javax.net.ssl.SSLEngine;
              
              class Test {
                  private static final String[] ONLY_TLSV1 = new String[] {"TLSv1"};
                  private static final String[] ONLY_TLSV12 = new String[] {"TLSv1.2"};
              
                  Test(SSLEngine sslEngine) {
                      sslEngine.setEnabledProtocols(ONLY_TLSV1);
                      sslEngine.setEnabledProtocols(ONLY_TLSV12);
                  }
              }
              """,
            """
              import javax.net.ssl.SSLEngine;
              
              class Test {
                  private static final String[] ONLY_TLSV12 = new String[] {"TLSv1.2"};
               
                  Test(SSLEngine sslEngine) {
                      sslEngine.setEnabledProtocols(new String[]{"TLSv1.2"});
                      sslEngine.setEnabledProtocols(ONLY_TLSV12);
                  }
              }
              """
          )
        );
    }
}
