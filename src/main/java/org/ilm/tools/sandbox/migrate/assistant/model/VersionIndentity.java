package org.ilm.tools.sandbox.migrate.assistant.model;

public class VersionIndentity {
	
	private String name;
	private Integer major;
	private Integer minor;
	
	public VersionIndentity(String name, Integer major, Integer minor) {
		this.name = name;
		this.major = major;
		this.minor = minor;
	}

	public void setMinor(Integer minor) {
		this.minor = minor;
	}

	public String getMajor() {
		return String.valueOf(major);
	}

	public String getMinor() {
		return String.valueOf(minor);
	}

	public String getName() {
		return String.valueOf(name);
	}
}
