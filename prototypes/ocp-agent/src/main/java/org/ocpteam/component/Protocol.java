package org.ocpteam.component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ocpteam.core.IComponent;
import org.ocpteam.entity.EOMObject;
import org.ocpteam.entity.InputFlow;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.entity.Session;
import org.ocpteam.entity.StreamSerializer;
import org.ocpteam.interfaces.IActivity;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;

public class Protocol extends DataSourceContainer implements IProtocol {

	private StreamSerializer streamSerializer;

	private Map<Integer, ITransaction> map = new HashMap<Integer, ITransaction>();

	private Map<Integer, IActivity> activityMap = new HashMap<Integer, IActivity>();

	public Protocol() {
		streamSerializer = new StreamSerializer();
	}

	@Override
	public void init() throws Exception {
		super.init();
		// load all module
		JLG.debug("components: " + this.getDesigner().getMap());
		Iterator<IComponent> it = ds().iteratorComponent();
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
	public void process(DataInputStream in, DataOutputStream out,
			Socket clientSocket) throws Exception {
		// read the first object
		JLG.debug("about to read object");
		Serializable o = getStreamSerializer().readObject(in);
		if (o instanceof InputMessage) {
			InputMessage inputMessage = (InputMessage) o;
			Session session = new Session(ds(), clientSocket);
			inputMessage.transaction = getMap().get(inputMessage.transid);
			Serializable s = inputMessage.transaction.run(session,
					inputMessage.objects);
			getStreamSerializer().writeObject(out, s);
		}
		if (o instanceof InputFlow) {
			InputFlow inputFlow = (InputFlow) o;
			Session session = new Session(ds(), clientSocket);
			inputFlow.activity = getActivityMap().get(inputFlow.activityid);
			inputFlow.activity.run(session,
					inputFlow.objects, in, out, this);
			getStreamSerializer().writeObject(out, new EOMObject());
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
		return this.activityMap ;
	}

}
