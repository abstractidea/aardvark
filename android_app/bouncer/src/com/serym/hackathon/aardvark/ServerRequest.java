package com.serym.hackathon.aardvark;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * CheckinRequest represents a check-in request to be sent to the server.
 */
public class ServerRequest {

	/**
	 * Tag for LogCat.
	 */
	private static final String TAG = "AARDVARK-BOUNCER-SERVERREQUEST";

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
	 * Private constructor to prevent instantiation.
	 */
	private ServerRequest() {
		// Intentionally empty
	}

	/**
	 * Sends the request to the server and returns the response. Must be run in
	 * a non-UI thread.
	 * 
	 * @return the response from the server
	 * @throws ServerRequestException
	 *             if the request cannot be carried out for any reason (e.g.
	 *             network errors)
	 */
	// TODO implement exponential backoff on failure
	public static String send(URL requestUrl, String requestBody)
			throws ServerRequestException {
		Log.d(TAG, "Request body: " + requestBody);

		byte[] requestBytes = requestBody.getBytes();
		String responseBody = null;
		HttpURLConnection conn = null;

		try {
			// Prepare request
			conn = (HttpURLConnection) requestUrl.openConnection();
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
				responseBody = new String(responseBytes, 0, numRead,
						RESPONSE_ENCODING);
			}

			responseStream.close();
		} catch (Exception e) {
			throw new ServerRequestException("Error sending request", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		Log.d(TAG, "Response body: " + responseBody);

		return responseBody;
	}

}
