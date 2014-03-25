/*
 * 
 */
package com.enablens.dfa.datastructures;

import java.util.Map;

/**
 * The Class NodeIdentity.
 */
public class NodeIdentity {

    /** The type. */
    private NodeType type;

    /** The identity. */
    private Map<String, String> identity;

    /**
     * Gets the identity.
     * 
     * @return the identity
     */
    public final Map<String, String> getIdentity() {
        return identity;
    }

    /**
     * Sets the identity.
     * 
     * @param identity
     *            the new identity
     */
    public final void setIdentity(final Map<String, String> identity) {
        this.identity = identity;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public final NodeType getType() {
        return type;
    }

    /**
     * Sets the type.
     * 
     * @param type
     *            the new type
     */
    public final void setType(final NodeType type) {
        this.type = type;
    }

}
