package org.lilbrocodes.theatrical.mixin.accessor;

import com.replaymod.recording.gui.GuiRecordingControls;

public interface GuiRecordingControlsDataHolder {
    default GuiRecordingControls theatrical$getGuiControls() {
        return null;
    }
}
