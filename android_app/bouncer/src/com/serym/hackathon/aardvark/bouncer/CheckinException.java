package com.serym.hackathon.aardvark.bouncer;

/**
 * CheckinException represents an error that occurred while sending or receiving
 * a check-in.
 */
public class CheckinException extends Exception {

	/**
	 * Needed because Exception implements Serializable. For best practices,
	 * should increment the value by 1 each release in which the class changes.
	 */
	private static final long serialVersionUID = 1L;

	public CheckinException() {
		super();
	}

	public CheckinException(String message) {
		super(message);
	}

	public CheckinException(Throwable cause) {
		super(cause);
	}

	public CheckinException(String message, Throwable cause) {
		super(message, cause);
	}

}
