package org.systemsbiology.gaggle.cereopsis.serialization;



import org.systemsbiology.gaggle.core.datatypes.Network;
import org.systemsbiology.gaggle.core.datatypes.Interaction;

import java.util.*;

/*
* Copyright (C) 2008 by Institute for Systems Biology,
* Seattle, Washington, USA.  All rights reserved.
*
* This source code is distributed under the GNU Lesser
* General Public License, the text of which is available at:
*   http://www.gnu.org/copyleft/lesser.html
*/

public class GaggleNetworkToCompactNetworkConverter {



    private Network gaggleNetwork;



    PairedListAndMap nodes;
    PairedListAndMap edges;
    PairedListAndMap verboseEdges;
    PairedListAndMap nodeAttributeNames;
    PairedListAndMap nodeAttributeValues;
    PairedListAndMap edgeAttributeNames;
    PairedListAndMap edgeAttributeValues;
    PairedListAndMap edgeTypes;

    Map<Integer,Map<Integer,Integer>> compressedNodeAttributes;
    Map<Integer,Map<Integer,Integer>> compressedEdgeAttributes;




    public GaggleNetworkToCompactNetworkConverter(Network gaggleNetwork) {
        this.gaggleNetwork = gaggleNetwork;
        nodes = new PairedListAndMap(gaggleNetwork.getNodes());

        nodeAttributeNames = new PairedListAndMap(gaggleNetwork.getNodeAttributeNames());
        edgeAttributeNames = new PairedListAndMap(gaggleNetwork.getEdgeAttributeNames());


        setUpEdgeTypes();
        setUpEdges();
        setUpNodeAttributes();
        setUpEdgeAttributes();
    }



    private void setUpEdges() {
        List<Object> tempEdgeList = new ArrayList<Object>();
        List<Object> tempVerboseEdgeList = new ArrayList<Object>();
        for (Interaction interaction : gaggleNetwork.getInteractions()) {
            int sourceIndex = nodes.getMap().get(interaction.getSource());
            int targetIndex = nodes.getMap().get(interaction.getTarget());
            int edgeTypeIndex = edgeTypes.getMap().get(interaction.getType());
            String directedness = (interaction.isDirected()) ? "d" : "u";
            String key = "" + sourceIndex + "," + targetIndex + "," + edgeTypeIndex + "," + directedness;
            String verboseEdge = interaction.getSource() + " (" + interaction.getType() + ") " + interaction.getTarget();
            tempEdgeList.add(key);
            tempVerboseEdgeList.add(verboseEdge);
        }
        edges = new PairedListAndMap(tempEdgeList);
        verboseEdges = new PairedListAndMap(tempVerboseEdgeList);
    }

    private void setUpEdgeAttributes() {
        Map<Object,Object> edgeAttrValueUniquifier = new HashMap<Object,Object>();
        for (Object edgeAttrName : gaggleNetwork.getEdgeAttributeNames()) {
            Map<String,Object> attrsForThisEdge = gaggleNetwork.getEdgeAttributes((String)edgeAttrName);
            for (String key : attrsForThisEdge.keySet()) {
                edgeAttrValueUniquifier.put(attrsForThisEdge.get(key),null);
            }
        }
        edgeAttributeValues = new PairedListAndMap(edgeAttrValueUniquifier.keySet().toArray());

        compressedEdgeAttributes = new HashMap<Integer,Map<Integer,Integer>>();
        for (Object edgeAttrName : gaggleNetwork.getEdgeAttributeNames()) {
            Map<String,Object> attrsForThisEdge = gaggleNetwork.getEdgeAttributes((String)edgeAttrName);
            Integer compressedAttributeName = edgeAttributeNames.getMap().get(edgeAttrName);
            Map<Integer,Integer> compressedAttrsForThisEdge = new HashMap<Integer,Integer>();
            for (String key : attrsForThisEdge.keySet()) {
                Integer compressedKey = verboseEdges.getMap().get(key);
                Object value = attrsForThisEdge.get(key);
                Integer compressedValue = edgeAttributeValues.getMap().get(value);
                compressedAttrsForThisEdge.put(compressedKey,compressedValue);
            }
            compressedEdgeAttributes.put(compressedAttributeName, compressedAttrsForThisEdge);
        }



    }

    private void setUpNodeAttributes() {
        /*
        old way of doing (node) attributes:
        a Map keyed by attribute name where the key is a Map<String,Object> mapping an edge name to an object

        new way:
        same thing except attribute name, key, and value are all indices

         */

        Map<Object,Object> nodeAttrValueUniquifier = new HashMap<Object,Object>();

        for (Object nodeAttrName : gaggleNetwork.getNodeAttributeNames()) {
            Map<String,Object> attrsForThisNode = gaggleNetwork.getNodeAttributes((String)nodeAttrName);
            for (String key : attrsForThisNode.keySet()) {
                nodeAttrValueUniquifier.put(attrsForThisNode.get(key),null);
            }
        }
        nodeAttributeValues = new PairedListAndMap(nodeAttrValueUniquifier.keySet().toArray());

        //now do it again
        compressedNodeAttributes = new HashMap<Integer,Map<Integer,Integer>>();
        for (Object nodeAttrName : gaggleNetwork.getNodeAttributeNames()) {
            Map<String,Object> attrsForThisNode = gaggleNetwork.getNodeAttributes((String)nodeAttrName);
            Integer compressedAttributeName = nodeAttributeNames.getMap().get(nodeAttrName);
            Map<Integer,Integer> compressedAttrsForThisNode = new HashMap<Integer,Integer>();
            for (String key : attrsForThisNode.keySet()) {
                Integer compressedKey = nodes.getMap().get(key);
                Object value = attrsForThisNode.get(key);
                Integer compressedValue = nodeAttributeValues.getMap().get(value);
                compressedAttrsForThisNode.put(compressedKey,compressedValue);
            }
            compressedNodeAttributes.put(compressedAttributeName,compressedAttrsForThisNode);
        }



    }

    private void setUpEdgeTypes() {
        Map<String,Object> uniquifier = new HashMap<String,Object>();
        for(Interaction interaction : gaggleNetwork.getInteractions()) {
            uniquifier.put(interaction.getType(), null);
        }
        Object[] listOfEdgeTypes = uniquifier.keySet().toArray();
        edgeTypes = new PairedListAndMap(listOfEdgeTypes);
    }




    public CompactNetwork toCompactNetwork() {
        CompactNetwork cn = new CompactNetwork();
        cn.setNodes(getNodes());
        cn.setEdges(getEdges());
        cn.setEdgeTypes(getEdgeTypes());
        cn.setNodeAttributes(getNodeAttributes());
        cn.setNodeAttrNames(getNodeAttrNames());
        cn.setNodeAttrValues(getNodeAttrValues());
        cn.setEdgeAttrNames(getEdgeAttrNames());
        cn.setEdgeAttrValues(getEdgeAttrValues());
        cn.setEdgeAttributes(getEdgeAttributes());
        cn.setName(gaggleNetwork.getName());
        cn.setSpecies(gaggleNetwork.getSpecies());
        return cn;
    }

    public String[] getNodes() {
        return gaggleNetwork.getNodes();
    }

    public String[] getEdges() {
        return edges.getList().toArray(new String[0]);
    }

    public String[] getEdgeTypes() {
        return edgeTypes.getList().toArray(new String[0]);
    }

    public Map<Integer,Map<Integer,Integer>> getNodeAttributes() {
        return compressedNodeAttributes;
    }

    public String[] getNodeAttrNames() {
        return nodeAttributeNames.getList().toArray(new String[0]);
    }

    public Object[] getNodeAttrValues() {
        return nodeAttributeValues.getList().toArray();
    }

    public String[] getEdgeAttrNames() {
        return edgeAttributeNames.getList().toArray(new String[0]);
    }

    public Object[] getEdgeAttrValues() {
        return edgeAttributeValues.getList().toArray();
    }

    public Map<Integer,Map<Integer,Integer>> getEdgeAttributes() {
        return compressedEdgeAttributes;
    }


}
