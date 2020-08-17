package org.halvors.datnuclearphysicslite.api.nbt;

import net.minecraft.nbt.NBTTagCompound;

public interface ISaveObject {
    void readFromNBT(NBTTagCompound tag);

    NBTTagCompound writeToNBT(NBTTagCompound tag);
}
