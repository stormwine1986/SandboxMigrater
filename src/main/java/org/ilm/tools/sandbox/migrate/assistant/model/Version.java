package org.ilm.tools.sandbox.migrate.assistant.model;

public class Version {
	
	private String displayName;
	private Integer siClientMajor;
	private Integer siClientMinor;
	
	public Version(String displayName, Integer siClientMajor, Integer siClientMinor) {
		this.displayName = displayName;
		this.siClientMajor = siClientMajor;
		this.siClientMinor = siClientMinor;
	}

	@Override
	public String toString() {
		return displayName;
	}

	public Integer getSiClientMajor() {
		return siClientMajor;
	}

	public Integer getSiClientMinor() {
		return siClientMinor;
	}
	
}
