package org.lilbrocodes.theatrical.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.lilbrocodes.composer_reloaded.api.render.ToastDrawUtils;
import org.lilbrocodes.theatrical.mixin.accessor.DirectorDataHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Misc {
    private static final SimpleCommandExceptionType NOT_DIRECTOR_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("exception.theatrical.not_director"));

    public static String getQuantifier(List<?> list) {
        if (list.size() == 1) return "";
        return "s";
    }

    public static String joinAndAppend(String[] list, String append) {
        if (list == null || list.length == 0) return append;
        return Arrays.stream(list, 0, list.length - 1)
                .collect(Collectors.joining(" ")) + (list.length > 1 ? " " : "") + append;
    }

    public static void checkDirectorOrThrow(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        if (!isDirector(ctx.getSource())) throw NOT_DIRECTOR_EXCEPTION.create();
    }

    public static boolean isDirector(ServerCommandSource src) {
        if (src.hasPermissionLevel(4)) return true;
        return src.isExecutedByPlayer() && src.getPlayer() instanceof DirectorDataHolder holder && holder.theatrical$isDirector();
    }

    public static int mixSpeedColor(int speed) {
        int r = (int)(255 * (100 - speed) / 100.0);
        return (r << 16) | (255 << 8);
    }

    public static int mixProgressColor(int duration, int progress) {
        if (duration <= 0) {
            return ToastDrawUtils.argb(255, 255, 0, 0);
        }

        progress = Math.max(0, Math.min(progress, duration));
        float t = progress / (float) duration;

        int r, g;

        if (t < 0.5f) {
            float localT = t / 0.5f;
            r = 255;
            g = (int) (localT * 255);
        } else {
            float localT = (t - 0.5f) / 0.5f;
            r = (int) (255 * (1.0f - localT));
            g = 255;
        }

        int b = 0;
        int a = 255;

        return ToastDrawUtils.argb(r, g, b, a);
    }

    public static int mixProgressDarkColor(int duration, int progress) {
        if (duration <= 0) {
            return ToastDrawUtils.argb(80, 70, 0, 61);
        }

        progress = Math.max(0, Math.min(progress, duration));
        float t = progress / (float) duration;

        int brightR, brightG;

        if (t < 0.5f) {
            float localT = t / 0.5f;
            brightR = 255;
            brightG = (int) (localT * 255);
        } else {
            float localT = (t - 0.5f) / 0.5f;
            brightR = (int) (255 * (1.0f - localT));
            brightG = 255;
        }
        int brightB = 0;

        int baseR = 70;
        int baseG = 0;
        int baseB = 61;

        float blendFactor = 0.3f;

        int r = (int) (brightR * blendFactor + baseR * (1 - blendFactor));
        int g = (int) (brightG * blendFactor + baseG * (1 - blendFactor));
        int b = (int) (brightB * blendFactor + baseB * (1 - blendFactor));

        int a = 80;

        return ToastDrawUtils.argb(r, g, b, a);
    }

}
