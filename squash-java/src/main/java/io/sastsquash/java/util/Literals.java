/*
 * Copyright 2023 LightShield.
 * <p>
 * All Rights Reserved.
 */
package io.sastsquash.java.util;

import lombok.NoArgsConstructor;
import org.openrewrite.java.tree.J;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Literals {

    public static boolean isLiteral(boolean b, J.Literal literal) {
        return literal.getValue() instanceof Boolean && literal.getValue().equals(b);
    }

}
