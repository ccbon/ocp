package org.ocpteam.component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ocpteam.core.IComponent;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.entity.MessageSerializer;
import org.ocpteam.entity.Session;
import org.ocpteam.entity.StreamSerializer;
import org.ocpteam.interfaces.IMessageSerializer;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.interfaces.IStreamSerializer;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;

public abstract class Protocol extends DataSourceContainer implements IProtocol {

	private IStreamSerializer streamSerializer;
	private IMessageSerializer messageSerializer;
	private Map<Integer, ITransaction> map = new HashMap<Integer, ITransaction>();

	@Override
	public void init() throws Exception {
		super.init();
		// load all module
		JLG.debug("components: " + this.getDesigner().getMap());
		Iterator<IComponent> it = this.iteratorComponent();
		while (it.hasNext()) {
			IComponent c = it.next();
			if (c instanceof Module) {
				load((Module) c);
			}
		}
		it = ds().iteratorComponent();
		while (it.hasNext()) {
			IComponent c = it.next();
			if (c instanceof Module) {
				load((Module) c);
			}
		}
	}

	public void load(Module m) throws Exception {
		JLG.debug("loading module: " + m.getClass());
		for (Method f : m.getClass().getMethods()) {
			Object o = f.getReturnType();
			if (o == ITransaction.class) {
				ITransaction t = (ITransaction) f.invoke(m);
				map.put(t.getId(), t);
			}
		}
		JLG.debug("map: " + map);
	}

	@Override
	public IStreamSerializer getStreamSerializer() {
		if (streamSerializer == null) {
			streamSerializer = new StreamSerializer();
		}
		return streamSerializer;
	}

	@Override
	public IMessageSerializer getMessageSerializer() {
		if (messageSerializer == null) {
			messageSerializer = new MessageSerializer(this);
		}
		return messageSerializer;
	}

	@Override
	public byte[] process(byte[] input, Socket clientSocket) throws Exception {
		InputMessage inputMessage = getMessageSerializer()
				.deserializeInput(input);
		Session session = new Session(ds(), clientSocket);
		JLG.debug("running transaction: " + inputMessage.transaction.getId());
		Serializable s = inputMessage.transaction.run(session, inputMessage.objects);
		if (s == null) {
			return null;
		}
		JLG.debug("s.getClass=" + s.getClass());
		return getMessageSerializer().serializeOutput(s);
	}
	
	@Override
	public void process(DatagramPacket packet) {
		byte[] input = Arrays.copyOf(packet.getData(), packet.getLength());
		String s = new String(input);
		JLG.debug("length=" + packet.getLength());
		JLG.debug("message=" + s);
	}

	public Map<Integer, ITransaction> getMap() {
		return this.map;
	}

}
