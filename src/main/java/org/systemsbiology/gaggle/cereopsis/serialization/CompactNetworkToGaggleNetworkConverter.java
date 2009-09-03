package org.systemsbiology.gaggle.cereopsis.serialization;



import org.systemsbiology.gaggle.core.datatypes.Network;
import org.systemsbiology.gaggle.core.datatypes.Interaction;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;


/*
* Copyright (C) 2008 by Institute for Systems Biology,
* Seattle, Washington, USA.  All rights reserved.
*
* This source code is distributed under the GNU Lesser
* General Public License, the text of which is available at:
*   http://www.gnu.org/copyleft/lesser.html
*/
public class CompactNetworkToGaggleNetworkConverter {

    private CompactNetwork cn;

    public CompactNetworkToGaggleNetworkConverter(CompactNetwork cn) {
        this.cn = cn;
    }

    public Network toGaggleNetwork() {
        Network gaggleNetwork = new Network();
        gaggleNetwork.setSpecies(cn.getSpecies());
        gaggleNetwork.setName(cn.getName());
        //gaggleNetwork.setMetadata(cn.getMetadata()); //todo
        for (String node : cn.getNodes()) {
            gaggleNetwork.add(node);
        }
        PairedListAndMap edgeTypes = new PairedListAndMap(cn.getEdgeTypes());
        PairedListAndMap nodeNames = new PairedListAndMap(cn.getNodes());
        List<Object> edgeNameList = new ArrayList<Object>();
        for (String edge : cn.getEdges()) {
            String[] segs = edge.split(",");
            String source = (String)nodeNames.getList().get(new Integer(segs[0]));
            String target = (String)nodeNames.getList().get(new Integer(segs[1]));
            String edgeType = (String)edgeTypes.getList().get(new Integer(segs[2]));
            Boolean directed = (segs[3].equals("d"));
            String edgeName = source + " (" + edgeType + ") " + target;
            edgeNameList.add(edgeName);
            gaggleNetwork.add(new Interaction(source, target, edgeType, directed));
        }
        PairedListAndMap edgeNames = new PairedListAndMap(edgeNameList);


        PairedListAndMap nodeAttributeNames = new PairedListAndMap(cn.getNodeAttrNames());
        PairedListAndMap nodeAttributeValues = new PairedListAndMap(cn.getNodeAttrValues());

        PairedListAndMap edgeAttributeNames = new PairedListAndMap(cn.getEdgeAttrNames());
        PairedListAndMap edgeAttributeValues = new PairedListAndMap(cn.getEdgeAttrValues());


        addAttributes(true, nodeNames, nodeAttributeNames, nodeAttributeValues, gaggleNetwork);
        addAttributes(false, edgeNames, edgeAttributeNames, edgeAttributeValues, gaggleNetwork);





        return gaggleNetwork;
    }

    private void addAttributes(boolean addingNodeAttributes, PairedListAndMap names,
                               PairedListAndMap attributeNames, PairedListAndMap attributeValues, Network gaggleNetwork) {
        Map<Integer,Map<Integer,Integer>> attrs = (addingNodeAttributes) ? cn.getNodeAttributes() : cn.getEdgeAttributes();

        for (Integer key : attrs.keySet()) {
            String attrName = (String) attributeNames.getList().get(key);
            Map<Integer, Integer> tmp = attrs.get(key);
            for (Integer tmpKey : tmp.keySet()) {
                String itemName = (String) names.getList().get(tmpKey);
                Integer valueIndex = tmp.get(tmpKey);
                Object value = attributeValues.getList().get(valueIndex);
                if (addingNodeAttributes) {
                    gaggleNetwork.addNodeAttribute(itemName, attrName, value);
                } else {
                    gaggleNetwork.addEdgeAttribute(itemName, attrName, value);
                }
            }

        }

    }


}
