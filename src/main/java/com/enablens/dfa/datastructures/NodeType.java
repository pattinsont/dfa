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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Enum NodeType.
 */
public enum NodeType {

    /** Topology node type. */
    TOPOLOGY(new String[]{"topologyName"}, new String[]{"description"}),

    /** Organization node type. */
    ORGANIZATION(new String[]{"organizationName"}, new String[]{"description",
            "orchestrationSource"}),

    /** Partition node type. */
    PARTITION(new String[]{"partitionName", "partitionSegmentId",
            "organizationName"}, new String[]{"description",
            "serviceNodeIpAddress"}),

    /** Network node type. */
    NETWORK(new String[]{"organizationName", "networkName", "segmentId",
            "vlanId", "mobilityDomainId", "profileName", "partitionName"},
            new String[]{"dvsId", "staticIpStart", "staticIpEnd",
                    "vSwitchControllerNetworkId", "description",
                    "vSwitchControllerId", "configArg", "dhcpScope"});

    /** The mandatory attributes. */
    private final List<String> mandatoryAttributes = new ArrayList<String>();

    /** The optional attributes. */
    private final List<String> optionalAttributes = new ArrayList<String>();

    /** All attributes. */
    private final List<String> allAttributes = new ArrayList<String>();

    /**
     * Instantiates a new node type.
     * 
     * @param mandatory
     *            the mandatory attributes
     * @param optional
     *            the optional attributes
     */
    private NodeType(final String[] mandatory, final String[] optional) {
        this.mandatoryAttributes.addAll(Arrays.asList(mandatory));
        this.optionalAttributes.addAll(Arrays.asList(optional));
        this.allAttributes.addAll(mandatoryAttributes);
        this.allAttributes.addAll(optionalAttributes);
        // All nodes have topology name but this is not defined in API, so
        // specified separately
        this.allAttributes.add("topologyName");
    }

    /**
     * Gets the mandatoryAttributes attributes.
     * 
     * @return mandatoryAttributes attributes
     */
    public List<String> getMandatoryAttributes() {
        return mandatoryAttributes;
    }

    /**
     * Gets the optionalAttributes attributes.
     * 
     * @return optionalAttributes attributes
     */
    public List<String> getOptionalAttributes() {
        return optionalAttributes;
    }

    /**
     * Gets all attributes.
     * 
     * @return all attributes
     */
    public List<String> getAllAttributes() {
        return allAttributes;
    }

    public List<String> getIdentityAttributes() {
        String[] identityAttributes;
        switch (this) {
        case TOPOLOGY:
            identityAttributes = new String[]{"topologyName"};
            break;
        case ORGANIZATION:
            identityAttributes = new String[]{"topologyName",
                    "organizationName"};
            break;
        case PARTITION:
            identityAttributes = new String[]{"topologyName",
                    "organizationName",
                    "partitionName"};
            break;
        default:
            identityAttributes = new String[]{"topologyName",
                    "organizationName",
                    "partitionName", "segmentId"};
            break;
        }
        return Arrays.asList(identityAttributes);
    }

    public NodeType getChildType() {
        NodeType childType;
        switch (this) {
        case TOPOLOGY:
            childType = ORGANIZATION;
            break;
        case ORGANIZATION:
            childType = PARTITION;
            break;
        case PARTITION:
            childType = NETWORK;
            break;
        case NETWORK:
            childType = null;
            break;
        default:
            childType = null;
            break;
        }
        return childType;
    }

    public NodeType getParentType() {
        NodeType parentType;
        switch (this) {
        case TOPOLOGY:
            parentType = null;
            break;
        case ORGANIZATION:
            parentType = TOPOLOGY;
            break;
        case PARTITION:
            parentType = ORGANIZATION;
            break;
        case NETWORK:
            parentType = PARTITION;
            break;
        default:
            parentType = null;
            break;
        }
        return parentType;
    }

    @Override
    public String toString() {
        return name();
    }

}
