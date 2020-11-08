package org.ilm.tools.sandbox.migrate.assistant.model;

public class Sandbox {

	private Integer id;
	private Integer topId;
	private String projectFile;
	private String projectFileCase;
	private String folder;
	private String folderCase;
	private String configPath;
	
	private String newFolderPath;

	public void setId(Integer id) {
		this.id = id;
	}

	public void setTopId(Integer topId) {
		this.topId = topId;
	}

	public void setProjectFile(String projectFile) {
		this.projectFile = projectFile;
	}

	public void setProjectFileCase(String projectFileCase) {
		this.projectFileCase = projectFileCase;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public void setFolderCase(String folderCase) {
		this.folderCase = folderCase;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public Integer getId() {
		return id;
	}

	public Integer getTopId() {
		return topId;
	}

	public String getProjectFile() {
		return projectFile;
	}

	public String getProjectFileCase() {
		return projectFileCase;
	}

	public String getFolder() {
		return folder;
	}

	public String getFolderCase() {
		return folderCase;
	}

	public String getConfigPath() {
		return configPath;
	}

	public boolean isTop() {
		return (topId == null)? true: false;
	}

	public String getNewFolderPath() {
		return newFolderPath;
	}

	public void setNewFolderPath(String newFolderPath) {
		this.newFolderPath = newFolderPath;
	}

}
