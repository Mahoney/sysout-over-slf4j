package uk.org.lidalia.sysoutslf4j.context;

import java.util.Arrays;

import uk.org.lidalia.sysoutslf4j.system.PerContextSystemOutput;
import uk.org.lidalia.sysoutslf4j.system.SystemOutput;

import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.WarnStatus;

public final class ConsoleAppender<E> extends OutputStreamAppender<E> {

	private PerContextSystemOutput target = PerContextSystemOutput.OUT;

	/**
	 * Sets the value of the <b>Target</b> option. Recognized values are
	 * "System.out" and "System.err". Any other value will be ignored.
	 */
	public void setTarget(String value) {
		PerContextSystemOutput t = PerContextSystemOutput.findByName(value.trim());
		if (t == null) {
			targetWarn(value);
		} else {
			target = t;
		}
	}

	private void targetWarn(String val) {
		Status status = new WarnStatus("[" + val + "] should be one of "
				+ Arrays.toString(SystemOutput.values()), this);
		status.add(new WarnStatus("Using previously set target, System.out by default.", this));
		addStatus(status);
	}

	@Override
	public void start() {
		setOutputStream(target.getOriginalPrintStream());
		super.start();
	}
}
