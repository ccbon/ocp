package org.ocpteam.interfaces;

import java.io.Serializable;

/**
 * A transaction serializer is used to (de)serialize a transacion input/output.
 *
 */
public interface ITransactionSerializer {
	byte[] serialize(Class<? extends ITransaction> transaction,
			Serializable... object) throws Exception;
	
	Serializable deserialize(byte[] output);
}
