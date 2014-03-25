/*
 * 
 */
package com.enablens.dfa.base;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enablens.dfa.datastructures.Node;
import com.enablens.dfa.datastructures.NodeType;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;

/**
 * The Class Utils.
 */
public final class Utils {

    /** The Debug Predicate */
    private static final boolean P_DEBUG = false;

    static {
        System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
        // change boolean to enable debug output
        if (P_DEBUG) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel",
                    "debug");
            System.setProperty(
                    "org.slf4j.simpleLogger.layout.ConversionPattern",
                    "%5p [%c] %m%n");
        }
    }

    /** The Constant log. */
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    /**
     * Instantiates a new utils.
     */
    private Utils() {
    }

    /**
     * Debug log.
     * 
     * @param logOutput
     *            the log output
     */
    private static void debugLog(final String logOutput) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                    "*** "
                            + Thread.currentThread().getStackTrace()[2]
                                    .getMethodName()
                            + " *** debug output: {}", logOutput);
        }
    }

    /**
     * Builds the id string.
     * 
     * @param identity
     *            the identity
     * @return the string
     */
    public static String idStringBuilder(final Map<String, String> identity) {

        StringBuilder id = new StringBuilder();

        if (identity.containsKey("topologyName")) {
            id.append("");
        }
        if (identity.containsKey("organizationName")) {
            id.append("/organizations/");
        }
        id.append(identity.get("organizationName"));

        if (identity.containsKey("partitionName")) {
            id.append("/partitions/");
            id.append(identity.get("partitionName"));
        }

        if (identity.containsKey("segmentId")) {
            id.append("/networks/segment/");
            id.append(identity.get("segmentId"));
        } else if (identity.containsKey("mobilityDomainId")
                && identity.containsKey("vlan")) {

            id.append("vlan/");
            id.append(identity.get("vlan"));

            id.append("/mobility-domain/");
            id.append(identity.get("mobilityDomainId"));
        }
        if (id.length() == 0) {
            return null;
        }
        debugLog(String.valueOf(id));
        return id.toString();
    }

    /**
     * Uri builder.
     * 
     * @param operation
     *            the operation
     * @param identity
     *            the identity
     * @return the string
     */
    public static String uriBuilder(final String operation,
            final Map<String, String> identity) {
        StringBuilder uri = new StringBuilder();
        uri.append("/rest/auto-config");
        String idString = idStringBuilder(identity);
        if (!("null".equals(idString))) {
            uri.append(idString);
        }
        if (operation.equals("list")) {
            uri.append("/");
            uri.append(Node.getType(identity).getChildType().toString()
                    .toLowerCase());
            uri.append("s?detail=true");
        }
        if (operation.equals("create")) {
            uri.append("/");
            uri.append(Node.getType(identity).getChildType().toString()
                    .toLowerCase());
            uri.append("s");
        }

        if (operation.equals("get")) {
        }

        if (operation.equals("delete")) {
        }
        debugLog(String.valueOf(uri));

        return uri.toString();
    }

    /**
     * List children.
     * 
     * @param dt
     *            the dt
     * @param parent
     *            the parent
     * @return the node[]
     */
    public static Node[] getChildArray(final DcnmAuthToken dt,
            final Node parent) {
        return getChildArray(dt, parent.getId());
    }

    public static Node[] getChildArray(final DcnmAuthToken dt,
            final Map<String, String> parentId) {
        String uri = uriBuilder("list", parentId);
        String nodesInJson = HttpOperations.getOperation(dt, uri).getBody();
        Node[] nodes = getNodesFromJson(nodesInJson);
        debugLog(Arrays.toString(nodes));
        return nodes;
    }

    /**
     * 
     * @param nodesAsJson
     *            array of Json Nodes
     * @return array of new Nodes
     */
    public static Node[] getNodesFromJson(final String nodesAsJson) {
        @SuppressWarnings("unchecked")
        Map<String, String>[] datas = new Gson().fromJson(nodesAsJson.trim(),
                HashMap[].class);
        Node[] nodes = new Node[datas.length];
        for (int i = 0; i < datas.length; i++) {
            nodes[i] = new Node(datas[i]);
        }
        debugLog(Arrays.toString(nodes));
        return nodes;
    }

    /**
     * Gets a node from a JSON object.
     * 
     * @param nodeAsJson
     *            Node as JSON
     * @return new Node
     */
    public static Node getNodeFromJson(final String nodeAsJson) {
        @SuppressWarnings("unchecked")
        Map<String, String> rawNode = new Gson().fromJson(nodeAsJson.trim(),
                HashMap.class);
        Node node = new Node(rawNode);
        LOG.debug(Thread.currentThread().getStackTrace()[1].getMethodName()
                + " debug output");
        LOG.debug(String.valueOf(node));
        return node;
    }

    /**
     * create a node on DCNM
     * 1. Takes a Node object - child and a nodeId
     * 2. creates the corresponding node on the DCNM, gets the node from the
     * DCNM
     * and returns that node.
     * 
     * @param dt
     *            the dt
     * @param parentId
     *            the parent id
     * @param node
     *            the child
     * @return true, if successful
     */
    public static Node putNode(final DcnmAuthToken dt, Node node) {
        Map<String, String> parentId = node.getParentId();
        if (parentId == null) {
            return null;
        }
        final String uri = uriBuilder("create", parentId);
        HttpOperations.postOperation(dt, uri, node.getDataAsJson());
        node = getNode(dt, node.getId());
        debugLog(String.valueOf(node));
        return node;

    }

    /**
     * Gets the node.
     * 
     * @param dt
     *            the dt
     * @param id
     *            the id
     * @return the node
     */
    public static Node getNode(final DcnmAuthToken dt,
            final Map<String, String> id) {
        Node node = null;
        final String uri = uriBuilder("get", id);
        final HttpResponse<String> response = HttpOperations.getOperation(dt,
                uri);
        final String nodeAsJson = response.getBody();
        if (response.getCode() == 200) {
            node = getNodeFromJson(nodeAsJson);
        }
        debugLog(String.valueOf(node));
        return node;

    }

    public static int deleteNode(DcnmAuthToken dt,
            Map<String, String> nodeId) {
        int responseCode;
        if (Node.getType(nodeId) != NodeType.NETWORK) {
            Node[] nodeChildren = getChildArray(dt, nodeId);
            for (Node nodeChild : nodeChildren) {
                deleteNode(dt, nodeChild.getId());
            }
        }

        String uri = uriBuilder("delete", nodeId);
        HttpResponse<String> response = HttpOperations.deleteOperation(dt, uri,
                "");
        responseCode = response.getCode();
        debugLog(String.valueOf(responseCode));
        return responseCode;
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */

    public static String nodeMd5(Node node) {
        String md5String = DigestUtils.md5Hex(node.getDataAsJson());
        debugLog(String.valueOf(md5String));
        return md5String;
    }

    public static void main(final String[] args) {
        DcnmAuthToken dt = new DcnmAuthToken("192.168.22.3", "admin",
                "Abcd1234", 10000L);
        Map<String, String> nodeData = new HashMap<String, String>();
        nodeData.put("organizationName", "Blue");
        nodeData.put("partitionName", "Data");
        Node[] nodes = getChildArray(dt, nodeData);
        for (Node n : nodes) {
            LOG.info(String.valueOf(n.getId()) + " : " +
                    nodeMd5(n));
        }
    }
}
