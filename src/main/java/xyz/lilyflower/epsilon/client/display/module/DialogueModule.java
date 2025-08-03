package xyz.lilyflower.epsilon.client.display.module;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import xyz.lilyflower.epsilon.text.EngineState;
import xyz.lilyflower.epsilon.text.TextEngine;
import xyz.lilyflower.epsilon.client.display.util.MagicNumbers;

@SuppressWarnings("unused")
public class DialogueModule extends EngineState implements GenericModule {
    @Override
    public boolean shouldRender() {
        return EngineState.isActive();
    }

    @Override
    public void render(DrawContext context, RenderTickCounter counter) {
        if (shouldRender()) {
            context.fill(MagicNumbers.BORDER_PADDING, MagicNumbers.BORDER_PADDING, MagicNumbers.BORDER_WIDTH, MagicNumbers.BORDER_HEIGHT, 0, ColorHelper.getArgb(0x70, 0x00, 0x00));
            context.fill(MagicNumbers.DIALOGUE_BOX_X, MagicNumbers.DIALOGUE_BOX_Y, MagicNumbers.DIALOGUE_BOX_WIDTH, MagicNumbers.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.getArgb(0x20, 0x20, 0x20));
            context.fill(MagicNumbers.DIALOGUE_BOX_WIDTH, MagicNumbers.DIALOGUE_BOX_Y, MagicNumbers.DIALOGUE_BOX_WIDTH - MagicNumbers.DIALOGUE_BOX_HEIGHT, MagicNumbers.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.getArgb(0x00, 0xFF, 0xFF));

            context.fill(MagicNumbers.PORTRAIT_X - 4, MagicNumbers.PORTRAIT_Y - 4, MagicNumbers.PORTRAIT_X + MagicNumbers.PORTRAIT_SIZE + 4, MagicNumbers.PORTRAIT_Y + MagicNumbers.PORTRAIT_SIZE + 4, 0, ColorHelper.getArgb(0x00, 0x00, 0xFF));
            context.fill(MagicNumbers.PORTRAIT_X, MagicNumbers.PORTRAIT_Y, MagicNumbers.PORTRAIT_X + MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_Y + MagicNumbers.PORTRAIT_SIZE, 0, ColorHelper.getArgb(0x00, 0xFF, 0x00));
            context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("epsilon", CURRENT_MESSAGE.portrait()), MagicNumbers.PORTRAIT_X, MagicNumbers.PORTRAIT_Y, 0, 0, MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_SIZE);

            TextEngine.update(context, counter);
        }
    }

    @Override
    public void renderDebug(DrawContext context, RenderTickCounter counter) {}
}
