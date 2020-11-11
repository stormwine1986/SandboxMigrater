package org.ilm.tools.sandbox.migrate.assistant.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.io.FileUtils;
import org.ilm.tools.sandbox.migrate.assistant.internal.DerbyConncetionFactory;
import org.ilm.tools.sandbox.migrate.assistant.internal.Migrater;
import org.ilm.tools.sandbox.migrate.assistant.model.DataSet;
import org.ilm.tools.sandbox.migrate.assistant.model.Sandbox;
import org.ilm.tools.sandbox.migrate.assistant.model.Version;
import org.ilm.tools.sandbox.migrate.assistant.model.VersionIndentity;

public class MainFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JButton versionSelectionButton;
	private JButton selectOldDataSetBtn;
	private JLabel selectOldDataSetLabel;
	private JComboBox<Version> versionSelection;
	private JButton selectHomeBtn;
	private JLabel selectHomeLabel;
	private JButton migrateBtn;
	
	private Version selectedVersion = null;
	private String homeDir;
	private String oldDataSetDir;

	private InvalidSandboxTable table;

	private JCheckBox migrateVScb;

	private JPanel homePanel;

	private Exception migrateException;

	public MainFrame() {
		super();
		homeDir = System.getProperties().getProperty("user.home"); 
		setTitle("沙箱迁移助手");
		setLayout(new BorderLayout()); 
		setSize(900, 600);
		setResizable(true);
		setLocationRelativeTo(getOwner());
		JPanel jp=new JPanel();
		selectOldDataSetLabel = new JLabel("旧数据集: <请选择>");
		jp.add(selectOldDataSetLabel);
		selectOldDataSetBtn = new JButton("...");
		selectOldDataSetBtn.addActionListener(this);
		jp.add(selectOldDataSetBtn);
		JLabel label = new JLabel(" 目标版本 ");
		jp.add(label);
		versionSelection = new JComboBox<Version>();
		versionSelection.addItem(new Version("--请选择--", -1, -1));
		versionSelection.addItem(new Version("12.3.1.0", 1, 12));
		versionSelection.addItem(new Version("10.9", 1, 11));
        jp.add(versionSelection);
        add(jp, BorderLayout.NORTH);
        versionSelectionButton = new JButton("确定");
        versionSelectionButton.addActionListener(this);
        jp.add(versionSelectionButton);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		try { 
			if(e.getSource().equals(versionSelectionButton)){
				clickSelectVersionBtn(e);
			}else if(e.getSource().equals(selectHomeBtn)) {
				clickSelectHomeBtn(e);
			}else if(e.getSource().equals(migrateBtn)) {
					clickMigrateBtn(e);
			}else if(e.getSource().equals(selectOldDataSetBtn)) {
				clickSelectOldDataSetBtn(e);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, 
					"迁移程序遇到错误: " + ex.getMessage(), "遇到错误",JOptionPane.ERROR_MESSAGE);
		}
	}

	private void clickSelectOldDataSetBtn(ActionEvent e) {
		JFileChooser chooser = new JFileChooser(oldDataSetDir);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showDialog(this, "选择");
		if (result == JFileChooser.APPROVE_OPTION) {
			oldDataSetDir = chooser.getSelectedFile().getAbsolutePath();
			selectOldDataSetLabel.setText("旧数据集: " + oldDataSetDir);
        }
	}

	private void clickMigrateBtn(ActionEvent e) throws Exception {
		int opt = JOptionPane.showConfirmDialog(
					this,
			    	"确认要进行沙箱迁移？确认点击【是】，再想想点击【否】", "确认信息",
			    	JOptionPane.YES_NO_OPTION);
		if (opt == JOptionPane.YES_OPTION) {
			MigrateProgressDialog dialog = new MigrateProgressDialog(this);		
			new Thread(() -> {
				try {
					Migrater.process(table.getSandboxes(), selectedVersion, oldDataSetDir, dialog);
					String clientdbLoc = oldDataSetDir + "\\clientdb";
					String installLoc = homeDir + "\\.mks\\clientdb";
					if(!clientdbLoc.equals(installLoc)) {
						FileUtils.copyDirectory(new File(clientdbLoc), new File(installLoc));
						if(migrateVScb.isSelected()) {
							FileUtils.copyDirectory(new File(oldDataSetDir + "\\viewset"), new File(homeDir + "\\.mks\\viewset"));
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					migrateException = e1;
				} finally {
					dialog.dispose();					
				}
			}).start();
			// 显示进度
			dialog.setVisible(true);
			if(migrateException == null) {
				// 迁移过程无异常
				System.out.println("Migrate Completed");
				// 显示完成提示
				JOptionPane.showMessageDialog(this, "迁移已经完成，点击确定后退出。");
				this.dispose();				
			}else {
				// 迁移过程有异常
				throw new IllegalStateException(migrateException);
			}
		}
	}

	private void clickSelectHomeBtn(ActionEvent e) {
		JFileChooser chooser = new JFileChooser(homeDir);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showDialog(this, "选择");
		if (result == JFileChooser.APPROVE_OPTION) {
			homeDir = chooser.getSelectedFile().getAbsolutePath();
			selectHomeLabel.setText(selectHomeLabel.getText() + homeDir);
        }
	}

	private void clickSelectVersionBtn(ActionEvent e) {
		try {
			if(oldDataSetDir == null) {
				JOptionPane.showMessageDialog(this, "请选择旧数据集目录。", "提示",JOptionPane.WARNING_MESSAGE);
				return;
			}
			Version value = (Version) versionSelection.getSelectedItem();
			if("--请选择--".equals(value.toString())) {
				JOptionPane.showMessageDialog(this, "请选择目标版本。", "提示",JOptionPane.WARNING_MESSAGE);
				return;
			}
			selectedVersion = value;
			versionSelection.setEnabled(false);
			versionSelectionButton.setEnabled(false);
			selectOldDataSetBtn.setEnabled(false);
			
			DataSet oldDataSet = loadOldDataSet();
			
			table = InvalidSandboxTable.getInstance(oldDataSet.getInvalidSandbox());
			JScrollPane scrollPane = new JScrollPane(table, 
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			this.add(scrollPane, BorderLayout.CENTER);
			
			homePanel = new JPanel();
			this.add(homePanel, BorderLayout.SOUTH);
			selectHomeLabel = new JLabel("当前用户目录: " + homeDir);
			homePanel.add(selectHomeLabel);
			selectHomeBtn = new JButton("...");
			selectHomeBtn.addActionListener(this);
			homePanel.add(selectHomeBtn);
			migrateVScb = new JCheckBox("同时迁移 Viewset");
			homePanel.add(migrateVScb);
			migrateBtn = new JButton("迁移");
			migrateBtn.addActionListener(this);
			homePanel.add(migrateBtn);
			
			this.setVisible(true);
		}catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "迁移程序遇到错误: " + ex.getMessage(), "遇到错误",JOptionPane.ERROR_MESSAGE);
			// 恢复按钮的可用性
			versionSelectionButton.setEnabled(true);
			selectOldDataSetBtn.setEnabled(true);
		}
	}

	private DataSet loadOldDataSet() throws Exception {
		DataSet dataSet = new DataSet();
		Connection conn = null;
    	try {
    		conn = DerbyConncetionFactory.getConnection(oldDataSetDir + "/clientdb");
    		Optional<VersionIndentity> identity = getVersionIdentity(conn);
    		List<Sandbox> sandboxes = getSandboxes(conn);
			List<Sandbox> invalidSandboxs = getInvalidSandboxs(sandboxes);
			dataSet.setVersionIndentity(identity.get());
			dataSet.setInvalidSandbox(invalidSandboxs);
    	} finally {
    		if(conn!=null) {
    			try {
    				conn.close();    				
    			}catch (Exception e) {}
    		}
    	}
		return dataSet;
	}
	
	private Optional<VersionIndentity> getVersionIdentity(Connection conn) throws SQLException {
		Optional<VersionIndentity> optional = Optional.empty();
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM VERSIONIDENTITY WHERE name = 'SIClient'");
		while(resultSet.next()) {
			int major = resultSet.getInt("MAJORVERSION");
			int minor = resultSet.getInt("MINORVERSION");
			VersionIndentity identity = new VersionIndentity("SIClient", major, minor);
			optional = Optional.of(identity);
		}
		return optional;
	}
	
	private List<Sandbox> getSandboxes(Connection conn) throws Exception {
		List<Sandbox> sandboxs = new LinkedList<Sandbox>();
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM SANDBOX");
		while(resultSet.next()) {
			Integer id = resultSet.getObject("ID", Integer.class);
			Integer topId = resultSet.getObject("TOPLEVEL_SANDBOX", Integer.class);
			String projectFile = resultSet.getString("PROJECTFILE");
			String projectFileCase = resultSet.getString("PROJECTFILELCASE");
			String folder = resultSet.getString("FOLDER");
			String folderCase = resultSet.getString("FOLDERLCASE");
			String configPath = resultSet.getString("CONFIGPATH");
			Sandbox sandbox = new Sandbox();
			sandboxs.add(sandbox);
			sandbox.setId(id);
			sandbox.setTopId(topId);
			sandbox.setProjectFile(projectFile);
			sandbox.setProjectFileCase(projectFileCase);
			sandbox.setFolder(folder);
			sandbox.setFolderCase(folderCase);
			sandbox.setConfigPath(configPath);
		}
		return sandboxs;
	}
	
	private List<Sandbox> getInvalidSandboxs(List<Sandbox> sandboxes) {
		List<Sandbox> invalidSandboxs = new LinkedList<Sandbox>();
		for(Sandbox sandbox: sandboxes) {
			if(!isValid(sandbox)) {
				invalidSandboxs.add(sandbox);
			}
		}
		return invalidSandboxs;
	}
	
	private boolean isValid(Sandbox sandbox) {
		File dir = new File(sandbox.getFolder());
		return dir.exists() && dir.isDirectory();
	}

}
