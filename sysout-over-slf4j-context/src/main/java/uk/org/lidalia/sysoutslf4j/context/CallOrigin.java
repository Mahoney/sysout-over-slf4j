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

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import uk.org.lidalia.sysoutslf4j.system.PerContextPrintStream;
import uk.org.lidalia.sysoutslf4j.system.PerContextSystemOutput;
import uk.org.lidalia.sysoutslf4j.system.SystemOutput;

import java.util.List;

import static java.util.Arrays.asList;

final class CallOrigin {

    private final boolean printingStackTrace;
    private final String className;
    private final boolean inLoggingSystem;

    private CallOrigin(final boolean isStacktrace, final boolean inLoggingSystem, final String className) {
        this.printingStackTrace = isStacktrace;
        this.className = className;
        this.inLoggingSystem = inLoggingSystem;
    }

    boolean isPrintingStackTrace() {
        return printingStackTrace;
    }

    String getClassName() {
        return className;
    }

    boolean isInLoggingSystem() {
        return inLoggingSystem;
    }

    static CallOrigin getCallOrigin(LoggingSystemRegister loggingSystemRegister) {
        Thread currentThread = Thread.currentThread();
        final List<StackTraceElement> stackTraceElements = asList(currentThread.getStackTrace());
        int firstPerContextPrintStreamFrame = frameWithPrintStreamClassName(stackTraceElements, 0).or(new Supplier<Integer>() {
            @Override
            public Integer get() {
                throw new IllegalStateException("Must be called from down stack of " + PerContextPrintStream.class.getName());
            }
        });
        int secondPerContextPrintStreamFrame = frameWithPrintStreamClassName(stackTraceElements, firstPerContextPrintStreamFrame + 1).or(stackTraceElements.size());

        final List<StackTraceElement> interestingStackTraceElements = stackTraceElements.subList(firstPerContextPrintStreamFrame, secondPerContextPrintStreamFrame);

        for (int i = interestingStackTraceElements.size() -1; i >= 0; i--) {
            StackTraceElement stackTraceElement = interestingStackTraceElements.get(i);
            String currentClassName = stackTraceElement.getClassName();
            if (currentClassName.equals(Throwable.class.getName()) && stackTraceElement.getMethodName().equals("printStackTrace")) {
                return new CallOrigin(true, false, getOuterClassName(interestingStackTraceElements.get(i + 1).getClassName()));
            }
            if (currentClassName.equals(PerContextPrintStream.class.getName())) {
                return new CallOrigin(false, false, getOuterClassName(interestingStackTraceElements.get(i + 1).getClassName()));
            }
            if (loggingSystemRegister.isInLoggingSystem(currentClassName)) {
                return new CallOrigin(false, true, null);
            }
        }
        throw new IllegalStateException("Must be called from down stack of " + PerContextPrintStream.class.getName());
    }

    private static Optional<Integer> frameWithPrintStreamClassName(final List<StackTraceElement> stackTraceElements, int startPoint) {
        for (int i = startPoint; i < stackTraceElements.size(); i++) {
            StackTraceElement stackTraceElement = stackTraceElements.get(i);
            String currentClassName = stackTraceElement.getClassName();
            if (currentClassName.equals(PerContextPrintStream.class.getName())) {
                return Optional.of(i);
            }
        }
        return Optional.absent();
    }

    private static String getOuterClassName(final String className) {
        final int startOfInnerClassName = className.indexOf('$');
        final String outerClassName;
        if (startOfInnerClassName == -1) {
            outerClassName = className;
        } else {
            outerClassName = className.substring(0, startOfInnerClassName);
        }
        return outerClassName;
    }
}
