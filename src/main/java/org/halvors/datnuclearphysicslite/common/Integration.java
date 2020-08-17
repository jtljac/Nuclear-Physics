package org.halvors.datnuclearphysicslite.common;

import net.minecraftforge.fml.common.Loader;

public class Integration {
    public static final String BUILDCRAFT_CORE_ID = "buildcraftcore";
    public static final String COFH_CORE_ID = "cofhcore";
    public static final String MEKANISM_ID = "mekanism";

    public static boolean isBuildcraftLoaded;
    public static boolean isCOFHCoreLoaded;
    public static boolean isMekanismLoaded;

    public static void initialize() {
        isBuildcraftLoaded = Loader.isModLoaded(BUILDCRAFT_CORE_ID);
        isCOFHCoreLoaded = Loader.isModLoaded(COFH_CORE_ID);
        isMekanismLoaded = Loader.isModLoaded(MEKANISM_ID);
    }
}
