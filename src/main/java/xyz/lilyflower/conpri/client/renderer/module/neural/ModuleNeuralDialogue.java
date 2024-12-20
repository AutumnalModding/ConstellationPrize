package xyz.lilyflower.conpri.client.renderer.module.neural;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import xyz.lilyflower.conpri.client.renderer.Display.Constants;
import xyz.lilyflower.conpri.client.renderer.module.RendererModule;
import xyz.lilyflower.conpri.util.TextRenderer;

@SuppressWarnings("unused")
public class ModuleNeuralDialogue implements RendererModule {
    @Override
    public boolean shouldRender() {
        return false;
    }

    @Override
    public void render(DrawContext context, RenderTickCounter counter) {

    }

    @Override
    public void renderDebug(DrawContext context, RenderTickCounter counter) {
        context.fill(Constants.BORDER_PADDING, Constants.BORDER_PADDING, Constants.BORDER_WIDTH, Constants.BORDER_HEIGHT, 0, ColorHelper.Argb.getArgb(0x70, 0x00, 0x00));
        context.fill(Constants.DIALOGUE_BOX_X, Constants.DIALOGUE_BOX_Y, Constants.DIALOGUE_BOX_WIDTH, Constants.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.Argb.getArgb(0x20, 0x20, 0x20));
        context.fill(Constants.DIALOGUE_BOX_WIDTH, Constants.DIALOGUE_BOX_Y, Constants.DIALOGUE_BOX_WIDTH - Constants.DIALOGUE_BOX_HEIGHT, Constants.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.Argb.getArgb(0x00, 0xFF, 0xFF));

        context.fill(Constants.PORTRAIT_X - 4, Constants.PORTRAIT_Y - 4, Constants.PORTRAIT_X + Constants.PORTRAIT_SIZE + 4, Constants.PORTRAIT_Y + Constants.PORTRAIT_SIZE + 4, 0, ColorHelper.Argb.getArgb(0x00, 0x00, 0xFF));
        context.fill(Constants.PORTRAIT_X, Constants.PORTRAIT_Y, Constants.PORTRAIT_X + Constants.PORTRAIT_SIZE, Constants.PORTRAIT_Y + Constants.PORTRAIT_SIZE, 0, ColorHelper.Argb.getArgb(0x00, 0xFF, 0x00));
        context.drawTexture(Identifier.of("conpri", "textures/gui/portrait/pancakes.png"), Constants.PORTRAIT_X, Constants.PORTRAIT_Y, 0, 0, 0, Constants.PORTRAIT_SIZE, Constants.PORTRAIT_SIZE, Constants.PORTRAIT_SIZE, Constants.PORTRAIT_SIZE);

        TextRenderer.update(context);
    }
}
