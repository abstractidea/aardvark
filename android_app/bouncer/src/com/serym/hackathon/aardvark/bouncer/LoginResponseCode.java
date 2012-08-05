package com.serym.hackathon.aardvark.bouncer;

/**
 * LoginResponseCode represents the possible status codes returned by the
 * server for a login response.
 */
public enum LoginResponseCode {

	/**
	 * Login approved.
	 */
	APPROVED(100),

	/**
	 * Login rejected.
	 */
	REJECTED(200),

	/**
	 * Bad login request.
	 */
	BAD_REQUEST(900),

	/**
	 * Unknown response code.
	 */
	UNKNOWN(-1);

	/**
	 * The associated integer response code.
	 */
	private int code;

	/**
	 * Set associated integer responses code.
	 * 
	 * @param code
	 *            the associated integer response code.
	 */
	private LoginResponseCode(int code) {
		this.code = code;
	}

	/**
	 * Returns the LoginResponseCode associated with the given integer code.
	 * 
	 * @param code
	 *            the integer code
	 * @return the associated LoginResponseCode, or UNKNOWN if the integer
	 *         code is not known.
	 */
	public static LoginResponseCode fromInt(int code) {
		for (LoginResponseCode responseCode : LoginResponseCode.values()) {
			if (responseCode.code == code) {
				return responseCode;
			}
		}
		return UNKNOWN;
	}

}
