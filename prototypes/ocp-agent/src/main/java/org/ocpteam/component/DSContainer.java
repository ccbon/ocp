package org.ocpteam.component;

import org.ocpteam.core.Container;

public class DSContainer<T extends DataSource> extends Container<T> {
	public T ds() {
		return getRoot();
	}
}
