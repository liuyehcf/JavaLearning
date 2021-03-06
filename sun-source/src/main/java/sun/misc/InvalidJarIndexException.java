/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.misc;

/**
 * Thrown if the URLClassLoader finds the INDEX.LIST file of
 * a jar file contains incorrect information.
 *
 * @author Zhenghua Li
 * @version %I%, %G%
 * @since 1.3
 */

public class InvalidJarIndexException extends RuntimeException {

    /**
     * Constructs an <code>InvalidJarIndexException</code> with no
     * detail message.
     */
    public InvalidJarIndexException() {
        super();
    }

    /**
     * Constructs an <code>InvalidJarIndexException</code> with the
     * specified detail message.
     *
     * @param s the detail message.
     */
    public InvalidJarIndexException(String s) {
        super(s);
    }
}
