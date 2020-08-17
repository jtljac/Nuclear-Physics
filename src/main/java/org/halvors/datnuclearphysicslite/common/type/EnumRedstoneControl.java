package org.halvors.datnuclearphysicslite.common.type;

import org.halvors.datnuclearphysicslite.common.utility.LanguageUtility;

public enum EnumRedstoneControl {
    DISABLED,
    HIGH,
    LOW,
    PULSE;

    public String getDisplay() {
        return LanguageUtility.transelate("gui.control." + name().toLowerCase());
    }
}