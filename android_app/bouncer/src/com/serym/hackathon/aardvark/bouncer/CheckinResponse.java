package com.serym.hackathon.aardvark.bouncer;

/**
 * CheckinResponse represents the response from the server after a
 * {@link CheckinRequest} has been made.
 */
public class CheckinResponse {

	// TODO should have individual fields once we decide on them
	private String message;

	// TODO should have individual fields once we decide on them
	public CheckinResponse(String message) {
		this.message = message;
	}

	// TODO should have individual fields once we decide on them
	public String getMessage() {
		return this.message;
	}

}
