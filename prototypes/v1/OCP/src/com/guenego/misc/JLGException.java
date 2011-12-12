package com.guenego.misc;

public class JLGException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Exception e;

	public JLGException(Exception _e) {
		if (_e.getClass().equals(JLGException.class)) {
			e = ((JLGException) _e).e;
		} else {
			e = _e;
			JLG.error(e);
		}
	}

	public JLGException(String string) {
		e = new Exception(string);
		JLG.error(e);
	}

}
