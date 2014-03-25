package com.enablens.dfa.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class HttpOperations {
    static {
        // change boolean to enable debug output
        boolean pDebug = false;
        if (pDebug) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel",
                    "debug");
            System.setProperty(
                    "org.slf4j.simpleLogger.layout.ConversionPattern",
                    "%5p [%c] %m%n");
        }
        System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
    }

    private static final Logger LOG = LoggerFactory
            .getLogger(HttpOperations.class);

    private static void debugLog(HttpResponse<String> response) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                    "*** "
                            + Thread.currentThread().getStackTrace()[2]
                                    .getMethodName()
                            + " *** debug output: {}", response.getCode());
        }
    }

    /**
     * Gets the operation.
     * 
     * @param dt
     *            the dt
     * @param uri
     *            the uri
     * @return the operation
     */
    public static HttpResponse<String> getOperation(final DcnmAuthToken dt,
            final String uri) {
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("http://" + dt.getServer() + uri)
                    .header("Dcnm-Token", dt.getToken()).asString();
        } catch (UnirestException e) {
            return null;
        }
        debugLog(response);
        return response;
    }

    /**
     * Delete operation.
     * 
     * @param dt
     *            the dt
     * @param uri
     *            the uri
     * @param body
     *            the body
     * @return the http response
     */
    public static HttpResponse<String> deleteOperation(DcnmAuthToken dt,
            String uri, String body) {
        HttpResponse<String> response = null;
        try {
            response = Unirest.delete("http://" + dt.getServer() + uri)
                    .header("Dcnm-Token", dt.getToken())
                    .header("Content-Type", "application/json").body(body)
                    .asString();
        } catch (UnirestException e) {
            return null;
        }
        debugLog(response);
        return response;
    }

    /**
     * Put operation.
     * 
     * @param dt
     *            the dt
     * @param uri
     *            the uri
     * @param body
     *            the body
     * @return the http response
     */
    public static HttpResponse<String> putOperation(DcnmAuthToken dt,
            String uri, String body) {
        HttpResponse<String> response = null;
        try {
            response = Unirest.put("http://" + dt.getServer() + uri)
                    .header("Dcnm-Token", dt.getToken())
                    .header("Content-Type", "application/json").body(body)
                    .asString();
        } catch (UnirestException e) {
            return null;
        }
        debugLog(response);
        return response;
    }

    /**
     * Post operation.
     * 
     * @param dt
     *            the dt
     * @param uri
     *            the uri
     * @param body
     *            the body
     * @return the http response
     */
    public static HttpResponse<String> postOperation(DcnmAuthToken dt,
            String uri, String body) {
        HttpResponse<String> response = null;
        String url = "http://" + dt.getServer() + uri;

        try {
            response = Unirest.post(url)
                    .header("Dcnm-Token", dt.getToken())
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asString();
        } catch (UnirestException e) {
        }
        debugLog(response);
        return response;
    }

}
