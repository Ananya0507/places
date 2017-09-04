package com.sample.demo.domain;

public class Place {
	private String name;
	private String reference;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	@Override
	public String toString() {
		return "Place [name=" + name + ", reference=" + reference + "]";
	}
}
