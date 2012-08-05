package com.serym.hackathon.aardvark;

/**
 * LoginResponse represents the response from the server after a
 * {@link LoginRequest} has been made.
 */
public class LoginResponse {

	/**
	 * The response code.
	 */
	private LoginResponseCode responseCode;

	/**
	 * The token. May be null if not specified in response.
	 */
	private String token;

	/**
	 * Creates a LoginResponse with the given response code and no token.
	 * 
	 * @param responseCode
	 *            the response code
	 */
	public LoginResponse(int responseCode) {
		this.responseCode = LoginResponseCode.fromInt(responseCode);
		this.token = null;
	}

	/**
	 * Creates a LoginResponse with the given response code and no token.
	 * 
	 * @param responseCode
	 *            the response code
	 */
	public LoginResponse(LoginResponseCode responseCode) {
		this.responseCode = responseCode;
		this.token = null;
	}

	/**
	 * Creates a LoginResponse with the given response code and token.
	 * 
	 * @param responseCode
	 *            the response code
	 * @param token
	 *            the token
	 */
	public LoginResponse(int responseCode, String token) {
		this.responseCode = LoginResponseCode.fromInt(responseCode);
		this.token = token;
	}

	/**
	 * Creates a LoginResponse with the given response code and token.
	 * 
	 * @param responseCode
	 *            the response code
	 * @param token
	 *            the token
	 */
	public LoginResponse(LoginResponseCode responseCode, String token) {
		this.responseCode = responseCode;
		this.token = token;
	}

	/**
	 * Returns the response code.
	 * 
	 * @return the response code
	 */
	public LoginResponseCode getResponseCode() {
		return this.responseCode;
	}

	/**
	 * Returns the token. May be null if not specified in response.
	 * 
	 * @return the token if present, or else null
	 */
	public String getToken() {
		return this.token;
	}

}
