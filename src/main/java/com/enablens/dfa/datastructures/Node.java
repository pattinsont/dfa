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
package com.enablens.dfa.datastructures;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * The Node class is the central data object used in this DFA interface toolset.
 */
public class Node {

    /** All data is held in the data map. */
    private Map<String, String> data;

    /** The type. */
    private NodeType type;

    /**
     * Instantiates a new node.
     */
    @SuppressWarnings("unused")
    private Node() {
    }

    /**
     * Instantiates a new node.
     * 
     * This also ensures that the data Map has
     * 1. all Mandatory Keys.
     * 2. only Mandatory and Optional Keys allowed for this node type.
     * 3. Identity values have values
     * 
     * @param type
     *            the type parameter determines which NodeType this node is.
     * 
     * @param newData
     *            the data hash determines what data is associated.
     */
    public Node(final Map<String, String> data) {
        this(data, "default");
    }

    public Node(final Map<String, String> data, String topology) {
        final Set<String> hashKeys = data.keySet();
        this.type = getType(data);
        this.data = data;
        this.data.put("topologyName", topology);
        final List<String> allAttributes = type.getAllAttributes();
        List<String> identityAttributes = getType().getIdentityAttributes();

        // Ensure that all mandatory keys are present in data
        for (final String mandatory : type.getMandatoryAttributes()) {
            if (!data.containsKey(mandatory)) {
                throw new IllegalArgumentException("Missing mandatory key");
            }
        }

        // Ensure that only mandatory and optional keys are present in data
        for (final String key : hashKeys) {
            if (!allAttributes.contains(key)) {
                throw new IllegalArgumentException(key
                        + " is not a Mandatory or Optional key.");
            }
        }

        // Ensure that identity keys have a value
        for (String idAttribute : identityAttributes) {
            final String id = data.get(idAttribute);
            if (id == null || id.length() == 0) {
                throw new IllegalArgumentException(idAttribute
                        + " is used for identity; cannot be blank.");
            }
        }
    }

    /**
     * Gets the type.
     * 
     * @param identity
     *            the identity
     * @return the type
     */
    public static NodeType getType(final Map<String, String> identity) {
        NodeType type = null;
        if (identity.containsKey("segmentId")
                || identity.containsKey("mobilityDomainId")) {
            type = NodeType.NETWORK;
        } else if (identity.containsKey("partitionName")) {
            type = NodeType.PARTITION;
        } else if (identity.containsKey("organizationName")) {
            type = NodeType.ORGANIZATION;
        } else {
            type = NodeType.TOPOLOGY;
        }
        return type;
    }

    /**
     * @return the data
     */
    public final Map<String, String> getData() {
        return data;
    }

    /**
     * Gets the data as json.
     * 
     * @return the data as json
     */
    public final String getDataAsJson() {
        return (new Gson().toJson(data));
    }

    /**
     * @param newData
     *            the data to set
     */
    public final void setData(final Map<String, String> newData) {
        this.data = newData;
    }

    /**
     * @return the type
     */
    public final NodeType getType() {
        return type;
    }

    /**
     * Gets the identity.
     * 
     * @return the identity
     */
    public final Map<String, String> getId() {
        return Maps.filterKeys(data,
                Predicates.in(type.getIdentityAttributes()));
    }

    /**
     * Gets the parent id.
     * 
     * @param topologyName
     *            the topology name
     * @return the parent id
     */
    public Map<String, String> getParentId() {
        if (type == NodeType.TOPOLOGY) {
            return null;
        }
        return Maps.filterKeys(data,
                Predicates.in(type.getParentType().getIdentityAttributes()));
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

}
