package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.options.ColorOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.util.Color;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Identifier;

import java.util.List;

public class iconHud extends AbstractHudEntry {

    public Identifier ID = new Identifier("axolotlclient", "IconHud");
    private final ColorOption color = new ColorOption("color", new Color(255, 255, 255, 255));

    public iconHud() {
        super(15, 15);
    }

    @Override
    public void render() {
        scale();
        DrawPosition pos = getPos();
        this.client.getTextureManager().bindTexture(Axolotlclient.badgeIcon);
        if(chroma.get())GlStateManager.color4f(textColor.getChroma().getRed(), textColor.getChroma().getGreen(), textColor.getChroma().getBlue(), 1F);
        else {GlStateManager.color4f(color.get().getRed(), color.get().getGreen(), color.get().getBlue(), color.get().getAlpha());}
        drawTexture(pos.x, pos.y, 0, 0, width, height, width, height);

        GlStateManager.popMatrix();
    }

    @Override
    public void renderPlaceholder() {
        scale();
        DrawPosition pos = getPos();
        this.client.getTextureManager().bindTexture(Axolotlclient.badgeIcon);
        GlStateManager.disableDepthTest();
        GlStateManager.color4f(1F, 1F, 1F, 1F);
        GuiLighting.disable();
        drawTexture(pos.x, pos.y, 0, 0, width, height, width, height);
        GlStateManager.enableDepthTest();
        GlStateManager.popMatrix();
        hovered = false;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    protected double getDefaultX() {
        return 0;
    }

    @Override
    protected float getDefaultY() {
        return MinecraftClient.getInstance().height-height;
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(color);
        options.add(chroma);
    }
}