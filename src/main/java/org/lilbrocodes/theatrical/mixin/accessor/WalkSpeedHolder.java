package org.lilbrocodes.theatrical.mixin.accessor;

public interface WalkSpeedHolder {
    default int theatrical$getWalkSpeed() {
        return 100;
    }

    default void theatrical$setWalkSpeed(int speed) {

    }
}
