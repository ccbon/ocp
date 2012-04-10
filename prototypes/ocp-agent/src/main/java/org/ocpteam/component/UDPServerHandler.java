package org.ocpteam.component;

import java.net.DatagramPacket;

import org.ocpteam.core.Component;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.interfaces.IUDPServerHandler;
import org.ocpteam.misc.JLG;

public class UDPServerHandler extends Component implements IUDPServerHandler {

	private IProtocol protocol;
	private DatagramPacket packet;
	
	public void setProtocol(IProtocol protocol) {
		this.protocol = protocol;	
	}

	public UDPServerHandler duplicate() {
		UDPServerHandler handler = new UDPServerHandler();
		handler.setParent(parent);
		handler.setProtocol(protocol);
		return handler;
	}

	@Override
	public void run() {
		try {
			protocol.process(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JLG.debug("end");
	}

	@Override
	public void setDatagramPacket(DatagramPacket packet) {
		this.packet = packet;
	}

}
