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

public class RenameFileAction extends Action {

	private UserExplorerComposite composite;

	public RenameFileAction(UserExplorerComposite userExplorerComposite) {
		this.composite = userExplorerComposite;
		setText("&Rename@F2");
		setToolTipText("Rename");
	}

	public void run() {
		JLG.debug("Rename");
		final TableItem item = composite.localDirectoryTable.getSelection()[0];
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
					if ((text.getText() != "") && (!name.equals(text.getText()))) {
						JLG.debug("renaming...");
						composite.renameLocalFile(name, text.getText());
						item.setText(0, text.getText());
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
					JLG.debug("renaming2...");
					JLG.debug("name = |" + name + "|");
					JLG.debug("text.getText() = |" + text.getText() + "|");
					composite.renameLocalFile(name, text.getText());
					item.setText(0, text.getText());
				}
				text.dispose();
				super.focusLost(e);
			}
		});
	}
}