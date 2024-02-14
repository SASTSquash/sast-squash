/*
 * Copyright 2023 LightShield.
 * <p>
 * All Rights Reserved.
 */
package io.sastsquash.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class HardenedLDAPTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new HardenedLDAP());
    }

    @Test
    void testSearchControlsFixed() {
        rewriteRun(
          java(
            """
              import javax.naming.directory.SearchControls;
              class Test {
                public static final SearchControls searchControls = new SearchControls(0, 0, 0, null, true, false);
              }
              """,
            """
              import javax.naming.directory.SearchControls;
              class Test {
                public static final SearchControls searchControls = new SearchControls(0, 0, 0, null, false, false);
              }
              """
          )
        );
    }

    @Test
    void searchControlsNoArgConstructor() {
        rewriteRun(
          java(
            """
              import javax.naming.directory.SearchControls;
              class Test {
                public static final SearchControls searchControls = new SearchControls();
              }
              """
          )
        );
    }
}
