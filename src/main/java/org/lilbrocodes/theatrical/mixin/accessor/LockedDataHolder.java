package org.lilbrocodes.theatrical.mixin.accessor;

public interface LockedDataHolder {
    default boolean theatrical$isLocked() {
        return false;
    }

    default void theatrical$setLocked(boolean locked) {

    }
}
