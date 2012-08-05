package com.serym.hackathon.aardvark.bouncer;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * CheckinRequest represents a check-in request to be sent to the server.
 */
public class CheckinRequest {

	/**
	 * Tag for LogCat.
	 */
	private static final String TAG = "AARDVARK-BOUNCER-CHECKINREQUEST";

	/**
	 * URI for check-in request.
	 */
	private static final String REQUEST_URI = "http://hackathon.serym.com/checkin";

	/**
	 * Name of JSON request token field.
	 */
	private static final String JSON_REQ_TOKEN = "token";

	/**
	 * Name of JSON request device registration id field.
	 */
	private static final String JSON_REQ_DEVICE_REG_ID = "device_registration_id";

	/**
	 * Name of JSON request event id field.
	 */
	private static final String JSON_REQ_EVENT_ID = "event_id";

	/**
	 * Name of JSON request bouncer id field.
	 */
	private static final String JSON_REQ_BOUNCER_ID = "bouncer_id";

	/**
	 * Name of JSON response status code field.
	 */
	private static final String JSON_RESP_STATUS_CODE = "status_code";

	/**
	 * Name of JSON response user name field.
	 */
	private static final String JSON_RESP_USER_NAME = "user_name";

	/**
	 * Prefix of all guest codes.
	 */
	private static final String GUEST_CODE_PREFIX = "AARDVARK";

	/**
	 * Length of the token field in a guest code.
	 */
	private static final int GUEST_CODE_TOKENLEN = 32;

	/**
	 * Minimum length of a valid guest code.
	 */
	private static final int GUEST_CODE_MINLEN = GUEST_CODE_PREFIX.length()
			+ GUEST_CODE_TOKENLEN;

	/**
	 * The token for this request.
	 */
	private String token;

	/**
	 * The device registration id for this request.
	 */
	private String deviceRegId;

	/**
	 * The bouncer id for this request.
	 */
	private String bouncerId;

	/**
	 * The event id for this request.
	 */
	private String eventId;

	/**
	 * Parses a guest code and creates a corresponding CheckinRequest. If the
	 * guest code is invalid, an IllegalArgumentException will be thrown.
	 * 
	 * @param qrCode
	 *            the contents of the guest QR code
	 * @param bouncerId
	 *            the bouncer id
	 * @param eventId
	 *            the event id
	 * @return a corresponding CheckinRequest
	 * @throws IllegalArgumentException
	 *             if the guest code is invalid
	 */
	public static CheckinRequest createFromCode(String qrCode,
			String bouncerId, String eventId) throws IllegalArgumentException {

		if (qrCode == null || !qrCode.startsWith(GUEST_CODE_PREFIX)
				|| qrCode.length() < GUEST_CODE_MINLEN) {
			throw new IllegalArgumentException("Invalid guest code: " + qrCode);
		}

		String token = qrCode.substring(GUEST_CODE_PREFIX.length(),
				GUEST_CODE_PREFIX.length() + GUEST_CODE_TOKENLEN);

		String deviceRegId = qrCode.substring(GUEST_CODE_PREFIX.length()
				+ GUEST_CODE_TOKENLEN);

		return new CheckinRequest(token, deviceRegId, bouncerId, eventId);
	}

	/**
	 * Creates a request with the given fields.
	 * 
	 * @param token
	 *            the token
	 * @param deviceRegId
	 *            the device registration id
	 * @param bouncerId
	 *            the bouncer id
	 * @param eventId
	 *            the event id
	 */
	public CheckinRequest(String token, String deviceRegId, String bouncerId,
			String eventId) {
		this.token = token;
		this.deviceRegId = deviceRegId;
		this.bouncerId = bouncerId;
		this.eventId = eventId;

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
	public CheckinResponse send() throws ServerRequestException {
		// Format as JSON
		String requestBody;
		try {
			requestBody = getRequestString(this.token, this.deviceRegId,
					this.bouncerId, this.eventId);
		} catch (JSONException e) {
			// Wrap exception
			throw new ServerRequestException("Error preparing request JSON", e);
		}

		URL requestUrl;
		try {
			requestUrl = new URL(REQUEST_URI);
		} catch (MalformedURLException e) {
			throw new ServerRequestException("Invalid URL: "
					+ REQUEST_URI, e);
		}

		// Send request
		String responseBody = ServerRequest.send(requestUrl, requestBody);

		// Parse JSON response
		CheckinResponse checkinResponse;
		try {
			checkinResponse = getCheckinResponse(responseBody);
		} catch (JSONException e) {
			// Wrap exception
			throw new ServerRequestException("Error parsing response JSON", e);
		}

		return checkinResponse;
	}

	/**
	 * Returns a JSON request string corresponding to the given fields.
	 * 
	 * @param token
	 *            the token
	 * @param deviceRegId
	 *            the device registration id
	 * @param bouncerId
	 *            the bouncer id
	 * @param eventId
	 *            the event id
	 * @return a corresponding JSON request string
	 * @throws JSONException
	 *             if there is a formatting error
	 */
	private static String getRequestString(String token, String deviceRegId,
			String bouncerId, String eventId) throws JSONException {

		JSONObject jsonRequest = new JSONObject();

		jsonRequest.put(JSON_REQ_TOKEN, token);
		jsonRequest.put(JSON_REQ_DEVICE_REG_ID, deviceRegId);
		jsonRequest.put(JSON_REQ_EVENT_ID, eventId);
		jsonRequest.put(JSON_REQ_BOUNCER_ID, bouncerId);

		return jsonRequest.toString();
	}

	/**
	 * Parses the JSON response and returns the corresponding CheckinResponse.
	 * 
	 * @param response
	 *            the response string from the server
	 * @return the corresponding CheckinResponse
	 * @throws JSONException
	 *             if the response cannot be parsed
	 */
	private static CheckinResponse getCheckinResponse(String response)
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
		String userName = null;
		if (jsonResponse.has(JSON_RESP_USER_NAME)) {
			userName = jsonResponse.optString(JSON_RESP_USER_NAME);
		}

		return new CheckinResponse(statusCode, userName);
	}

}
