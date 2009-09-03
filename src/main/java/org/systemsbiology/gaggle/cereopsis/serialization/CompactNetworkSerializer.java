package org.systemsbiology.gaggle.cereopsis.serialization;

import org.systemsbiology.gaggle.core.datatypes.Network;
import org.systemsbiology.gaggle.core.datatypes.Interaction;

import net.sf.json.JSONObject;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/*
* Copyright (C) 2008 by Institute for Systems Biology,
* Seattle, Washington, USA.  All rights reserved.
*
* This source code is distributed under the GNU Lesser
* General Public License, the text of which is available at:
*   http://www.gnu.org/copyleft/lesser.html
*/

public class CompactNetworkSerializer {


    public static String serializeToJSON(CompactNetwork compactNetwork) {
        JSONObject jsonObject = JSONObject.fromObject(compactNetwork);
        return jsonObject.toString();
    }

   public static CompactNetwork serializeFromJSON(String json) {
       JSONObject jsonObject = JSONObject.fromObject(json);
       CompactNetwork cn = (CompactNetwork) JSONObject.toBean(jsonObject, CompactNetwork.class);

       JSONObject jsonNodeAttributes = jsonObject.getJSONObject("nodeAttributes");
       cn.setNodeAttributes(cleanUpAfterJsonlib(jsonNodeAttributes));


       JSONObject jsonEdgeAttributes = jsonObject.getJSONObject("edgeAttributes");
       cn.setEdgeAttributes(cleanUpAfterJsonlib(jsonEdgeAttributes));


       return cn;
   }


   private static Map<Integer, Map<Integer,Integer>> cleanUpAfterJsonlib(JSONObject jsonObject) {
       Map<Integer, Map<Integer,Integer>> map = new HashMap<Integer, Map<Integer,Integer>>();
       for (Iterator<String> it = jsonObject.keys(); it.hasNext();) {
           Integer key = new Integer(it.next());
           JSONObject jmap = jsonObject.getJSONObject("" + key);
           Map<Integer,Integer> tmp = new HashMap<Integer,Integer>();
           for (Iterator<String> it2 = jmap.keys(); it2.hasNext();) {
               Integer tmpKey = new Integer(it2.next());
               Integer value = jmap.getInt("" + tmpKey);
               tmp.put(tmpKey, value);
           }
           map.put(key, tmp);
       }
       return map;
   }

    /*
    public static GaggleNetworkToCompactNetworkConverter serializeFromJSON(String json) {
        GaggleNetworkToCompactNetworkConverter cn = new GaggleNetworkToCompactNetworkConverter(network);
        JSONObject jsonObject = JSONObject.fromObject(json);
        JSONArray jnodes = jsonObject.getJSONArray("nodes");
        String[] nodes = (String[])(JSONArray.toArray(jnodes,String.class));
        System.out.println("nodes:");
        for (String node : nodes) {
            System.out.println(node);
        }

        return cn;
    }
    */


    public static void main(String[] args) {

        Interaction i0 = new Interaction("YFL036W", "YFL037W", "GeneCluster", false);
        Interaction i1 = new Interaction("YFL037W", "YLR212C", "GeneFusion", true);
        Interaction i2 = new Interaction("YFL037W", "YML085C", "GeneFusion", false);
        Interaction i3 = new Interaction("YFL037W", "YML124C", "GeneFusion", true);
        Interaction i4 = new Interaction("YLR212C", "YLR213C", "GeneCluster", false);
        Interaction i5 = new Interaction("YLR212C", "YML085C", "GeneFusion", true);
        Interaction i6 = new Interaction("YLR212C", "YML124C", "GeneFusion", false);
        Interaction i7 = new Interaction("YML123C", "YML124C", "GeneCluster", true);
        Interaction i8 = new Interaction("YML085C", "YML086C", "GeneCluster", false);
        Interaction i9 = new Interaction("YML085C", "YML124C", "GeneFusion", true);


        Network network = new Network();
        network.add(i0);
        network.add(i1);
        network.add(i2);
        network.add(i3);
        network.add(i4);
        network.add(i5);
        network.add(i6);
        network.add(i7);
        network.add(i8);
        network.add(i9);

        String species = "Saccharomyces cerevisiae";
        String[] nodeNames = {"YFL036W", "YFL037W", "YLR212C", "YLR213C",
                "YML085C", "YML086C", "YML123C", "YML124C"};
        for (String nodeName : nodeNames) {
            network.addNodeAttribute(nodeName, "moleculeType", "DNA");
            network.addNodeAttribute(nodeName, "species", species);
        }

        network.addEdgeAttribute("YFL036W (GeneCluster) YFL037W", "score", new Double(0.5));
        network.addEdgeAttribute("YFL037W (GeneFusion) YLR212C", "score", new Double(0.4));
        network.addEdgeAttribute("YFL037W (GeneFusion) YML085C", "score", new Double(0.3));
        network.addEdgeAttribute("YFL037W (GeneFusion) YML124C", "score", new Double(0.2));
        network.addEdgeAttribute("YLR212C (GeneCluster) YLR213C", "score", new Double(0.1));
        network.addEdgeAttribute("YLR212C (GeneFusion) YML085C", "score", new Double(0.8));
        network.addEdgeAttribute("YLR212C (GeneFusion) YML124C", "score", new Double(0.75));
        network.addEdgeAttribute("YML123C (GeneCluster) YML124C", "score", new Double(0.55));
        network.addEdgeAttribute("YML085C (GeneCluster) YML086C", "score", new Double(0.45));
        network.addEdgeAttribute("YML085C (GeneFusion) YML124C", "score", new Double(0.35));
        network.addEdgeAttribute("YLR212C (GeneFusion) YML124C", "a string attribute", "hi there");
        network.setSpecies("moose moosculus");
        network.setName("a sample network");

        network.addNodeAttribute("YFL036W","foo", 1);
        network.addNodeAttribute("YML085C","foo", "bar");
        network.addNodeAttribute("YML124C","bar", 1);
        network.addNodeAttribute("YML085C","baz", 1.2);



        GaggleNetworkToCompactNetworkConverter converter = new GaggleNetworkToCompactNetworkConverter(network);
        CompactNetwork cn = converter.toCompactNetwork();
        String jsonNet = CompactNetworkSerializer.serializeToJSON(cn);
        System.out.println("Serialized compact network:\n" + jsonNet);
        cn = CompactNetworkSerializer.serializeFromJSON(jsonNet);
        System.out.println("Round trip:");
        System.out.println(CompactNetworkSerializer.serializeToJSON(cn));

        CompactNetworkToGaggleNetworkConverter cconv = new CompactNetworkToGaggleNetworkConverter(cn);
        Network gn = cconv.toGaggleNetwork();
        System.out.println("Gaggle network:\n" + gn.toString());


    }

}
