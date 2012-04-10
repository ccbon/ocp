package org.ocpteam.interfaces;

import java.net.DatagramPacket;

public interface IUDPServerHandler extends Runnable {

	void setDatagramPacket(DatagramPacket packet);

}
