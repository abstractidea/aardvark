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
	private static final String CHECKIN_REQUEST_URI = "http://hackathon.serym.com/?checkin";

	/**
	 * HTTP response code OK.
	 */
	private static final int HTTP_STATUS_OK = 200;

	/**
	 * Mimetype for JSON messages.
	 */
	private static final String MIMETYPE_JSON = "application/json";

	/**
	 * Encoding used for request message.
	 */
	private static final String REQUEST_ENCODING = "UTF-8";

	/**
	 * Name of JSON bouncer id field.
	 */
	private static final String JSON_BOUNCER_ID = "bouncer_id";

	/**
	 * Name of JSON device registration id field.
	 */
	private static final String JSON_DEVICE_REG_ID = "device_registration_id";

	/**
	 * Name of JSON event id field.
	 */
	private static final String JSON_EVENT_ID = "event_id";

	/**
	 * Name of JSON user id field.
	 */
	private static final String JSON_USER_ID = "user_id";

	/**
	 * Name of JSON server MAC field.
	 */
	private static final String JSON_MAC_SERVER = "mac_server";

	/**
	 * Name of JSON client MAC field.
	 */
	private static final String JSON_MAC_CLIENT = "mac_client";

	private String bouncerId;

	private String deviceRegId;

	private String eventId;

	private String userId;

	private String macServer;

	private String macClient;

	/**
	 * Creates a request with the given fields.
	 * 
	 * @param bouncerId
	 *            bouncer id
	 * @param deviceRegId
	 *            device registration id
	 * @param eventId
	 *            event id
	 * @param userId
	 *            user id
	 * @param macServer
	 *            server MAC
	 * @param macClient
	 *            client MAC
	 */
	public CheckinRequest(String bouncerId, String deviceRegId, String eventId,
			String userId, String macServer, String macClient) {
		this.bouncerId = bouncerId;
		this.deviceRegId = deviceRegId;
		this.eventId = eventId;
		this.userId = userId;
		this.macServer = macServer;
		this.macClient = macClient;
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
		JSONObject jsonRequest = new JSONObject();
		try {
			jsonRequest.put(JSON_BOUNCER_ID, this.bouncerId);
			jsonRequest.put(JSON_DEVICE_REG_ID, this.deviceRegId);
			jsonRequest.put(JSON_EVENT_ID, this.eventId);
			jsonRequest.put(JSON_USER_ID, this.userId);
			jsonRequest.put(JSON_MAC_SERVER, this.macServer);
			jsonRequest.put(JSON_MAC_CLIENT, this.macClient);
		} catch (JSONException e) {
			// Wrap exception
			throw new CheckinException("Error preparing request JSON", e);
		}
		String strRequest = jsonRequest.toString();

		Log.d(TAG, "Request body: " + strRequest);

		URL url;
		try {
			url = new URL(CHECKIN_REQUEST_URI);
		} catch (MalformedURLException e) {
			throw new CheckinException("Invalid URL: " + CHECKIN_REQUEST_URI, e);
		}

		byte[] requestBytes = strRequest.getBytes();
		byte[] responseBytes = null;
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
			responseBytes = new byte[conn.getContentLength()];
			responseStream.read(responseBytes, 0, conn.getContentLength());
			responseStream.close();
		} catch (Exception e) {
			throw new CheckinException("Error sending request", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		String strResponse = new String(responseBytes);

		Log.d(TAG, "Response body: " + strResponse);

		if (strResponse.isEmpty()) {
			throw new CheckinException("Empty response body");
		}

		JSONObject jsonResponse = null;
		try {
			jsonResponse = (JSONObject) (new JSONTokener(strResponse))
					.nextValue();
		} catch (JSONException e) {
			// Wrap exception
			throw new CheckinException("Error parsing response JSON", e);
		}

		// TODO
		// Once we get response format, construct CheckinResponse
		// from JSON fields.

		CheckinResponse response = new CheckinResponse(strResponse);

		return response;
	}

}
