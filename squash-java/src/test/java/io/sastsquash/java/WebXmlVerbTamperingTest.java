package io.sastsquash.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.xml.Assertions.xml;

public class WebXmlVerbTamperingTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new WebXmlVerbTampering());
    }

    @Test
    @DocumentExample
    void webXmlVerbTampering() {
        rewriteRun(
          xml(
            """
              <servlet>
                  <security-constraint>
                      <web-resource-collection>
                          <url-pattern>/vulnerable/*</url-pattern>
                          <http-method>GET</http-method>
                          <http-method>PUT</http-method>
                      </web-resource-collection>
                      <auth-constraint>
                          <role-name>administrator</role-name>
                      </auth-constraint>
                  </security-constraint>
                  <security-constraint>
                      <web-resource-collection>
                          <url-pattern>/safe/*</url-pattern>
                      </web-resource-collection>
                      <auth-constraint>
                          <role-name>administrator</role-name>
                      </auth-constraint>
                  </security-constraint>
              </servlet>
              """,
            """
              <servlet>
                  <security-constraint>
                      <web-resource-collection>
                          <url-pattern>/vulnerable/*</url-pattern>
                      </web-resource-collection>
                      <auth-constraint>
                          <role-name>administrator</role-name>
                      </auth-constraint>
                  </security-constraint>
                  <security-constraint>
                      <web-resource-collection>
                          <url-pattern>/safe/*</url-pattern>
                      </web-resource-collection>
                      <auth-constraint>
                          <role-name>administrator</role-name>
                      </auth-constraint>
                  </security-constraint>
              </servlet>
              """,
            spec -> spec.path("src/main/resources/WEB-INF/web.xml")
          )
        );
    }
}
