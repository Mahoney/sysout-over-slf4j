package uk.org.lidalia.sysoutslf4j.context;

import java.io.Console;

public class TestConsole {

	public static void main(String[] args) {
		Console console = System.console();
		System.out.println(console);
	}
}
