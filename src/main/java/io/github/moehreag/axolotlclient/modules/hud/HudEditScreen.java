package io.github.moehreag.axolotlclient.modules.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.config.ConfigManager;
import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.screen.CategoryScreenBuilder;
import io.github.moehreag.axolotlclient.config.screen.OptionsScreenBuilder;
import io.github.moehreag.axolotlclient.config.screen.widgets.CustomButtonWidget;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.snapping.SnappingHelper;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class HudEditScreen extends Screen {

    private final BooleanOption snapping = new BooleanOption("snapping", true);
    private AbstractHudEntry current;
    private DrawPosition offset = null;
    private final HudManager manager;
    private boolean mouseDown;
    private SnappingHelper snap;
    private final Screen parent;

    public HudEditScreen(Screen parent){
        snapping.setDefaults();
        updateSnapState();
        manager = HudManager.getINSTANCE();
        mouseDown = false;
        this.parent=parent;
    }

    public HudEditScreen(){
        this(null);
    }


    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        if(this.client.world!=null)fillGradient(0,0, width, height, new Color(0xB0100E0E, true).hashCode(), new Color(0x46212020, true).hashCode());
        else {renderDirtBackground(0);}

        GlStateManager.enableAlphaTest();
        for(ButtonWidget button:buttons){
            button.render(client, mouseX, mouseY);
        }
        GlStateManager.disableAlphaTest();
        manager.renderPlaceholder();
        if (mouseDown && snap != null) {
            snap.renderSnaps();
        }

        Optional<AbstractHudEntry> entry = manager.getEntryXY(mouseX, mouseY);
        entry.ifPresent(abstractHudEntry -> abstractHudEntry.setHovered(true));

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {

        Optional<AbstractHudEntry> entry = manager.getEntryXY(Math.round(mouseX), Math.round(mouseY));
        if (button == 0) {
            mouseDown = true;
            if (entry.isPresent()) {
                current = entry.get();
                offset = new DrawPosition(Math.round(mouseX - current.getX()), Math.round(mouseY - current.getY()));
                updateSnapState();
            } else {
                current = null;
                super.mouseClicked(mouseX, mouseY, button);
            }
        } else if (button == 1) {
            entry.ifPresent(abstractHudEntry -> client.openScreen(new OptionsScreenBuilder(this, abstractHudEntry.getOptionsAsCategory())));
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        current = null;
        snap = null;
        mouseDown = false;
        ConfigManager.save();
    }

    @Override
    protected void mouseDragged(int mouseX, int mouseY, int button, long l) {
        if (current != null) {
            current.setXY(mouseX - offset.x, mouseY - offset.y);
            if (snap != null) {
                Integer snapX, snapY;
                snap.setCurrent(current.getScaledBounds());
                if ((snapX = snap.getCurrentXSnap()) != null) {
                    current.setX(snapX);
                }
                if ((snapY = snap.getCurrentYSnap()) != null) {
                    current.setY(snapY);
                }
            }
            if (current.tickable()) {
                current.tick();
            }
        }
    }

    private void updateSnapState() {
        if (snapping.get() && current != null) {
            List<Rectangle> bounds = manager.getAllBounds();
            bounds.remove(current.getScaledBounds());
            snap = new SnappingHelper(bounds, current.getScaledBounds());
        } else if (snap != null) {
            snap = null;
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if(button.id==1){
            snapping.toggle();
            ConfigManager.save();
            client.openScreen(this);
        }

        if(button.id==3)client.openScreen(new CategoryScreenBuilder(this));

        if(button.id==0)client.openScreen(parent);
        else if(button.id==2)client.closeScreen();
    }

    @Override
    public void init() {
        super.init();
        this.buttons.add(new CustomButtonWidget(1,
                width / 2 - 50,
                height/2+ 12,
                100, 20,
                I18n.translate("hud.snapping") + ": "+I18n.translate(snapping.get()?"options.on":"options.off"),
                new Identifier("axolotlclient", "textures/gui/button2.png")
        ));

        this.buttons.add(new CustomButtonWidget(3,
                width / 2 - 75,
                height/2-10,
                150, 20,
                I18n.translate("hud.clientOptions"),
                new Identifier("axolotlclient", "textures/gui/button1.png")
        ));
        if(parent!=null)this.buttons.add(new CustomButtonWidget(
                0, width/2 -75, height - 50 + 22, 150, 20,
                I18n.translate("back"),new Identifier("axolotlclient", "textures/gui/button1.png")));
        else this.buttons.add(new CustomButtonWidget(
                2, width/2 -75, height - 50 + 22, 150, 20,
                I18n.translate("close"),new Identifier("axolotlclient", "textures/gui/button1.png")));

    }

    @Override
    public void tick() {
        super.tick();
        if(current!=null && current.tickable())current.tick();
    }

    @Override
    public void removed() {
        manager.save();
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }


}
