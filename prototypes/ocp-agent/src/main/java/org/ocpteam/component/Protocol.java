package org.ocpteam.component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ocpteam.entity.Session;
import org.ocpteam.entity.StreamSerializer;
import org.ocpteam.interfaces.IActivity;
import org.ocpteam.interfaces.IModule;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.interfaces.ISerializer;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.EOMObject;
import org.ocpteam.serializable.InputFlow;
import org.ocpteam.serializable.InputMessage;

public class Protocol extends DSContainer<DataSource> implements IProtocol {

	private StreamSerializer streamSerializer;

	private Map<Integer, ITransaction> map = new HashMap<Integer, ITransaction>();

	private Map<Integer, IActivity> activityMap = new HashMap<Integer, IActivity>();

	public Protocol() {
		streamSerializer = new StreamSerializer();
	}

	@Override
	public void init() throws Exception {
		super.init();
		streamSerializer.setSerializer(ds().getComponent(ISerializer.class));
		// load all module
		JLG.debug("components: " + this.getDesigner().getMap());
		Iterator<Object> it = ds().iteratorComponent();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof IModule) {
				load((IModule) o);
			}
		}
	}

	public void load(IModule m) throws Exception {
		JLG.debug("loading module: " + m.getClass());
		for (Method f : m.getClass().getMethods()) {
			Object o = f.getReturnType();
			if (o == ITransaction.class) {
				ITransaction t = (ITransaction) f.invoke(m);
				map.put(t.getId(), t);
			}
			if (o == IActivity.class) {
				IActivity t = (IActivity) f.invoke(m);
				activityMap.put(t.getId(), t);
			}
		}
		JLG.debug("map: " + map);
	}

	@Override
	public StreamSerializer getStreamSerializer() {
		return streamSerializer;
	}

	@Override
	public void process(Socket clientSocket) throws Exception {
		// read the first object
		JLG.debug("about to read object");
		Serializable o = getStreamSerializer().readObject(clientSocket);
		if (o instanceof InputMessage) {
			InputMessage inputMessage = (InputMessage) o;
			Session session = new Session(ds(), clientSocket);
			inputMessage.transaction = getMap().get(inputMessage.transid);
			Serializable s = inputMessage.transaction.run(session,
					inputMessage.objects);
			getStreamSerializer().writeObject(clientSocket, s);
		}
		if (o instanceof InputFlow) {
			InputFlow inputFlow = (InputFlow) o;
			Session session = new Session(ds(), clientSocket);
			inputFlow.activity = getActivityMap().get(inputFlow.activityid);
			if (inputFlow.activity == null) {
				throw new Exception("activity not found. inputFlow.activityid=" + inputFlow.activityid);
			}
			inputFlow.activity.run(session, inputFlow.objects, clientSocket, this);
			getStreamSerializer().writeObject(clientSocket, new EOMObject());
		}
		JLG.debug("end process");
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

	private Map<Integer, IActivity> getActivityMap() {
		return this.activityMap;
	}

}
