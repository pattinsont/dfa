/*******************************************************************************
 * Copyright (c) 2014 Terry Pattinson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Terry - initial API and implementation
 ******************************************************************************/
package com.enablens.dfa.base;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.enablens.dfa.base.DcnmAuthToken;
import com.enablens.dfa.base.DcnmAuthToken.states;

/**
 * Dcnm Authentication Token Test Class.
 * 
 * @author Terry Pattinson <terry@enablens.com>
 * @version 0.1
 * @since 2014/02/01
 */
public class DcnmAuthTokenTest {

    /**
     * DCNM Authentication Token lifetime. Set to 1 second
     */
    private static final long LIFETIME = 1000L;

    /** DCNM Authentication Token Password. */
    private static final String PASSWORD = "Abcd1234";

    /**
     * DCNM server. FQDN or IP Address.
     */
    private static final String SERVER = "dcnm.test";

    /** DCNM Authentication Token Username. */
    private static final String USERNAME = "admin";

    /** The dt. */
    private DcnmAuthToken dt;

    /**
     * Dcnm connector. Used to establish a connection and request a token.
     * 
     * @param serv
     *            DCNM Server. Uses class-level field definition if null.
     * @param user
     *            DCNM Username. Uses class-level field definition if null.
     * @param pass
     *            DCNM Password. Uses class-level field definition if null.
     * @param life
     *            Lifetime of token. Uses class-level field definition if null.
     */
    private final void dcnmConnector(final String serv, final String user,
            final String pass, final Long life) {
        String server;
        String username;
        String password;
        Long lifetime;

        if (serv == null) {
            server = SERVER;
        } else {
            server = serv;
        }
        if (user == null) {
            username = USERNAME;
        } else {
            username = user;
        }
        if (pass == null) {
            password = PASSWORD;
        } else {
            password = pass;
        }
        if (life == null) {
            lifetime = LIFETIME;
        } else {
            lifetime = life;
        }
        dt = new DcnmAuthToken(server, username, password, lifetime);
        dt.getToken();
    }

    /**
     * Stale token generator. Sleep for lifetime of token (by definition),
     * before returning to calling method.
     */
    public void generateStaleToken() {
        final String serv = null;
        final String user = null;
        final String pass = null;
        final Long life = null;
        dcnmConnector(serv, user, pass, life);
        try {
            Thread.sleep(LIFETIME);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the server.
     *
     */
    @Test
    public void getServer() {
        final String serv = null, user = null, pass = null;
        final Long life = null;
        dcnmConnector(serv, user, pass, life);
        assertEquals(dt.getServer(), SERVER);
    }

    /**
     * Ensures that length of returned token string is > 0.
     * 
     */
    @Test
    public void getToken() {
        final String serv = null, user = null, pass = null;
        final Long life = null;
        dcnmConnector(serv, user, pass, life);
        assertEquals(dt.getState(), states.VALID);
        assertTrue(dt.getToken().length() > 0);
    }

    /**
     * Net fail token.
     */
    @Test
    public void netFailToken() {
        String serv = null;
        final String user = null, pass = null;
        final Long life = null;
        serv = "dcnm.test1";
        dcnmConnector(serv, user, pass, life);
        assertEquals(dt.getState(), states.NET_ERROR);
    }

    /**
     * Refresh state.
     */
    @Test
    public void refreshState() {
        generateStaleToken();
        dt.getToken();
        assertEquals(dt.getState(), states.VALID);
    }

    /**
     * Stale token.
     */
    @Test
    public final void staleToken() {
        generateStaleToken();
        assertEquals(dt.getState(), states.STALE);
    }

    /**
     * User auth fail token.
     */
    @Test
    public void userAuthFailToken() {
        final String serv = null;
        String user = null;
        final String pass = null;
        final Long life = null;
        user = "BIGBARRY";
        dcnmConnector(serv, user, pass, life);
        assertEquals(dt.getState(), states.SERVERSIDE_ERROR);
    }

    /**
     * Valid token.
     */
    @Test
    public final void validToken() {
        final String serv = null, user = null, pass = null;
        final Long life = null;
        dcnmConnector(serv, user, pass, life);
        assertEquals(dt.getState(), states.VALID);
    }

}
