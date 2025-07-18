package xyz.lilyflower.conpri.client.display.module;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import xyz.lilyflower.conpri.text.EngineState;
import xyz.lilyflower.conpri.text.util.ReadaheadParser;
import xyz.lilyflower.conpri.text.TextEngine;
import xyz.lilyflower.conpri.client.display.util.MagicNumbers;

@SuppressWarnings("unused")
public class DialogueModule implements GenericModule {
    public static String CURRENT_PORTRAIT = "";
    private static final ArrayList<String> LINES = new ArrayList<>();
    private static boolean ACTIVE = false;

    public static void init(String... keys) {
        LINES.clear();
        String[] unparsed = new String[keys.length];
        boolean first = true;
        for (int index = 0; index < keys.length; index++) {
            unparsed[index] = I18n.translate(keys[index]);
        }

        String[] parsed = ReadaheadParser.parse(unparsed);
        LINES.addAll(Arrays.asList(parsed));

        ACTIVE = true;
    }

    @Override
    public boolean shouldRender() {
        return ACTIVE;
    }

    @Override
    public void render(DrawContext context, RenderTickCounter counter) {

    }

    @Override
    public void renderDebug(DrawContext context, RenderTickCounter counter) {
        if (!EngineState.isActive()) {
            init("dialogue.conpri.debug_4", "dialogue.conpri.debug_5");
            EngineState.setPosition(MagicNumbers.PORTRAIT_X + MagicNumbers.PORTRAIT_SIZE + 6, MagicNumbers.PORTRAIT_Y + 1);

            TextEngine.init(new TextEngine.Message(
                    "textures/gui/portrait/pancakes.png",
                    null,
                    null,
                    LINES.toArray(new String[0])
            ));
        }

        context.fill(MagicNumbers.BORDER_PADDING, MagicNumbers.BORDER_PADDING, MagicNumbers.BORDER_WIDTH, MagicNumbers.BORDER_HEIGHT, 0, ColorHelper.getArgb(0x70, 0x00, 0x00));
        context.fill(MagicNumbers.DIALOGUE_BOX_X, MagicNumbers.DIALOGUE_BOX_Y, MagicNumbers.DIALOGUE_BOX_WIDTH, MagicNumbers.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.getArgb(0x20, 0x20, 0x20));
        context.fill(MagicNumbers.DIALOGUE_BOX_WIDTH, MagicNumbers.DIALOGUE_BOX_Y, MagicNumbers.DIALOGUE_BOX_WIDTH - MagicNumbers.DIALOGUE_BOX_HEIGHT, MagicNumbers.DIALOGUE_BOX_HEIGHT, 0, ColorHelper.getArgb(0x00, 0xFF, 0xFF));

        context.fill(MagicNumbers.PORTRAIT_X - 4, MagicNumbers.PORTRAIT_Y - 4, MagicNumbers.PORTRAIT_X + MagicNumbers.PORTRAIT_SIZE + 4, MagicNumbers.PORTRAIT_Y + MagicNumbers.PORTRAIT_SIZE + 4, 0, ColorHelper.getArgb(0x00, 0x00, 0xFF));
        context.fill(MagicNumbers.PORTRAIT_X, MagicNumbers.PORTRAIT_Y, MagicNumbers.PORTRAIT_X + MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_Y + MagicNumbers.PORTRAIT_SIZE, 0, ColorHelper.getArgb(0x00, 0xFF, 0x00));
        context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("conpri", CURRENT_PORTRAIT), MagicNumbers.PORTRAIT_X, MagicNumbers.PORTRAIT_Y, 0, 0, MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_SIZE, MagicNumbers.PORTRAIT_SIZE);

        TextEngine.update(context, counter);
    }
}
