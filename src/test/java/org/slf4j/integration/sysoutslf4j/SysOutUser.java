package org.slf4j.integration.sysoutslf4j;

class SysOutUser implements ISysOutUser {
	public void useSysOut() {
		System.out.println("Logged");
	}
}