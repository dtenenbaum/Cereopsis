package org.systemsbiology.gaggle.cereopsis.serialization;



import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;

class PairedListAndMap {
    List<Object> list;
    Map<Object, Integer> map = new HashMap<Object,Integer>();

    PairedListAndMap(List<Object> list) {
        this.list = list;
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i),i);
        }
    }

    PairedListAndMap(Object[] array) {
        this(Arrays.asList(array));
    }


    Map<Object,Integer> getMap() {
        return map;
    }

    List<Object> getList() {
        return list;
    }

    void setMap(Map<Object,Integer> map) {
        this.map = map;
    }

}
