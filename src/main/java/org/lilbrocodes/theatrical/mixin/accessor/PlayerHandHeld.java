package org.lilbrocodes.theatrical.mixin.accessor;

public interface PlayerHandHeld {
    default boolean theatrical$isHandHeldOut() {
        return false;
    }

    default void theatrical$setHandHeldOut(boolean attackHeld) {

    }
}
