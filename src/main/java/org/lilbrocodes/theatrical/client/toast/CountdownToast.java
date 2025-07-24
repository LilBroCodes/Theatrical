package org.lilbrocodes.theatrical.client.toast;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;
import org.lilbrocodes.composer_reloaded.ComposerReloaded;
import org.lilbrocodes.composer_reloaded.api.render.ToastDrawUtils;
import org.lilbrocodes.composer_reloaded.api.toast.AbstractToast;
import org.lilbrocodes.theatrical.util.Misc;

import java.util.List;

public class CountdownToast extends AbstractToast {

    private static final int HEIGHT = 40;
    private static final int MAX_WIDTH = 60;
    private static final int ICON_SIZE = 30;
    private static final int MARGIN = 20;
    private static final Identifier ICON_TEXTURE = ComposerReloaded.identify("textures/gui/toast/attention.png");

    private static final long FADE_TIME = 500;
    private static final long START_NOTIFICATION_MS = 500;

    private final int tickDuration;
    private final int msCountdownDuration;
    private final int msTotalDuration;

    private int lastSoundSecond = -1;

    public CountdownToast(int duration) {
        super();
        this.tickDuration = duration + 20;
        this.msCountdownDuration = tickDuration * 50;
        this.msTotalDuration = msCountdownDuration + (int) START_NOTIFICATION_MS;
    }

    @Override
    protected void draw(DrawContext context, long timeAlive, int x, int y) {
        float scaleX = getHorizontalScaleFactor(timeAlive);

        int currentSecond = (int) (timeAlive / 1000);
        if (currentSecond != lastSoundSecond && timeAlive <= msCountdownDuration) {
            MinecraftClient.getInstance().getSoundManager().play(
                    PositionedSoundInstance.master(SoundEvents.ENTITY_ARROW_HIT_PLAYER, 1.0f)
            );
            lastSoundSecond = currentSecond;
        }

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scaleX, 1.0f, 1.0f);

        drawBox(context, timeAlive);
        drawText(context, timeAlive);
        drawIcon(context);

        context.getMatrices().pop();

        if (timeAlive >= msTotalDuration || MinecraftClient.getInstance().world == null) {
            remove();
        }
    }

    private void drawText(DrawContext ctx, long timeAlive) {
        int wrapWidth = Math.max(10, MAX_WIDTH - 20);
        Text message;

        if (timeAlive >= msCountdownDuration) {
            message = Text.translatable("theatrical.toast.countdown.done")
                    .formatted(Formatting.GREEN, Formatting.BOLD);
        } else {
            int tickProgress = (int) (timeAlive * 0.02);
            int remainingTicks = tickDuration - tickProgress - 20;
            if (remainingTicks <= 0) {
                message = Text.translatable("theatrical.toast.countdown.done")
                        .formatted(Formatting.GREEN, Formatting.BOLD);
            } else {
                message = Text.literal(String.format("%.1f", remainingTicks / 20d));
            }
        }

        List<OrderedText> lines = textRenderer.wrapLines(message, wrapWidth);
        int wrappedHeight = textRenderer.getWrappedLinesHeight(message, wrapWidth);
        ToastDrawUtils.drawCenteredLines(ctx, textRenderer, lines, 6, 0, wrappedHeight, 0xFFFFFFFF);
    }

    private void drawIcon(DrawContext ctx) {
        int x = -MAX_WIDTH / 2 - 5;
        int y = -ICON_SIZE / 2;
        ctx.drawTexture(ICON_TEXTURE, x, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

    private void drawBox(DrawContext ctx, long timeAlive) {
        int tickProgress = (int) (timeAlive * 0.02);
        ToastDrawUtils.drawCenteredBox(ctx, 0, 0, MAX_WIDTH, HEIGHT, Misc.mixProgressDarkColor((int) (msTotalDuration * 0.02), tickProgress));
        ToastDrawUtils.drawCenteredOutline(ctx, 0, 0, MAX_WIDTH, HEIGHT, Misc.mixProgressColor((int) (msTotalDuration * 0.02), tickProgress), 1);
    }

    private float getHorizontalScaleFactor(long timeAlive) {
        if (timeAlive < FADE_TIME) {
            float t = timeAlive / (float) FADE_TIME;
            return 1.0f - (float) Math.pow(1.0f - t, 3);
        } else if (timeAlive > msTotalDuration - FADE_TIME) {
            float t = (timeAlive - (msTotalDuration - FADE_TIME)) / (float) FADE_TIME;
            return 1.0f - (1.0f - (float) Math.pow(1.0f - t, 3));
        } else {
            return 1.0f;
        }
    }

    @Override
    protected Vector2i size() {
        return new Vector2i(MAX_WIDTH, HEIGHT);
    }

    @Override
    protected int margin() {
        return MARGIN;
    }
}
