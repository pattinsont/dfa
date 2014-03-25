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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.testng.annotations.Test;

/**
 * Dcnm Authentication Token Test Class.
 * 
 * @author Terry Pattinson <terry@enablens.com>
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
     * Dcnm connector. Used to establish a connection and request a token using
     * default values.
     */

    public static DcnmAuthToken getTestToken() {
        return new DcnmAuthToken(SERVER, USERNAME, PASSWORD, LIFETIME);
    }

    private void dcnmConnector() {
        // Use default srv / usr / pass / life values
        dcnmConnector(null, null, null, null);
    }

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
    private void dcnmConnector(final String serv, final String user,
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
    public final void generateStaleToken() {
        dcnmConnector();
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
    public final void getServer() {
        dcnmConnector();
        assertEquals(dt.getServer(), SERVER);
    }

    /**
     * Ensures that length of returned token string is > 0.
     * 
     */
    @Test
    public final void getToken() {
        dcnmConnector();
        assertEquals(dt.getState(), DcnmResponseStates.VALID);
        assertTrue(dt.getToken().length() > 0);
    }

    /**
     * Net fail token.
     */
    @Test
    public final void netFailToken() {
        final String serv = "dcnm.test1";
        ;
        final String user = null;
        final String pass = null;
        final Long life = null;
        dcnmConnector(serv, user, pass, life);
        assertEquals(dt.getState(), DcnmResponseStates.NET_ERROR);
    }

    /**
     * Refresh state.
     */
    @Test
    public final void refreshState() {
        generateStaleToken();
        dt.getToken();
        assertEquals(dt.getState(), DcnmResponseStates.VALID);
    }

    /**
     * Stale token.
     */
    @Test
    public final void staleToken() {
        generateStaleToken();
        assertEquals(dt.getState(), DcnmResponseStates.STALE);
    }

    /**
     * User auth fail token.
     */
    @Test
    public final void userAuthFailToken() {
        final String serv = null;
        final String user = "BIGBARRY";
        final String pass = null;
        final Long life = null;
        dcnmConnector(serv, user, pass, life);
        assertEquals(dt.getState(), DcnmResponseStates.SERVERSIDE_ERROR);
    }

    /**
     * Valid token.
     */
    @Test
    public final void validToken() {
        dcnmConnector();
        assertEquals(dt.getState(), DcnmResponseStates.VALID);
    }

    /**
     * toString.
     */
    @Test
    public final void toStringTest() {
        dcnmConnector();
        System.out.println(dt);
    }

    /**
     * Main test - Successful Token generation.
     */
    @Test
    public final void mainTestCorrect() {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        DcnmAuthToken.main(new String[]{SERVER, USERNAME, PASSWORD,
                String.valueOf(LIFETIME)});
        assertTrue(outContent.toString().startsWith(
                "DCNM Authentication Token = "));
    }

    /**
     * Main test - IncorrectrAgs
     */
    @Test
    public final void mainTestIncorrectAgs() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Provide arguments in format of:\n");
        sb.append("Server Username Password Lifetime");
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        DcnmAuthToken.main(new String[]{SERVER, USERNAME, PASSWORD});
        assert (outContent.toString().trim().equals(sb.toString()));
    }

    /**
     * Main test - Nonumber
     */
    @Test
    public final void mainTestNoNumber() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("The last argument must be a number");
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        DcnmAuthToken.main(new String[]{SERVER, USERNAME, PASSWORD, "ten"});
        assert (outContent.toString().trim().equals(sb.toString()));
    }
}
