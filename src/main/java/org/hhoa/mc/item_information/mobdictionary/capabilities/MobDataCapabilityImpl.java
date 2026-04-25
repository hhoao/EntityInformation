package org.hhoa.mc.item_information.mobdictionary.capabilities;

import org.hhoa.mc.item_information.mobdictionary.data.MobSavedData;

public class MobDataCapabilityImpl implements MobDataCapability {
    private MobSavedData mobSavedData = new MobSavedData();

    @Override
    public MobSavedData getMobSavedData() {
        return this.mobSavedData;
    }

    @Override
    public void setMobSavedData(MobSavedData mobSavedData) {
        this.mobSavedData = mobSavedData;
    }
}
