package xyz.lilyflower.conpri.feature.dialogue;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import xyz.lilyflower.conpri.client.renderer.module.RendererModule;

@SuppressWarnings("unused")
public class DialogueBoxRenderer implements RendererModule {
    public static String CURRENT_PORTRAIT = "";

    @Override
    public boolean shouldRender() {
        return false;
    }

    @Override
    public void render(DrawContext context, RenderTickCounter counter) {

    }

    @Override
    public void renderDebug(DrawContext context, RenderTickCounter counter) {
        if (DialogueTextRenderer.RENDERER_STATE == DialogueTextRenderer.State.INACTIVE) {
            DialogueTextRenderer.DRAW_POSITION_X = MagicNumbers.PORTRAIT_X + MagicNumbers.PORTRAIT_SIZE + 6;
            DialogueTextRenderer.DRAW_POSITION_Y = MagicNumbers.PORTRAIT_Y + 1;

            DialogueTextRenderer.init(new DialogueTextRenderer.Message(
                    "textures/gui/portrait/pancakes.png",
                    null,
                    SoundEvents.UI_BUTTON_CLICK.value(),
                    "\000\002\000\377\000\377\000\001 \000\002\000\377\000\377\001\001 \000\002\000\377\000\377\002\001 This is some text. It's fourty characters!",
                    "And you can have up to six lines, too!",
                    "You can even\000\001\001\070 pause text while parsing!",
                    "Or change the\000\001\002\012 line speed \000\001\002\002mid-line!\000\001\001\062 "
            ));
        }

        context.fill(MagicNumbers.BORDER_PADDING, MagicNumbers.BORDER_PADDING, MagicNumbers.BORDER_WIDTH, MagicNumbers.BORDER_HEIGHT, 0, ColorHelper.Argb.getArgb(0x70, 0x00, 0x00));
        context.fill(MagicNumbers.DIALOGUE_BOX_X, MagicNumbers.DIALOGUE_BOX_Y, MagicNumbers.DIALOGUE_BOX_WIDTH, MagicNumbers.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.Argb.getArgb(0x20, 0x20, 0x20));
        context.fill(MagicNumbers.DIALOGUE_BOX_WIDTH, MagicNumbers.DIALOGUE_BOX_Y, MagicNumbers.DIALOGUE_BOX_WIDTH - MagicNumbers.DIALOGUE_BOX_HEIGHT, MagicNumbers.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.Argb.getArgb(0x00, 0xFF, 0xFF));

        context.fill(MagicNumbers.PORTRAIT_X - 4, MagicNumbers.PORTRAIT_Y - 4, MagicNumbers.PORTRAIT_X + MagicNumbers.PORTRAIT_SIZE + 4, MagicNumbers.PORTRAIT_Y + MagicNumbers.PORTRAIT_SIZE + 4, 0, ColorHelper.Argb.getArgb(0x00, 0x00, 0xFF));
        context.fill(MagicNumbers.PORTRAIT_X, MagicNumbers.PORTRAIT_Y, MagicNumbers.PORTRAIT_X + MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_Y + MagicNumbers.PORTRAIT_SIZE, 0, ColorHelper.Argb.getArgb(0x00, 0xFF, 0x00));
        context.drawTexture(Identifier.of("conpri", CURRENT_PORTRAIT), MagicNumbers.PORTRAIT_X, MagicNumbers.PORTRAIT_Y, 0, 0, 0, MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_SIZE);

        DialogueTextRenderer.update(context);
    }
}
