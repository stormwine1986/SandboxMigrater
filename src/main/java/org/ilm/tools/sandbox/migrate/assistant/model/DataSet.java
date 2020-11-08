package org.ilm.tools.sandbox.migrate.assistant.model;

import java.util.List;

public class DataSet {
	
	private VersionIndentity versionIndentity;
	private List<Sandbox> invalidSandbox;
	
	public VersionIndentity getVersionIndentity() {
		return versionIndentity;
	}
	public void setVersionIndentity(VersionIndentity versionIndentity) {
		this.versionIndentity = versionIndentity;
	}
	public List<Sandbox> getInvalidSandbox() {
		return invalidSandbox;
	}
	public void setInvalidSandbox(List<Sandbox> invalidSandbox) {
		this.invalidSandbox = invalidSandbox;
	}
	
	
}
