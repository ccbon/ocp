package org.ocpteam.serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;

public class DebugContent extends Content {
	private IStructurable structurable;

	public DebugContent() {
	}

	public DebugContent(String username, byte[] value, byte[] signature,
			IStructurable structurable) {
		super(username, value, signature);
		this.structurable = structurable;
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = super.toStructure();
		result.setName(getClass());
		result.setSubstructField("debug", structurable);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		super.fromStructure(s);
		structurable = s.getSubstructField("debug");
	}

	private static final long serialVersionUID = 1L;

}
