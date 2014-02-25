/*******************************************************************************
 * Copyright (c) 2014 Terry Pattinson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Terry Pattinson - initial API and implementation
 ******************************************************************************/
package com.enablens.dfa.base;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * DCNM Authentication Token Class.
 *
 * @author Terry Pattinson <terry@enablens.com>
 * @version 0.1
 * @since 2014/02/01
 */
public class DcnmAuthToken {

    /** DCNM Token Key constant used in JSON parsing. */
    private static final String DCNM_TOKEN_KEY = "Dcnm-Token";

    /** OK HTTP Response Code. */
    private static final int HTTP_OK = 200;

    /** Token authentication time. */
    private Long authTime = -1L;

    /** DCNM account password. */
    private String password = "";

    /** DCNM server. Can be FQDN or IP Address */
    private String server = "";

    /** State of the DCNM Authentication Token. */
    private transient states state = states.UNINITIALISED;

    /** Authentication Token. */
    private transient String token = "";

    /** Authentication token life. */
    private Long tokenLife = 0L;

    /** DCNM username. */
    private String username = "";

    /**
     * Token state.
     */
    public enum states {

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

    /**
     * Instantiates a new dcnm auth token.
     *
     * @param newServer the DCNM server.
     * @param newUsername the DCNM username.
     * @param newPassword the DCNM password for the provided username.
     * @param newTokenLife the token life in msec.
     */
    public DcnmAuthToken(final String newServer, final String newUsername,
            final String newPassword, final Long newTokenLife) {
        super();
        server = newServer;
        username = newUsername;
        password = newPassword;
        tokenLife = newTokenLife;
    }

    /**
     * Authenticate.
     */
    private void authenticate() {
        authTime = System.currentTimeMillis();
        HttpResponse<String> response;
        int responseCode;
        try {
            response = Unirest.post("http://" + server + "/rest/logon/")
                    .basicAuth(username, password)
                    .body("{expiration: " + tokenLife + "}").asString();
            responseCode = response.getCode();
        } catch (final UnirestException e) {
            token = "";
            state = states.NET_ERROR;
            return;
        }
        responseCode = response.getCode();
        if (responseCode == HTTP_OK) {
            final JsonParser parser = new JsonParser();
            final JsonObject jsonObject = parser.parse(
                    response.getBody().trim()).getAsJsonObject();
            if (jsonObject.has(DCNM_TOKEN_KEY)) {
                token = jsonObject.get(DCNM_TOKEN_KEY).getAsString();
                state = states.VALID;
            } else {
                // No token returned - DCNM returns {}
                // if credentials are incorrect
                token = "";
                state = states.SERVERSIDE_ERROR;
            }
        } else {
            // Not Response Code 200
            token = "";
            state = states.WEB_FAILURE;
        }

    }

    /**
     * Gets the server.
     *
     * @return the server
     */
    public final String getServer() {
        return server;
    }

    /**
     * Gets the state.
     *
     * @return the Authentication Token state
     */
    public final states getState() {
        refreshState();
        return state;
    }

    /**
     * Gets the token.
     *
     * @return the token value
     */
    public final String getToken() {
        refreshState();
        if (state != states.VALID)
            authenticate();
        return token;
    }

    /**
     * Refresh state.
     */
    private void refreshState() {
        if (state.compareTo(states.STALE) > -1) {
            if ("".equals(token)) {
                state = states.UNINITIALISED;
            } else if (authTime + tokenLife < System.currentTimeMillis()) {
                state = states.STALE;
                return;
            } else {
                state = states.VALID;
            }
        }
    }

    /**
     * Sets the password.
     *
     * @param newPassword the new password
     */
    public final void setPassword(final String newPassword) {
        password = newPassword;
    }

    /**
     * Sets the server.
     *
     * @param newServer the new server
     */
    public final void setServer(final String newServer) {
        server = newServer;
    }

    /**
     * Sets the token life.
     *
     * @param newTokenLife the new token life
     */
    public final void setTokenLife(final Long newTokenLife) {
        tokenLife = newTokenLife;
    }

    /**
     * Sets the username.
     *
     * @param newUsername the new username
     */
    public final void setUsername(final String newUsername) {
        username = newUsername;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (authTime == null ? 0 : authTime.hashCode());
        result = prime * result + (password == null ? 0 : password.hashCode());
        result = prime * result + (server == null ? 0 : server.hashCode());
        result = prime * result
                + (tokenLife == null ? 0 : tokenLife.hashCode());
        result = prime * result + (username == null ? 0 : username.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DcnmAuthToken)) {
            return false;
        }
        final DcnmAuthToken other = (DcnmAuthToken) obj;
        if (authTime == null) {
            if (other.authTime != null) {
                return false;
            }
        } else if (!authTime.equals(other.authTime)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (server == null) {
            if (other.server != null) {
                return false;
            }
        } else if (!server.equals(other.server)) {
            return false;
        }
        if (tokenLife == null) {
            if (other.tokenLife != null) {
                return false;
            }
        } else if (!tokenLife.equals(other.tokenLife)) {
            return false;
        }
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        StringBuilder s = new StringBuilder();
        if (getState() == states.VALID) {
        s.append("DCNM Authentication Token = ");
        s.append(getToken());
        } else {
            s.append("Token state is ");
            s.append(getState());
        }
        return s.toString();
    }

    public static void main(String[] args) {
        if (args.length == 4) {
            String server = args[0];
            String username = args[1];
            String password = args[2];
            Long lifetime = 0L;
            try {
                lifetime = Long.parseLong(args[3]);
            } catch (NumberFormatException e) {
                System.out.println("The last argument must be a number");
            }
            DcnmAuthToken dt = new DcnmAuthToken(server, username, password, lifetime);
            dt.getToken();
            System.out.println(dt);
        } else { System.out.println("Provide arguments in format of:\n" +
                "Server Username Password Lifetime");
        }
    }
}