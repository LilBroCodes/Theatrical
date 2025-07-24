package org.lilbrocodes.theatrical.integrations;

import org.lilbrocodes.theatrical.integrations.replaymod.ReplaymodChecks;
import org.lilbrocodes.theatrical.integrations.voicechat.VoicechatChecks;

import java.util.List;

public class CooldownChecks {
    public static List<Integer> runChecks() {
        return List.of(VoicechatChecks.runChecks(), ReplaymodChecks.runChecks());
    }

    public static void fixChecks() {
        VoicechatChecks.fixChecks();
        ReplaymodChecks.fixChecks();
    }
}
