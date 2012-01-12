package com.guenego.ocp.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.guenego.misc.JLG;
import com.guenego.ocp.TreeEntry;

public class RenameRemoteFileAction extends Action {

	private UserExplorerComposite composite;

	public RenameRemoteFileAction(UserExplorerComposite userExplorerComposite) {
		this.composite = userExplorerComposite;
		setText("&Rename@F2");
		setToolTipText("Rename");
	}

	public void run() {
		JLG.debug("Rename");
		final TableItem item = composite.remoteDirectoryTable.getSelection()[0];
		final String name = item.getText(0);
		final Text text = new Text(item.getParent(), SWT.BORDER);
		text.setBounds(item.getBounds());
		text.setText(name);
		text.setSelection(0, name.length());
		text.setFocus();
		text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.KEYPAD_CR:
				case (int) '\r':
					if ((text.getText() != "")
							&& (!name.equals(text.getText()))) {
						composite.renameRemoteFile(name, text.getText());
						item.setText(0, text.getText());
						try {
							TreeEntry te = composite.fs.getTree(
									composite.currentRemoteDirString).getEntry(
									text.getText());
							item.setData(te);
						} catch (Exception e1) {
							// TODO: handle exception
						}
					}
					text.dispose();
					break;
				case SWT.ESC:
					text.dispose();
					break;
				default:
				}
				JLG.debug("keypressed: keycode:" + e.keyCode
						+ " and character = '" + e.character + "'");
			};
		});
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if ((text.getText() != "") && (!name.equals(text.getText()))) {
					composite.renameRemoteFile(name, text.getText());
					item.setText(0, text.getText());
					try {
						TreeEntry te = composite.fs.getTree(
								composite.currentRemoteDirString).getEntry(
								text.getText());
						item.setData(te);
					} catch (Exception e1) {
						// TODO: handle exception
					}
				}
				text.dispose();
				super.focusLost(e);
			}
		});
	}
}