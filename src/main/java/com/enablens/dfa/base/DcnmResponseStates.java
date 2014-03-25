package com.enablens.dfa.base;

/**
 * Token state.
 */
public enum DcnmResponseStates {

    /**
     * Internal Error - if the JSON response is unparseable.
     */
    INTERNAL_ERROR,

    /**
     * Network Error - if the RESTful library returns an error. Commmonly
     * results from a DNS timeout (incorrectly specified FQDN).
     */
    NET_ERROR,

    /**
     * The server side error. HTTP response is valid but doesn't include
     * JSON 2Dcnm-Token". For example, an authentication error results in
     * this error code.
     */
    SERVERSIDE_ERROR,

    /** The stale. */
    STALE,

    /** The uninitialised. */
    UNINITIALISED,

    /** The valid. */
    VALID,

    /**
     * Webfailure - state if anything other than 200 is returned.
     */
    WEB_FAILURE
}