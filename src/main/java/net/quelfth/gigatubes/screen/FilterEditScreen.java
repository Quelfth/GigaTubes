package net.quelfth.gigatubes.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.quelfth.gigatubes.GigaTubes;
import net.quelfth.gigatubes.network.GigaPacketHandler;
import net.quelfth.gigatubes.network.ServerboundFilterEditPacket;
import net.quelfth.gigatubes.util.CodeCharType;
import net.quelfth.gigatubes.util.Couple;
import net.quelfth.gigatubes.util.code.CodeFormats;
import net.quelfth.gigatubes.util.code.CodeParagraph;
import net.quelfth.gigatubes.util.code.GigaParser;
import net.quelfth.gigatubes.util.code.Predicate;
import net.quelfth.gigatubes.util.code.ParagraphFormat;

public class FilterEditScreen extends Screen {

    private static final ResourceLocation TEXTURE = 
        new ResourceLocation(GigaTubes.MOD_ID, "textures/gui/code_edit.png");

    private String text = "";
    private int caret;
    private final List<String> undoStack = new ArrayList<String>();

    private final int itemSlotLocation;
    
    public FilterEditScreen(int itemSlotLocation) {
        this(itemSlotLocation, "");
    }

    public FilterEditScreen(int itemSlotLocation, String source) {
        super(Component.translatable("text.gigatubes.filter_edit"));
        minecraft = Minecraft.getInstance();
        this.itemSlotLocation = itemSlotLocation;
        this.text = source;
        caret = source.length();
    }

    private byte timer;

    private void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float tick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        
        int x = (width - 176) / 2;
        int y = (height - 166) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, 176, 166);

        graphics.drawString(font, title, x+8, y+6, 0x404040, false);

        String[] lines = text.split("\n");
        CodeParagraph code = new CodeParagraph(lines);
        Couple<Predicate, ParagraphFormat> parse = GigaParser.parseIntoPredicate(code);
        ParagraphFormat format = parse.$1;
        //System.out.println(parse.$0);

        timer++;

        int caretLine = 0;
        int caretPos = 0;
        for (int i = 0; i < text.length() && i < caret; i++) {
            if (text.charAt(i) == '\n') {
                caretLine++;
                caretPos = 0;
            } else {
                caretPos++;
            }
        }

        
        int extraLines = 0;
        while (text.length() > extraLines && text.charAt(text.length()-extraLines-1) == '\n')
            extraLines++;

        boolean blink = (timer & 32) != 0;

        boolean caretRendered = false;

        int i;
        for (i = 0; i < code.numLines(); i++) {
            int w = x+10;
            for (int j = 0; j < code.lineLength(i);) {
                int run = format.getRunLength(i, j);
                final int h = y + 20 + i*10;
                final int color = CodeFormats.colorOf(format.get(i,j));
                if (i != caretLine || !blink)
                    w = graphics.drawString(font, lines[i].substring(j, j+run), w, h, color)-1;
                else {
                    if (caretPos < j || caretPos >= j+run)
                        w = graphics.drawString(font, lines[i].substring(j, j+run), w, h, color)-1;
                    else {
                        if (caretPos - j > 0)
                            w = graphics.drawString(font, lines[i].substring(j, caretPos), w, h, color)-1;
                        graphics.drawString(font, "|", w-1, h, 0x00ff00);
                        caretRendered = true;
                        if (j+run - caretPos > 0)
                            w = graphics.drawString(font, lines[i].substring(caretPos, j+run), w, h, color)-1;
                    }
                }
                
                j += run;
            }
            if (i == caretLine && blink && !caretRendered) {
                graphics.drawString(font, "_", w, y+20+ 10*i, 0x00ff00);
                caretRendered = true;
            }
        }

        
        if (blink && !caretRendered)
            graphics.drawString(font, "_", x+10, y+20 + 10*caretLine, 0x00ff00);
        

        
        
    }

    

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float tick) {
        renderBackground(graphics);
        renderWindow(graphics, mouseX, mouseY, tick);
        super.render(graphics, mouseX, mouseY, tick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case 259 -> {backspace();}
            case 257 -> {type('\n');}
            case 262 -> {caretRight();}
            case 263 -> {caretLeft();}
            case 268 -> {caretHome();}
            case 269 -> {caretEnd();}
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (CodeCharType.of(codePoint) != null) {
            type(codePoint);
            return true;
        }
        
        return super.charTyped(codePoint, modifiers);
    }

    

    private void type(char c) {
        undoStack.add(text);
        text = text.substring(0, caret) + c + text.substring(caret, text.length());
        caret++;
    }

    private void backspace() {
        if (text.length() <= 0 || caret == 0)
            return;
        undoStack.add(text);

        if (caret == 1)
            text = text.substring(1, text.length());
        else if (caret == text.length())
            text = text.substring(0, text.length()-1);
        else
            text = text.substring(0, caret-1) + text.substring(caret, text.length());
        caret--;
    
    }

    private void caretRight() {
        if (caret < text.length())
            caret++;
    }

    private void caretLeft() {
        if (caret > 0)
            caret--;
    }

    private void caretHome() {
        while (caret > 0 && text.charAt(caret-1) != '\n')
            caret--;
    }

    private void caretEnd() {
        while (caret < text.length() && text.charAt(caret) != '\n')
            caret++;
    }

    private void save() {
        
        GigaPacketHandler.CHANNEL.sendToServer(new ServerboundFilterEditPacket(text, itemSlotLocation));
    }

    @Override
    public void onClose() {
        super.onClose();
        save();
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
