package org.ocpteam.component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ocpteam.entity.Address;
import org.ocpteam.entity.Node;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

public abstract class AddressDataSource extends DSPDataSource {

	private RingNodeMap ringNodeMap;
	private RingAddressMap map;
	private Map<Address, byte[]> localMap;
	private MessageDigest md;
	private Random random;

	public AddressDataSource() throws Exception {
		super();
		addComponent(INodeMap.class, new RingNodeMap());
		addComponent(IAddressMap.class, new RingAddressMap());
		addComponent(IDataModel.class, new AddressMapDataModel());
		addComponent(MapModule.class);
		addComponent(RingMapModule.class);
		addComponent(MessageDigest.class);
		addComponent(Random.class);
		localMap = Collections.synchronizedMap(new HashMap<Address, byte[]>());
	}

	@Override
	public void init() throws Exception {
		super.init();
		ringNodeMap = (RingNodeMap) getComponent(INodeMap.class);
		map = (RingAddressMap) getComponent(IAddressMap.class);
		map.setNodeMap(ringNodeMap);
		map.setLocalMap(localMap);
		md = getComponent(MessageDigest.class);
		random = getComponent(Random.class);
	}

	@Override
	protected void readNetworkConfig() throws Exception {
		JLG.debug("readNetworkConfig " + getName());
		super.readNetworkConfig();
		getComponent(MessageDigest.class).setAlgo(
				network.getProperty("hash", "SHA-1"));
		ringNodeMap.setRingNbr(Integer.parseInt(network.getProperty("ringNbr",
				"3")));
	}

	@Override
	protected void askForNode() throws Exception {
		super.askForNode();
		setNode(new Node(new Id(md.hash(random.generate())),
				ringNodeMap.getLessPopulatedRing()));
	}

}
