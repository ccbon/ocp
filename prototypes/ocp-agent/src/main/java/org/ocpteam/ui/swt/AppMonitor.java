package org.ocpteam.ui.swt;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.composite.ExplorerComposite;

public class AppMonitor implements Runnable {

	public class MonitoredTask {

		public TableEditor te;
		public int lasti;
		private long startTime;
		public Future<?> future;

		public MonitoredTask(TableEditor te, Future<?> future) {
			this.te = te;
			this.future = future;
			this.lasti = 0;
			this.startTime = System.currentTimeMillis();
		}

	}

	public Map<Task, MonitoredTask> map;

	private Thread t;
	private DataSourceWindow w;
	private ThreadPoolExecutor pool;

	public AppMonitor(DataSourceWindow w) {
		this.w = w;
		map = new HashMap<Task, MonitoredTask>();
		int maxThread = w.ps.getInt(MonitorPreferencePage.MAXTHREAD);
		LOG.debug("maxThread=" + maxThread);
		pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);
	}

	public void start() {
		t = new Thread(this);
		t.start();
	}

	public void stop() {
		pool.shutdownNow();
		t.interrupt();
	}

	public void monitor(Task task) {
		// add a line in the table
		ExplorerComposite explorerComposite = null;
		CTabFolder tabFolder = w.tabFolder;
		CTabItem[] items = tabFolder.getItems();
		for (CTabItem item : items) {
			if (item.getControl().getClass() == ExplorerComposite.class) {
				explorerComposite = (ExplorerComposite) item.getControl();
			}
		}
		if (explorerComposite == null) {
			QuickMessage
					.error(w.getShell(), "Cant find the explorer composite");
			return;
		}
		TableEditor te = new TableEditor(explorerComposite.table);

		te.grabHorizontal = true;
		te.horizontalAlignment = SWT.LEFT;
		TableItem item = new TableItem(explorerComposite.table, SWT.NONE);
		item.setData(task);
		ProgressBar pb = new ProgressBar(explorerComposite.table,
				SWT.HORIZONTAL | SWT.SMOOTH | SWT.INDETERMINATE);
		te.setEditor(pb, item, 3);
		Future<?> future = pool.submit(task);
		MonitoredTask mt = new MonitoredTask(te, future);
		map.put(task, mt);
	}

	@Override
	public void run() {

		try {
			while (true) {
				Thread.sleep(w.ps.getInt(MonitorPreferencePage.REFRESHRATE));
				for (Task task : map.keySet()) {
					showProgress(task);
				}
			}
		} catch (InterruptedException e) {
		}

	}

	public void showProgress(final Task task) {
		if (!map.containsKey(task)) {
			return;
		}
		w.getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				showTask(task);

				if (task.isComplete() || map.get(task).future.isCancelled()) {
					map.get(task).te.getEditor().dispose();
					map.get(task).te.getItem().dispose();
					map.get(task).te.dispose();
					map.remove(task);
					for (MonitoredTask mt : map.values()) {
						mt.te.layout();
					}
				}
			}
		});
	}

	protected void showTask(Task task) {
		MonitoredTask mt = map.get(task);
		long elapsed = 0;
		if (mt == null) {
			return;
		}
		elapsed = (System.currentTimeMillis() - mt.startTime) / 1000;
		String sStarted = "N/A";
		if (task.hasStarted()) {
			long started = (System.currentTimeMillis() - task.startTime) / 1000;
			sStarted = started + "s";
		}

		String sRemaining = "N/A";
		TableItem item = map.get(task).te.getItem();
		if (item.isDisposed()) {
			return;
		}
		item.setText(new String[] { task.name, "10B", "U", "", "N/A",
				elapsed + "s", sStarted, sRemaining });
	}

	public ThreadPoolExecutor getPool() {
		return pool;
	}
}
