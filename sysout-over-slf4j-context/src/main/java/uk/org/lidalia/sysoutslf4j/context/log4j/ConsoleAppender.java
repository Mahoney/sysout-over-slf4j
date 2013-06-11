package uk.org.lidalia.sysoutslf4j.context.log4j;

import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.helpers.LogLog;

import uk.org.lidalia.sysoutslf4j.system.PerContextSystemOutput;

public class ConsoleAppender extends WriterAppender {

    public static final String SYSTEM_OUT = "System.out";
    public static final String SYSTEM_ERR = "System.err";

    protected String target = SYSTEM_OUT;

    /**
     * Constructs an unconfigured appender.
     */
    public ConsoleAppender() {
    }

    /**
     * Creates a configured appender.
     *
     * @param layout layout, may not be null.
     */
    public ConsoleAppender(Layout layout) {
        this(layout, SYSTEM_OUT);
    }

    /**
     *   Creates a configured appender.
     * @param layout layout, may not be null.
     * @param target target, either "System.err" or "System.out".
     */
    public ConsoleAppender(Layout layout, String target) {
        setLayout(layout);
        setTarget(target);
        activateOptions();
    }

    /**
     *  Sets the value of the <b>Target</b> option. Recognized values
     *  are "System.out" and "System.err". Any other value will be
     *  ignored.
     * */
    public void setTarget(String value) {
        String v = value.trim();

        if (SYSTEM_OUT.equalsIgnoreCase(v)) {
            target = SYSTEM_OUT;
        } else if (SYSTEM_ERR.equalsIgnoreCase(v)) {
            target = SYSTEM_ERR;
        } else {
            targetWarn(value);
        }
    }

    /**
     * Returns the current value of the <b>Target</b> property. The
     * default value of the option is "System.out".
     *
     * See also {@link #setTarget}.
     * */
    public String getTarget() {
        return target;
    }

    void targetWarn(String val) {
        LogLog.warn("["+val+"] should be System.out or System.err.");
        LogLog.warn("Using previously set target, System.out by default.");
    }

    /**
     *   Prepares the appender for use.
     */
    public void activateOptions() {
        if (target.equals(SYSTEM_ERR)) {
            setWriter(createWriter(PerContextSystemOutput.ERR.getOriginalPrintStream()));
        } else {
            setWriter(createWriter(PerContextSystemOutput.OUT.getOriginalPrintStream()));
        }
        super.activateOptions();
    }

    @Override
    protected final void closeWriter() {
    }
}
