/*
 * Copyright (c) 2014, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package edu.jvm.runtime.safepoint;

/**
 * @author Ivan Mamontov
 */
public class Test {

    static int[] array = new int[1024];

    public void hot(int i) {
        int ii = (i + 10 * 100) % array.length;
        int jj = (ii + i / 33) % array.length;
        if (ii < 0) ii = -ii;
        if (jj < 0) jj = -jj;
        array[ii] = array[jj] + 1;
    }

    public void cold() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            hot((int) i);
        }
    }

    public static void main(String[] args) {
        Test test = new Test();
        for (int i = 0; i < 500; i++) {
            test.cold();
        }
    }
}
