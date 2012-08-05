package com.serym.hackathon.aardvark.bouncer;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * LoginRequest represents a login request to be sent to the server.
 */
public class LoginRequest {

	/**
	 * Tag for LogCat.
	 */
	private static final String TAG = "AARDVARK-BOUNCER-LOGINREQUEST";

	/**
	 * URI for login request.
	 */
	private static final String REQUEST_URI = "http://hackathon.serym.com/login";

	/**
	 * Name of JSON request user name field.
	 */
	private static final String JSON_REQ_USER_NAME = "user_name";

	/**
	 * Name of JSON request password field.
	 */
	private static final String JSON_REQ_PASSWORD = "password";

	/**
	 * Name of JSON response status code field.
	 */
	private static final String JSON_RESP_STATUS_CODE = "status_code";

	/**
	 * Name of JSON response token field.
	 */
	private static final String JSON_RESP_TOKEN = "token";

	/**
	 * The user name for this request.
	 */
	private String userName;

	/**
	 * The password for this request.
	 */
	private String password;

	/**
	 * Creates a request with the given fields.
	 * 
	 * @param userName
	 *            the user name
	 * @param password
	 *            the password
	 */
	public LoginRequest(String userName, String password) {
		this.userName = userName;
		this.password = password;

	}

	/**
	 * Sends the request to the server and returns the response. Must be run in
	 * a non-UI thread.
	 * 
	 * @return the response from the server
	 * @throws ServerRequestException
	 *             if the request cannot be carried out for any reason (e.g.
	 *             network errors).
	 */
	public LoginResponse send() throws ServerRequestException {
		// Format as JSON
		String requestBody;
		try {
			requestBody = getRequestString(this.userName, this.password);
		} catch (JSONException e) {
			// Wrap exception
			throw new ServerRequestException("Error preparing request JSON", e);
		}

		URL requestUrl;
		try {
			requestUrl = new URL(REQUEST_URI);
		} catch (MalformedURLException e) {
			throw new ServerRequestException("Invalid URL: " + REQUEST_URI, e);
		}

		// Send request
		String responseBody = ServerRequest.send(requestUrl, requestBody);

		// Parse JSON response
		LoginResponse loginResponse;
		try {
			loginResponse = getLoginResponse(responseBody);
		} catch (JSONException e) {
			// Wrap exception
			throw new ServerRequestException("Error parsing response JSON", e);
		}

		return loginResponse;
	}

	/**
	 * Returns a JSON request string corresponding to the given fields.
	 * 
	 * @param userName
	 *            the user name
	 * @param password
	 *            the password
	 * @throws JSONException
	 *             if there is a formatting error
	 */
	private static String getRequestString(String userName, String password)
			throws JSONException {

		JSONObject jsonRequest = new JSONObject();

		jsonRequest.put(JSON_REQ_USER_NAME, userName);
		jsonRequest.put(JSON_REQ_PASSWORD, password);

		return jsonRequest.toString();
	}

	/**
	 * Parses the JSON response and returns the corresponding LoginResponse.
	 * 
	 * @param response
	 *            the response string from the server
	 * @return the corresponding LoginResponse
	 * @throws JSONException
	 *             if the response cannot be parsed
	 */
	private static LoginResponse getLoginResponse(String response)
			throws JSONException {
		if (response == null || response.isEmpty()) {
			throw new JSONException("Empty response body");
		}

		// Parse as JSONObject
		JSONObject jsonResponse;
		Object nextValue = (new JSONTokener(response)).nextValue();
		if (nextValue instanceof JSONObject) {
			jsonResponse = (JSONObject) nextValue;
		} else {
			throw new JSONException("Expected JSONObject response but got "
					+ nextValue.getClass().getName());
		}

		// Get status code and convert to integer
		String stringCode = jsonResponse.getString(JSON_RESP_STATUS_CODE);
		int statusCode;
		try {
			statusCode = Integer.valueOf(stringCode);
		} catch (NumberFormatException e) {
			throw new JSONException("Non-numerical status code: " + stringCode);
		}

		// Get user name if exists
		String token = null;
		if (jsonResponse.has(JSON_RESP_TOKEN)) {
			token = jsonResponse.optString(JSON_RESP_TOKEN);
		}

		return new LoginResponse(statusCode, token);
	}

}
