package org.lilbrocodes.theatrical.mixin.accessor;

import org.lilbrocodes.theatrical.util.PlotArmorType;

public interface PlotArmorDataHolder {
    default PlotArmorType theatrical$getType() {
        return PlotArmorType.NONE;
    }

    default void theatrical$setType(PlotArmorType type) {

    }
}
