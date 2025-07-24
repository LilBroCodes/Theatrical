package org.lilbrocodes.theatrical.integrations.voicechat;

import de.maxhenkel.voicechat.voice.client.ClientManager;
import de.maxhenkel.voicechat.voice.client.ClientVoicechat;

public class VoicechatClassInteractor {
    public static boolean isRecording() {
        ClientVoicechat clientVoicechat = ClientManager.getClient();
        if (clientVoicechat == null) return false;
        return clientVoicechat.getRecorder() != null;
    }

    public static void setRecording(boolean recording) {
        ClientVoicechat clientVoicechat = ClientManager.getClient();
        if (clientVoicechat == null) return;
        clientVoicechat.setRecording(recording);
    }

    public static void toggleRecording() {
        ClientVoicechat clientVoicechat = ClientManager.getClient();
        if (clientVoicechat == null) return;
        clientVoicechat.toggleRecording();
    }
}
