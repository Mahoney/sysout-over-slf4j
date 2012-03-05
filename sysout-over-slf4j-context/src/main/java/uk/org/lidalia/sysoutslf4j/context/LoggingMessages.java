package uk.org.lidalia.sysoutslf4j.context;

public final class LoggingMessages {
    public static final String PERFORMANCE_WARNING = "A logging system is sending data to the console. This will work but with a significant performance hit ." +
            "Visit http://projects.lidalia.org.uk/sysout-over-slf4j/performance.html for details of how to avoid this.";

    private LoggingMessages() {
        throw new UnsupportedOperationException("Not instantiable");
    }
}
