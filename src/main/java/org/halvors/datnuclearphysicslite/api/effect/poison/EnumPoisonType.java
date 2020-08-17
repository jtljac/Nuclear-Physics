package org.halvors.datnuclearphysicslite.api.effect.poison;

public enum EnumPoisonType {
    RADIATION,
    CHEMICAL,
    CONTAGIOUS;

    public String getName() {
        return name().toLowerCase();
    }
}