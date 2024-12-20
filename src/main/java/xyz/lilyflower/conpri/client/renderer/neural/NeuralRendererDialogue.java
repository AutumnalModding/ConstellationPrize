package xyz.lilyflower.conpri.client.renderer.neural;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.ColorHelper;
import xyz.lilyflower.conpri.client.renderer.neural.Display.Constants;
import xyz.lilyflower.conpri.init.ConstellationPrizeClient;

@SuppressWarnings("unused")
public class NeuralRendererDialogue extends DisplayModule {
    @Override
    boolean shouldRender() {
        return false;
    }

    @Override
    public void render(DrawContext context, RenderTickCounter counter) {

    }

    @Override
    public void renderDebug(DrawContext context, RenderTickCounter counter) {
        context.fill(Constants.BORDER_PADDING, Constants.BORDER_PADDING, Constants.BORDER_WIDTH, Constants.BORDER_HEIGHT, 0, ColorHelper.Argb.getArgb(0xFF, 0x00, 0xFF));
        context.fill(Constants.DIALOGUE_BOX_X, Constants.DIALOGUE_BOX_Y, Constants.DIALOGUE_BOX_WIDTH, Constants.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.Argb.getArgb(0xFF, 0x00, 0x00));
        context.fill(Constants.DIALOGUE_BOX_WIDTH, Constants.DIALOGUE_BOX_Y, Constants.DIALOGUE_BOX_WIDTH - Constants.DIALOGUE_BOX_HEIGHT, Constants.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.Argb.getArgb(0x00, 0xFF, 0xFF));

        context.fill(Constants.PORTRAIT_X - 4, Constants.PORTRAIT_Y - 4, Constants.PORTRAIT_X + Constants.PORTRAIT_SIZE + 4, Constants.PORTRAIT_Y + Constants.PORTRAIT_SIZE + 4, 0, ColorHelper.Argb.getArgb(0x00, 0x00, 0xFF));
        context.fill(Constants.PORTRAIT_X, Constants.PORTRAIT_Y, Constants.PORTRAIT_X + Constants.PORTRAIT_SIZE, Constants.PORTRAIT_Y + Constants.PORTRAIT_SIZE, 0, ColorHelper.Argb.getArgb(0x00, 0xFF, 0x00));

        for (int line = 0; line < 6; line++) {
            context.drawText(ConstellationPrizeClient.TEXT_RENDERER, "This is a test string. It doesn't support newlines.", Constants.PORTRAIT_X + Constants.PORTRAIT_SIZE + 6, Constants.PORTRAIT_Y + 1 + line * 11, 0xFFFFFF, true);
        }
    }
}
