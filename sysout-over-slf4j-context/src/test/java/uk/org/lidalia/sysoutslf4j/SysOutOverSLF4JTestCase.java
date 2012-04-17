package uk.org.lidalia.sysoutslf4j;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;

import uk.org.lidalia.testutils.SLF4JTestCase;

/*
 * Copyright (c) 2009-2012 Robert Elliot
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

public abstract class SysOutOverSLF4JTestCase extends SLF4JTestCase {

    protected ClassLoader originalContextClassLoader;
    protected PrintStream SYS_OUT;
    protected PrintStream SYS_ERR;

    @Before
    public void storeOriginalSystemOutAndErr() {
        SYS_OUT = System.out;
        SYS_ERR = System.err;
    }

    @After
    public void restoreOriginalSystemOutAndErr() {
        System.setOut(SYS_OUT);
        System.setErr(SYS_ERR);
    }

    @Before
    public void storeOriginalContextClassLoader() {
        originalContextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @After
    public void restoreOriginalContextClassLoader() {
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }
}
