package org.ocpteam.component;

import org.ocpteam.entity.StreamSerializer;
import org.ocpteam.entity.TransactionSerializer;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.interfaces.IStreamSerializer;
import org.ocpteam.interfaces.ITransactionSerializer;

public abstract class Protocol extends DataSourceContainer implements IProtocol {

	private IStreamSerializer streamSerializer;
	private ITransactionSerializer transactionSerializer;

	@Override
	public IStreamSerializer getStreamSerializer() {
		if (streamSerializer == null) {
			streamSerializer = new StreamSerializer();
		}
		return streamSerializer;
	}
	
	@Override
	public ITransactionSerializer getTransactionSerializer() {
		if (transactionSerializer == null) {
			transactionSerializer = new TransactionSerializer();
		}
		return transactionSerializer;
	}

}
