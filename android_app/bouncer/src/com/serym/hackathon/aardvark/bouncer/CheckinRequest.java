package com.serym.hackathon.aardvark.bouncer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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
	 * Mimetype for JSON messages.
	 */
	private static final String MIMETYPE_JSON = "application/json";

	/**
	 * HTTP Accept header field name.
	 */
	private static final String HEADER_ACCEPT = "Accept";

	/**
	 * HTTP Content-type field name.
	 */
	private static final String HEADER_CONTENT_TYPE = "Content-type";

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

		// Prepare request
		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpPost httpPost = new HttpPost(CHECKIN_REQUEST_URI);
		httpPost.setHeader(HEADER_ACCEPT, MIMETYPE_JSON);
		httpPost.setHeader(HEADER_CONTENT_TYPE, MIMETYPE_JSON);

		HttpEntity requestEntity = null;
		try {
			requestEntity = new StringEntity(strRequest, REQUEST_ENCODING);
		} catch (UnsupportedEncodingException e) {
			// Wrap exception
			throw new CheckinException("Unsupported encoding in request", e);
		}
		httpPost.setEntity(requestEntity);

		// Send request
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			// Wrap exception
			throw new CheckinException("HTTP client protocol error in request",
					e);
		} catch (IOException e) {
			// Wrap exception
			throw new CheckinException("I/O error during request", e);
		}

		// Read response
		HttpEntity responseEntity = httpResponse.getEntity();
		int responseLen = (responseEntity.getContentLength() < Integer.MAX_VALUE) ? (int) responseEntity
				.getContentLength() : Integer.MAX_VALUE;
		byte[] responseBytes = new byte[responseLen];
		try {
			responseEntity.getContent().read(responseBytes, 0, responseLen);
			responseEntity.consumeContent();
		} catch (IOException e) {
			// Wrap exception
			throw new CheckinException("I/O error reading response", e);
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

		CheckinResponse response = new CheckinResponse(strResponse);

		return response;
	}
}
