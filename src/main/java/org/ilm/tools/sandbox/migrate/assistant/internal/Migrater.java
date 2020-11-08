package org.ilm.tools.sandbox.migrate.assistant.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;

import org.ilm.tools.sandbox.migrate.assistant.model.Sandbox;
import org.ilm.tools.sandbox.migrate.assistant.model.Version;
import org.ilm.tools.sandbox.migrate.assistant.model.VersionIndentity;
import org.ilm.tools.sandbox.migrate.assistant.ui.MigrateProgressDialog;

public class Migrater {

	public static void process(List<Sandbox> sandboxes, Version version, String dataSetDir, MigrateProgressDialog dialog) throws Exception {
		System.out.println("Begin Migrate: total = " + sandboxes.size());
		dialog.setTotal(sandboxes.size());
		String dbname = dataSetDir + "/clientdb";
		Connection conn = null;
		try {
			conn = DerbyConncetionFactory.getConnection(dbname);
			updateVersionIdentity(new VersionIndentity("SIClient", version.getSiClientMajor(), version.getSiClientMinor()), 
					conn);
			for(Sandbox sd :sandboxes) {
				System.out.println("Migrating " + sd);
				// 只有输入了新位置的沙箱才需要处理
				if(sd.getNewFolderPath() != null) {
					Sandbox newSd = null;
					if(isTop(sd)) {
						newSd = changeFolder(sd, sd);							
					} else {
						Sandbox top = getTopSandbox(sd, sandboxes);
						newSd = changeFolder(sd, top);
					}
					updateSandbox(newSd, conn);
				}
				dialog.addCompleted();
			}
		} finally {
			if(conn != null) {
				try {
					conn.close();					
				}catch (Exception e) {
				}
			}
		}
	}
	
	private static Sandbox changeFolder(Sandbox sandbox, Sandbox top) {
		Sandbox newSd = new Sandbox();
		newSd.setId(sandbox.getId());
		newSd.setTopId(sandbox.getTopId());
		newSd.setProjectFile(sandbox.getNewFolderPath() + "\\project.pj");
		newSd.setProjectFileCase(sandbox.getNewFolderPath().toLowerCase(Locale.ROOT) + "\\project.pj");
		newSd.setFolder(sandbox.getNewFolderPath());
		newSd.setFolderCase(sandbox.getNewFolderPath().toLowerCase(Locale.ROOT));
		newSd.setConfigPath(sandbox.getConfigPath().replace(top.getFolder().replace("\\", "/"), top.getNewFolderPath().replace("\\", "/")));
		return newSd;
	}
	
	private static Sandbox getTopSandbox(Sandbox sandbox, List<Sandbox> sandboxes) {
		final Integer topId = sandbox.getTopId();
		return sandboxes.stream().filter(sd -> topId.equals(sd.getId())).findFirst().get();
	}

	private static boolean isTop(Sandbox sandbox) {
		return sandbox.getTopId() == null;
	}

	private static void updateSandbox(Sandbox sandbox, Connection conn) throws SQLException {
		String sql = String.format(
				Locale.ROOT, 
				"UPDATE SANDBOX SET PROJECTFILE='%s', PROJECTFILELCASE='%s', FOLDER='%s', FOLDERLCASE='%s', CONFIGPATH='%s' WHERE ID = %s", 
				sandbox.getProjectFile(),
				sandbox.getProjectFileCase(),
				sandbox.getFolder(),
				sandbox.getFolderCase(),
				sandbox.getConfigPath(),
				sandbox.getId());
		Statement statement = conn.createStatement();
		statement.execute(sql);
	}
	
	private static void updateVersionIdentity(VersionIndentity version, Connection conn) throws Exception {
		String sql = String.format(Locale.ROOT, "UPDATE VERSIONIDENTITY SET MAJORVERSION=%s, MINORVERSION=%s WHERE NAME = '%s'", 
				version.getMajor(),
				version.getMinor(),
				version.getName());
		Statement statement = conn.createStatement();
		statement.execute(sql);
	}

}
