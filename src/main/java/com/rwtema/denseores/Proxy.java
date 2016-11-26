package com.rwtema.denseores;

public class Proxy {

	public void postInit() {

	}

	public RuntimeException wrap(RuntimeException throwable) {
		return throwable;
	}

}
