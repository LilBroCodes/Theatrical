package org.lilbrocodes.theatrical.mixin.accessor;

public interface HandHeldDataHolder {
    default boolean theatrical$isHandHeldOut() {
        return false;
    }

    default void theatrical$setHandHeldOut(boolean attackHeld) {

    }
}
