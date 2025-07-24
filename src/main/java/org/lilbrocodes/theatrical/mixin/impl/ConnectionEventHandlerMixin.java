package org.lilbrocodes.theatrical.mixin.impl;

import com.replaymod.recording.gui.GuiRecordingControls;
import com.replaymod.recording.handler.ConnectionEventHandler;
import org.lilbrocodes.theatrical.mixin.accessor.GuiRecordingControlsDataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ConnectionEventHandler.class)
public class ConnectionEventHandlerMixin implements GuiRecordingControlsDataHolder {

    @Shadow private GuiRecordingControls guiControls;

    @Override
    public GuiRecordingControls theatrical$getGuiControls() {
        return guiControls;
    }
}
