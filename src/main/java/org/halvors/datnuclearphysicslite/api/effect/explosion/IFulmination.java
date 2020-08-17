package org.halvors.datnuclearphysicslite.api.effect.explosion;

public interface IFulmination {
    /**
     * The RADIUS of effect of the explosion.
     */
    float getRadius();

    /**
     * The energy emitted by this explosive. In Joules and approximately based off of a real life equivalent.
     */
    int getEnergy();
}