package com.serym.hackathon.aardvark.bouncer;

/**
 * CheckinResponse represents the response from the server after a
 * {@link CheckinRequest} has been made.
 */
public class CheckinResponse {

	/**
	 * The response code.
	 */
	private CheckinResponseCode responseCode;

	/**
	 * The user name. May be null if not specified in response.
	 */
	private String userName;

	/**
	 * Creates a CheckinResponse with the given response code and no user name.
	 * 
	 * @param responseCode
	 *            the response code
	 */
	public CheckinResponse(int responseCode) {
		this.responseCode = CheckinResponseCode.fromInt(responseCode);
		this.userName = null;
	}

	/**
	 * Creates a CheckinResponse with the given response code and no user name.
	 * 
	 * @param responseCode
	 *            the response code
	 */
	public CheckinResponse(CheckinResponseCode responseCode) {
		this.responseCode = responseCode;
		this.userName = null;
	}

	/**
	 * Creates a CheckinResponse with the given response code and user name.
	 * 
	 * @param responseCode
	 *            the response code
	 * @param userName
	 *            the user name
	 */
	public CheckinResponse(int responseCode, String userName) {
		this.responseCode = CheckinResponseCode.fromInt(responseCode);
		this.userName = userName;
	}

	/**
	 * Creates a CheckinResponse with the given response code and user name.
	 * 
	 * @param responseCode
	 *            the response code
	 * @param userName
	 *            the user name
	 */
	public CheckinResponse(CheckinResponseCode responseCode, String userName) {
		this.responseCode = responseCode;
		this.userName = userName;
	}

	/**
	 * Returns the response code.
	 * 
	 * @return the response code
	 */
	public CheckinResponseCode getResponseCode() {
		return this.responseCode;
	}

	/**
	 * Returns the user name. May be null if not specified in response.
	 * 
	 * @return the user name if present, or else null
	 */
	public String getUserName() {
		return this.userName;
	}

}
