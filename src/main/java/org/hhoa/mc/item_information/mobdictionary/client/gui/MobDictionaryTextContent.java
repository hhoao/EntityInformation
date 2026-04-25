package org.hhoa.mc.item_information.mobdictionary.client.gui;

final class MobDictionaryTextContent {
    private MobDictionaryTextContent() {}

    static String lockedDisplayName(String unknownPrefix, int id) {
        return unknownPrefix + " #" + id;
    }

    static String progressText(int unlockedCount, int totalCount) {
        return unlockedCount + "/" + totalCount;
    }
}
