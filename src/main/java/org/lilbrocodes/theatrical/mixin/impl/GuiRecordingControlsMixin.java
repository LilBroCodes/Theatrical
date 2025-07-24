package org.lilbrocodes.theatrical.mixin.impl;

import com.replaymod.core.ReplayMod;
import com.replaymod.recording.gui.GuiRecordingControls;
import com.replaymod.recording.packet.PacketListener;
import org.lilbrocodes.theatrical.mixin.accessor.RecordingControlsAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiRecordingControls.class)
public abstract class GuiRecordingControlsMixin implements RecordingControlsAccessor {
    @Shadow private boolean paused;
    @Shadow private PacketListener packetListener;
    @Shadow private ReplayMod core;
    @Shadow protected abstract void updateState();
    @Shadow private boolean stopped;

    @Override
    public void theatrical$start() {
        this.paused = false;
        this.packetListener.addMarker("_RM_END_CUT");
        this.core.printInfoToChat("replaymod.chat.recordingstarted");

        stopped = !stopped;
        updateState();
    }

    @Override
    public void theatrical$stop() {
        int timestamp = (int)this.packetListener.getCurrentDuration();
        if (!this.paused) {
            this.packetListener.addMarker("_RM_START_CUT", timestamp);
        }
        this.packetListener.addMarker("_RM_SPLIT", timestamp + 1);

        stopped = !stopped;
        updateState();
    }
}
