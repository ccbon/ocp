package org.ocpteam.component;

import java.net.Socket;

import org.ocpteam.core.IComponent;

public interface IProtocol extends IComponent {

	byte[] process(byte[] input, Socket clientSocket) throws Exception;
}
