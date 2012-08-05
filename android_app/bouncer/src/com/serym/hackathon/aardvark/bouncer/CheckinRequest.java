package com.serym.hackathon.aardvark.bouncer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

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
	private static final String CHECKIN_REQUEST_URI = "http://hackathon.serym.com/checkin";

	/**
	 * HTTP response code OK.
	 */
	private static final int HTTP_STATUS_OK = 200;

	/**
	 * Mimetype for JSON messages.
	 */
	private static final String MIMETYPE_JSON = "application/json";

	/**
	 * The content encoding used for request message.
	 */
	private static final String REQUEST_ENCODING = "UTF-8";

	/**
	 * The content length to assume if not specified in the response.
	 */
	private static final int DEFAULT_CONTENT_LENGTH = 1024;

	/**
	 * The content encoding used to read the response message.
	 */
	private static final String RESPONSE_ENCODING = "UTF-8";

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
			+ GUEST_CODE_TOKENLEN + 1;

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
	 * guest code is invalid, a CheckinException will be thrown.
	 * 
	 * @param qrCode
	 *            contents of the guest QR code
	 * @param bouncerId
	 *            the bouncer id
	 * @param eventId
	 *            the event id
	 * @return a corresponding CheckinRequest
	 * @throws CheckinException
	 *             if the guest code is invalid
	 */
	public static CheckinRequest createFromCode(String qrCode,
			String bouncerId, String eventId) throws CheckinException {

		if (qrCode == null || !qrCode.startsWith(GUEST_CODE_PREFIX)
				|| qrCode.length() < GUEST_CODE_MINLEN) {
			throw new CheckinException("Invalid guest code: " + qrCode);
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
	 *            token
	 * @param deviceRegId
	 *            device registration id
	 * @param bouncerId
	 *            bouncer id
	 * 
	 * @param eventId
	 *            event id
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
	 * @throws CheckinException
	 *             if the request cannot be carried out for any reason (e.g.
	 *             network errors).
	 */
	// TODO implement exponential backoff on failure
	public CheckinResponse send() throws CheckinException {
		// Format as JSON
		String strRequest;
		try {
			strRequest = getRequestString(this.token, this.deviceRegId,
					this.bouncerId, this.eventId);
		} catch (JSONException e) {
			// Wrap exception
			throw new CheckinException("Error preparing request JSON", e);
		}

		Log.d(TAG, "Request body: " + strRequest);

		URL url;
		try {
			url = new URL(CHECKIN_REQUEST_URI);
		} catch (MalformedURLException e) {
			throw new CheckinException("Invalid URL: " + CHECKIN_REQUEST_URI, e);
		}

		byte[] requestBytes = strRequest.getBytes();
		String strResponse = null;
		HttpURLConnection conn = null;

		try {
			// Prepare request
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(requestBytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", MIMETYPE_JSON + ";charset="
					+ REQUEST_ENCODING);

			// Post the request
			OutputStream out = conn.getOutputStream();
			out.write(requestBytes);
			out.close();

			// Check the response code
			int status = conn.getResponseCode();
			if (status != HTTP_STATUS_OK) {
				throw new IOException("POST failed with error code " + status);
			}

			InputStream responseStream = conn.getInputStream();

			int contentLen;
			if (conn.getContentLength() >= 0) {
				contentLen = conn.getContentLength();
			} else {
				Log.w(TAG,
						"Content length not specified in response header; using default value of "
								+ DEFAULT_CONTENT_LENGTH);
				contentLen = DEFAULT_CONTENT_LENGTH;
			}

			byte[] responseBytes = new byte[contentLen];
			int numRead = responseStream.read(responseBytes, 0, contentLen);
			if (numRead >= 0) {
				strResponse = new String(responseBytes, 0, numRead,
						RESPONSE_ENCODING);
			}

			responseStream.close();
		} catch (Exception e) {
			throw new CheckinException("Error sending request", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		Log.d(TAG, "Response body: " + strResponse);

		// Parse JSON response
		CheckinResponse checkinResponse;
		try {
			checkinResponse = getCheckinResponse(strResponse);
		} catch (JSONException e) {
			// Wrap exception
			throw new CheckinException("Error parsing response JSON", e);
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
