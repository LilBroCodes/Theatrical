package org.lilbrocodes.theatrical.mixin.accessor;

public interface DirectorDataHolder {
    default boolean theatrical$isDirector() {
        return false;
    }

    default void theatrical$setDirector(boolean director) {

    }
}
