package org.hhoa.mc.item_information.itemtooltip.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemInfo {
    private String name;
    private Map<String, Set<String>> infos = new HashMap<>();

    public ItemInfo() {}

    public ItemInfo(String name) {
        this.name = name;
        this.infos = new HashMap<>();
    }

    public Map<String, Set<String>> getInfos() {
        return infos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInfos(Map<String, Set<String>> infos) {
        this.infos = infos;
    }

    public Set<String> getInfo(String tag) {
        return infos.computeIfAbsent(tag, (k) -> new HashSet<>());
    }

    @Override
    public String toString() {
        return "Item{" + "name='" + name + '\'' + ", info=" + infos + '}';
    }
}
