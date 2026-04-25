package org.hhoa.mc.item_information.mobdictionary.capabilities;

import org.hhoa.mc.item_information.mobdictionary.data.MobSavedData;

/** The interface First login capability. */
public interface MobDataCapability {
    MobSavedData getMobSavedData();

    void setMobSavedData(MobSavedData mobSavedData);
}
