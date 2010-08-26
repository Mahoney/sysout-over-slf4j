package org.slf4j.sysoutslf4j.common;

public interface PrintStreamCoordinator {

	public abstract void replaceSystemOutputsWithSLF4JPrintStreams();

	public abstract void restoreOriginalSystemOutputs();

}