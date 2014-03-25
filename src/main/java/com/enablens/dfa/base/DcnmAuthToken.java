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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * The Class DcnmAuthToken.
 * 
 * @author Terry Pattinson <terry@enablens.com>
 * @since 2014/02/01
 */
public class DcnmAuthToken {

    /** Argument counter used in main(). */
    private static final int ARGCOUNT = 4;

    /**
     * LOG logging constant.
     */
    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(DcnmAuthToken.class);

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
    private transient DcnmResponseStates state = DcnmResponseStates.UNINITIALISED;

    /** Authentication Token. */
    private transient String token = "";

    /** Authentication token life. */
    private Long tokenLife = 0L;

    /** DCNM username. */
    private String username = "";

    /**
     * Instantiates a new dcnm auth token.
     * 
     * @param newServer
     *            the DCNM server.
     * @param newUsername
     *            the DCNM username.
     * @param newPassword
     *            the DCNM password for the provided username.
     * @param newTokenLife
     *            the token life in msec.
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
            state = DcnmResponseStates.NET_ERROR;
            return;
        }
        responseCode = response.getCode();
        if (responseCode == HTTP_OK) {
            final JsonParser parser = new JsonParser();
            final JsonObject jsonObject = parser.parse(
                    response.getBody().trim()).getAsJsonObject();
            if (jsonObject.has(DCNM_TOKEN_KEY)) {
                token = jsonObject.get(DCNM_TOKEN_KEY).getAsString();
                state = DcnmResponseStates.VALID;
            } else {
                // No token returned - DCNM returns {}
                // if credentials are incorrect
                token = "";
                state = DcnmResponseStates.SERVERSIDE_ERROR;
            }
        } else {
            // Not Response Code 200
            token = "";
            state = DcnmResponseStates.WEB_FAILURE;
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
    public final DcnmResponseStates getState() {
        refreshState();
        return state;
    }

    /**
     * Gets the token.
     * 
     * @return the token
     */
    public final String getToken() {
        refreshState();
        if (state != DcnmResponseStates.VALID) {
            authenticate();
        }
        return token;
    }

    /**
     * Refresh state.
     */
    private void refreshState() {
        if (state.compareTo(DcnmResponseStates.STALE) > -1) {
            if ("".equals(token)) {
                state = DcnmResponseStates.UNINITIALISED;
            } else if (authTime + tokenLife < System.currentTimeMillis()) {
                state = DcnmResponseStates.STALE;
                return;
            } else {
                state = DcnmResponseStates.VALID;
            }
        }
    }

    /**
     * Sets the password.
     * 
     * @param newPassword
     *            the new password
     */
    public final void setPassword(final String newPassword) {
        password = newPassword;
    }

    /**
     * Sets the server.
     * 
     * @param newServer
     *            the new server
     */
    public final void setServer(final String newServer) {
        server = newServer;
    }

    /**
     * Sets the token life.
     * 
     * @param newTokenLife
     *            the new token life
     */
    public final void setTokenLife(final Long newTokenLife) {
        tokenLife = newTokenLife;
    }

    /**
     * Sets the username.
     * 
     * @param newUsername
     *            the new username
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
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments. <Server> <Username> <Password> <Lifetime>
     * 
     */
    public static void main(final String[] args) {
        StringBuilder sb = new StringBuilder();
        if (args.length == ARGCOUNT) {
            int i = 0;
            String server = args[i++];
            String username = args[i++];
            String password = args[i++];
            Long lifetime = 0L;
            try {
                lifetime = Long.parseLong(args[i++]);
            } catch (NumberFormatException e) {
                System.out.println("The last argument must be a number");
                return;
            }
            DcnmAuthToken dt = new DcnmAuthToken(server, username, password,
                    lifetime);
            final String token = dt.getToken();
            if (dt.getState() == DcnmResponseStates.VALID) {
                sb.append("DCNM Authentication Token = ");
                sb.append(token);
            } else {
                sb.append("Token returned a state of ");
                sb.append(dt.getState());
            }
        } else {
            sb.append("Provide arguments in format of:\n");
            sb.append("Server Username Password Lifetime");
        }

        System.out.println(sb);
    }
}