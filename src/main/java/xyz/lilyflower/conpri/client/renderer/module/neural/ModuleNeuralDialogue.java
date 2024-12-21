package xyz.lilyflower.conpri.client.renderer.module.neural;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import xyz.lilyflower.conpri.client.renderer.Display;
import xyz.lilyflower.conpri.client.renderer.Display.Constants;
import xyz.lilyflower.conpri.client.renderer.module.RendererModule;
import xyz.lilyflower.conpri.util.TextRenderer;

@SuppressWarnings("unused")
public class ModuleNeuralDialogue implements RendererModule {
    public static String CURRENT_PORTRAIT = "";

    @Override
    public boolean shouldRender() {
        return Display.GLASSES_EQUIPPED;
    }

    @Override
    public void render(DrawContext context, RenderTickCounter counter) {

    }

    @Override
    public void renderDebug(DrawContext context, RenderTickCounter counter) {
        if (!TextRenderer.SHOULD_UPDATE) {
            TextRenderer.DRAW_POSITION_X = Display.Constants.PORTRAIT_X + Display.Constants.PORTRAIT_SIZE + 6;
            TextRenderer.DRAW_POSITION_Y = Display.Constants.PORTRAIT_Y + 1;

            TextRenderer.init(new TextRenderer.Message(
                    "textures/gui/portrait/pancakes.png",
                    null,
                    null,
                    "This is some text. It's fourty characters!",
                    "And you can have up to six lines, too!",
                    "You can even\000\001\001\144 pause text while parsing!",
                    "Or change the\000\001\002\012 line speed \000\001\002\002mid-line!\000\001\001\062 "
            ));
        }

        context.fill(Constants.BORDER_PADDING, Constants.BORDER_PADDING, Constants.BORDER_WIDTH, Constants.BORDER_HEIGHT, 0, ColorHelper.Argb.getArgb(0x70, 0x00, 0x00));
        context.fill(Constants.DIALOGUE_BOX_X, Constants.DIALOGUE_BOX_Y, Constants.DIALOGUE_BOX_WIDTH, Constants.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.Argb.getArgb(0x20, 0x20, 0x20));
        context.fill(Constants.DIALOGUE_BOX_WIDTH, Constants.DIALOGUE_BOX_Y, Constants.DIALOGUE_BOX_WIDTH - Constants.DIALOGUE_BOX_HEIGHT, Constants.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.Argb.getArgb(0x00, 0xFF, 0xFF));

        context.fill(Constants.PORTRAIT_X - 4, Constants.PORTRAIT_Y - 4, Constants.PORTRAIT_X + Constants.PORTRAIT_SIZE + 4, Constants.PORTRAIT_Y + Constants.PORTRAIT_SIZE + 4, 0, ColorHelper.Argb.getArgb(0x00, 0x00, 0xFF));
        context.fill(Constants.PORTRAIT_X, Constants.PORTRAIT_Y, Constants.PORTRAIT_X + Constants.PORTRAIT_SIZE, Constants.PORTRAIT_Y + Constants.PORTRAIT_SIZE, 0, ColorHelper.Argb.getArgb(0x00, 0xFF, 0x00));
        context.drawTexture(Identifier.of("conpri", CURRENT_PORTRAIT), Constants.PORTRAIT_X, Constants.PORTRAIT_Y, 0, 0, 0, Constants.PORTRAIT_SIZE, Constants.PORTRAIT_SIZE, Constants.PORTRAIT_SIZE, Constants.PORTRAIT_SIZE);

        TextRenderer.update(context);
    }
}
