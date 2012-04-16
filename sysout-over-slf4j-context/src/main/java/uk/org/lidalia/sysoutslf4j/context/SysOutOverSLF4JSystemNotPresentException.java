package uk.org.lidalia.sysoutslf4j.context;

public class SysOutOverSLF4JSystemNotPresentException extends RuntimeException {

    public static final String MESSAGE =
            "You do not seem to have sysout-over-slf4j-system on your classpath.  In order to redirect system outputs to slf4j " +
            "it is necessary to add this jar to your classpath, at a point where it is visible to all classloaders and " +
            "where the classloader that will load it will never discard it.";

    public SysOutOverSLF4JSystemNotPresentException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
