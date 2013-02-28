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

package uk.org.lidalia.sysoutslf4j.context;

import org.junit.Test;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static uk.org.lidalia.test.Assert.isNotInstantiable;
import static uk.org.lidalia.test.ShouldThrow.shouldThrow;

public class StringUtilsTests extends SysOutOverSLF4JTestCase {

    @Test
    public void stripEndStripsEnd() {
        assertEquals("hello wo", StringUtils.stripEnd("hello world", "elders"));
    }

    @Test
    public void stripEndReturnsEmptyStringIfEmptyStringPassedIn() {
        assertEquals("", StringUtils.stripEnd("", "irrelevant"));
    }

    @Test
    public void stripEndReturnsInputIfEmptyStripCharsPassedIn() {
        assertEquals("hello", StringUtils.stripEnd("hello", ""));
    }

    @Test
    public void stripEndThrowsNullPointerExceptionIfInputIsNull() {
        shouldThrow(NullPointerException.class, new Runnable() {
            public void run() {
                StringUtils.stripEnd(null, "irrelevant");
            }
        });
    }

    @Test
    public void stripEndThrowsNullPointerExceptionIfStripCharsIsNull() {
        shouldThrow(NullPointerException.class, new Runnable() {
            public void run() {
                StringUtils.stripEnd("irrelevant", null);
            }
        });
    }

    @Test
    public void notInstantiable() {
        assertThat(StringUtils.class, isNotInstantiable());
    }
}
