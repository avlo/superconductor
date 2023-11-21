package com.prosilion.nostrrelay.event;

public class Nip001Message {

	private String name;

	public Nip001Message() {
	}

	public Nip001Message(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
