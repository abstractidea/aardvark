package com.serym.hackathon.aardvark.bouncer;

/**
 * ServerRequestException represents an error that occurred while sending a
 * server request or receiving a server response.
 */
public class ServerRequestException extends Exception {

	/**
	 * Needed because Exception implements Serializable. For best practices,
	 * should increment the value by 1 each release in which the class changes.
	 */
	private static final long serialVersionUID = 1L;

	public ServerRequestException() {
		super();
	}

	public ServerRequestException(String message) {
		super(message);
	}

	public ServerRequestException(Throwable cause) {
		super(cause);
	}

	public ServerRequestException(String message, Throwable cause) {
		super(message, cause);
	}

}
