package org.systemsbiology.gaggle.cereopsis.serialization;

import java.util.Map;

/*
* Copyright (C) 2008 by Institute for Systems Biology,
* Seattle, Washington, USA.  All rights reserved.
*
* This source code is distributed under the GNU Lesser
* General Public License, the text of which is available at:
*   http://www.gnu.org/copyleft/lesser.html
*/


/**
 * Use the CompactNetworkSerializer to serialize/deserialize this class -- don't do it directly with jsonlib --
 * otherwise you will run into some bugs with jsonlib.
 */
public class CompactNetwork {

    private String[] nodes;
    private String[] edges;
    private String[] edgeTypes;
    private Map<Integer, Map<Integer,Integer>> nodeAttributes;
    private String[] nodeAttrNames;
    private Object[] nodeAttrValues;
    private String[] edgeAttrNames;
    private Object[] edgeAttrValues;
    private Map<Integer,Map<Integer,Integer>> edgeAttributes;

    private String name;
    private String species;

    /*
    private Tuple metadata;
    */


    public CompactNetwork() {

    }


    public String[] getNodes() {
        return nodes;
    }

    public void setNodes(String[] nodes) {
        this.nodes = nodes;
    }

    public String[] getEdges() {
        return edges;
    }

    public void setEdges(String[] edges) {
        this.edges = edges;
    }

    public String[] getEdgeTypes() {
        return edgeTypes;
    }

    public void setEdgeTypes(String[] edgeTypes) {
        this.edgeTypes = edgeTypes;
    }

    public Map<Integer, Map<Integer, Integer>> getNodeAttributes() {
        return nodeAttributes;
    }

    public void setNodeAttributes(Map<Integer, Map<Integer, Integer>> nodeAttributes) {
        this.nodeAttributes = nodeAttributes;
    }

    public String[] getNodeAttrNames() {
        return nodeAttrNames;
    }

    public void setNodeAttrNames(String[] nodeAttrNames) {
        this.nodeAttrNames = nodeAttrNames;
    }

    public Object[] getNodeAttrValues() {
        return nodeAttrValues;
    }

    public void setNodeAttrValues(Object[] nodeAttrValues) {
        this.nodeAttrValues = nodeAttrValues;
    }

    public String[] getEdgeAttrNames() {
        return edgeAttrNames;
    }

    public void setEdgeAttrNames(String[] edgeAttrNames) {
        this.edgeAttrNames = edgeAttrNames;
    }

    public Object[] getEdgeAttrValues() {
        return edgeAttrValues;
    }

    public void setEdgeAttrValues(Object[] edgeAttrValues) {
        this.edgeAttrValues = edgeAttrValues;
    }

    public Map<Integer, Map<Integer, Integer>> getEdgeAttributes() {
        return edgeAttributes;
    }

    public void setEdgeAttributes(Map<Integer, Map<Integer, Integer>> edgeAttributes) {
        this.edgeAttributes = edgeAttributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }
}
