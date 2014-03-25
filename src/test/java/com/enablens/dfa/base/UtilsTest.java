/*
 * 
 */
package com.enablens.dfa.base;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.enablens.dfa.datastructures.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class UtilsTest.
 */
public class UtilsTest {

    String rawNodes;
    List<String> testResults1 = new LinkedList<String>();;
    Node[] testNodes;
    DcnmAuthToken dt = DcnmAuthTokenTest.getTestToken();

    /**
     * Before test.
     */
    @BeforeTest
    public void beforeTest() {
        byte[] encodedTestData = null;
        BufferedReader TestResultsReader;
        try {
            encodedTestData = Files.readAllBytes(Paths
                    .get("resources/UtilsTestData1.json"));
            TestResultsReader = Files.newBufferedReader(Paths
                    .get("resources/UtilsTestData2.txt"),
                    StandardCharsets.UTF_8);
            rawNodes = StandardCharsets.UTF_8.decode(
                    ByteBuffer.wrap(encodedTestData))
                    .toString();
            while (TestResultsReader.ready()) {
                testResults1.add(
                        TestResultsReader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        testNodes = Utils.getNodesFromJson(rawNodes);
    }

    /**
     * Creates the child.
     */
    @Test
    public void createNodes(Boolean... test) {
        for (Node n : testNodes) {
            Map<String, String> parentId = n.getParentId();
            Node resultNode = Utils.putNode(
                    dt, n);
            if (parentId != null && test[0]) {
                assert (resultNode.getId().equals(n.getId()));
            }
        }
    }

    /**
     * Delete operation.
     */
    @Test
    public void deleteNode() {
        createNodes(false);
        Utils.deleteNode(dt, testNodes[1].getId());
        Utils.deleteNode(dt, testNodes[2].getId());
        // Delete non-existent nodes:
        Utils.deleteNode(dt, testNodes[1].getId());
        Utils.deleteNode(dt, testNodes[2].getId());

    }

    /**
     * Gets the node.
     * 
     * @return the node
     */
    @Test
    public void getNode() {
        Utils.putNode(dt, testNodes[6]);
        Node nodeFromDcnm = Utils.getNode(dt, testNodes[6].getId());
        assertEquals(testNodes[6], nodeFromDcnm);
    }

    /**
     * Gets the node from json.
     * 
     * @return the node from json
     */
    @Test
    public void getNodeFromJson() {
        throw new RuntimeException("Test not implemented");
    }

    /**
     * Gets the nodes from json.
     * 
     * @return the nodes from json
     */
    @Test
    public void getNodesFromJson() {
        throw new RuntimeException("Test not implemented");
    }

    /**
     * Gets the operation.
     * 
     * @return the operation
     */
    @Test
    public void getOperation() {
        throw new RuntimeException("Test not implemented");
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    @Test
    public void getType() {
        throw new RuntimeException("Test not implemented");
    }

    /**
     * Id string builder.
     */
    @Test
    public void idStringBuilder() {
        for (int i = 0; i < testNodes.length; i++) {
            Node n = testNodes[i];
            String expected = testResults1.get(i);
            String result = Utils.idStringBuilder(n.getId());
            assertEquals(expected, result);
        }

    }

    /**
     * List children.
     */
    @Test
    public void listChildren() {
        throw new RuntimeException("Test not implemented");
    }

    /**
     * Main.
     */
    @Test
    public void main() {
        throw new RuntimeException("Test not implemented");
    }

    /**
     * Post operation.
     */
    @Test
    public void postOperation() {
        throw new RuntimeException("Test not implemented");
    }

    /**
     * Put operation.
     */
    @Test
    public void putOperation() {
        throw new RuntimeException("Test not implemented");
    }

    /**
     * Uri builder.
     */
    @Test
    public void uriBuilder() {
        throw new RuntimeException("Test not implemented");
    }
}
