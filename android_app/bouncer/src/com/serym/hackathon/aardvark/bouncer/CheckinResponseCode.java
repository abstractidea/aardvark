package com.serym.hackathon.aardvark.bouncer;

/**
 * CheckinResponseCode represents the possible status codes returned by the
 * server for a check-in response.
 */
public enum CheckinResponseCode {

	/**
	 * Check-in approved.
	 */
	APPROVED(100),

	/**
	 * Check-in rejected.
	 */
	REJECTED(200),

	/**
	 * Bad check-in request.
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
	private CheckinResponseCode(int code) {
		this.code = code;
	}

	/**
	 * Returns the CheckinResponseCode associated with the given integer code.
	 * 
	 * @param code
	 *            the integer code
	 * @return the associated CheckinResponseCode, or UNKNOWN if the integer
	 *         code is not known.
	 */
	public static CheckinResponseCode fromInt(int code) {
		for (CheckinResponseCode responseCode : CheckinResponseCode.values()) {
			if (responseCode.code == code) {
				return responseCode;
			}
		}
		return UNKNOWN;
	}

}
