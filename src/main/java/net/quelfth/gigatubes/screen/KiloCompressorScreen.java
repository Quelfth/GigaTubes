package net.quelfth.gigatubes.screen;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.quelfth.gigatubes.GigaTubes;

public class KiloCompressorScreen extends AbstractContainerScreen<KiloCompressorMenu> {
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation(GigaTubes.MOD_ID, "textures/gui/chadmium_compressor.png");

    public KiloCompressorScreen(KiloCompressorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY += 9;
        this.titleLabelY -= 9;
    }

    

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float tick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        imageHeight = 184;
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        
        renderProgressArrow(graphics, x, y);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouse_x, int mouse_y, float partial_tick) {
        
        super.render(graphics, mouse_x, mouse_y, partial_tick);
        renderTooltip(graphics, mouse_x, mouse_y);
    }

    private void renderProgressArrow(GuiGraphics graphics, int x, int y) {
        graphics.blit(TEXTURE, x + 100, y + 44, 176, 0, 22, 16);
    }
}
