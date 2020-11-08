package org.ilm.tools.sandbox.migrate.assistant.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class MigrateProgressDialog extends JDialog {

	private static final long serialVersionUID = 9153099250054990996L;
	private JLabel label;
	private int completed = 0;
	private int total = 0;;

	MigrateProgressDialog(JFrame frame){
		super(frame, true);
		setSize(350, 100);
		setLocationRelativeTo(null);
		setUndecorated(true);
		Border border = BorderFactory.createRaisedBevelBorder();
		JPanel panel = new JPanel(new BorderLayout());
		add(panel);
		panel.setBorder(border);
		label = new JLabel("", JLabel.CENTER);
		panel.add(label,BorderLayout.CENTER);
		
	}

	public void addCompleted() {
		completed ++;
		label.setText(String.format("%s/%s 正在迁移...", completed, total));
	}

	public void setTotal(int size) {
		this.total = size;
		label.setText(String.format("%s/%s 正在迁移...", completed, total));
	}
}
