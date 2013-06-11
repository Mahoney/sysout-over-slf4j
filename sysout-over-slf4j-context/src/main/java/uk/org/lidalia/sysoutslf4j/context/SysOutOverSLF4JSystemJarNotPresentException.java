package uk.org.lidalia.sysoutslf4j.context;

public class SysOutOverSLF4JSystemJarNotPresentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final String MESSAGE =
            "You do not seem to have sysout-over-slf4j-system on your classpath.  In order to redirect system outputs to slf4j " +
            "it is necessary to add this jar to your classpath, at a point where it is visible to all class loaders and " +
            "where the class loader that will load it will never be discarded.";

    public SysOutOverSLF4JSystemJarNotPresentException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
