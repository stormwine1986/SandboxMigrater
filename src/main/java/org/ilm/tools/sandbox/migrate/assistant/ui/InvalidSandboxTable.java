package org.ilm.tools.sandbox.migrate.assistant.ui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.ilm.tools.sandbox.migrate.assistant.model.Sandbox;

public class InvalidSandboxTable extends JTable implements MouseListener {

	private static final long serialVersionUID = 1L;
	
	public static InvalidSandboxTable getInstance(List<Sandbox> invalidSandbox) {
		Object[] columns={"原来的位置","新的位置"};
		Object[][] data= new Object[invalidSandbox.size()][1];
		
		for(int i = 0; i < invalidSandbox.size(); i++) {
			Sandbox sd = invalidSandbox.get(i);
			data[i][0] = sd.getFolder();
		}
		
		DefaultTableModel model=new DefaultTableModel(data, columns);
		
		return new InvalidSandboxTable(model, invalidSandbox);
	}

	private List<Sandbox> invalidSandbox;

	public InvalidSandboxTable(DefaultTableModel model, List<Sandbox> invalidSandbox) {
		super(model);
		this.invalidSandbox = invalidSandbox;
		addMouseListener(this);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			Point p = e.getPoint();
			int row = this.rowAtPoint(p);
			int column = this.columnAtPoint(p);
			String value = (String) getValueAt(row, column);
			if(column == 1) {
				JFileChooser chooser = new JFileChooser(value);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle("选择 " + (String) getValueAt(row, 0) + " 的替代目录");
				int opt = chooser.showDialog(this, "选择");
				if(opt == JFileChooser.APPROVE_OPTION) {
					String newFolder = formatPath(chooser.getSelectedFile().getAbsolutePath());
					setValueAt(newFolder, row, column);
					Sandbox sandbox = invalidSandbox.get(row);
					sandbox.setNewFolderPath(newFolder);
					if(isTop(sandbox)) {
						List<Sandbox> subSandboxes = getSubSandbox(sandbox);
						autoFillNewFolder(subSandboxes, sandbox, newFolder);
					}
				}
			}
		}
	}

	private void autoFillNewFolder(List<Sandbox> subSandboxes, Sandbox topSandbox, String newTopFolder) {
		String topFolder = topSandbox.getFolder();
		for(Sandbox sd :subSandboxes) {
			int row = invalidSandbox.indexOf(sd);
			String subfolder = sd.getFolder();
			if(subfolder.startsWith(topFolder)) {
				String newSubFoler = formatPath(subfolder.replace(topFolder, newTopFolder));
				setValueAt(newSubFoler, row, 1);
				sd.setNewFolderPath(newSubFoler);
			}
		}
	}

	private List<Sandbox> getSubSandbox(Sandbox sandbox) {
		final Integer id = sandbox.getId();
		return invalidSandbox.stream().filter(sd -> id.equals(sd.getTopId())).collect(Collectors.toList());
	}

	private boolean isTop(Sandbox sandbox) {
		return sandbox.getTopId() == null;
	}
	
	private static String formatPath(String path) {
		String driver = path.substring(0, path.indexOf(":")).toLowerCase(Locale.ROOT);
		String p = path.substring(path.indexOf(":"));
		
		return driver + p;
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void freeze() {
		removeMouseListener(this);
	}

	public void active() {
		addMouseListener(this);
	}

	public List<Sandbox> getSandboxes() {
		return invalidSandbox == null? new LinkedList<>(): invalidSandbox;
	}

}
