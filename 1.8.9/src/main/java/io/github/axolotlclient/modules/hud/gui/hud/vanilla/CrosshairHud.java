/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.modules.hud.gui.hud.vanilla;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.options.*;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.util.RenderUtil;
import io.github.axolotlclient.util.Util;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class CrosshairHud extends AbstractHudEntry implements DynamicallyPositionable {

	public static final Identifier ID = new Identifier("kronhud", "crosshairhud");

	private final EnumOption type = new EnumOption("crosshair_type", Crosshair.values(), Crosshair.CROSS.toString());
	private final BooleanOption showInF5 = new BooleanOption("showInF5", false);
	private final BooleanOption applyBlend = new BooleanOption("applyBlend", true);
	private final BooleanOption overrideF3 = new BooleanOption("overrideF3", false);
	private final ColorOption defaultColor = new ColorOption("defaultcolor", Color.WHITE);
	private final ColorOption entityColor = new ColorOption("entitycolor", Color.SELECTOR_RED);
	private final ColorOption containerColor = new ColorOption("blockcolor", Color.SELECTOR_BLUE);

	private final GraphicsOption customTextureGraphics = new GraphicsOption("customTextureGraphics",
			new int[][]{
					new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},}, true);

	public CrosshairHud() {
		super(15, 15);
	}

	@Override
	public boolean movable() {
		return false;
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(type);
		options.add(customTextureGraphics);
		options.add(showInF5);
		options.add(overrideF3);
		options.add(applyBlend);
		options.add(defaultColor);
		options.add(entityColor);
		options.add(containerColor);
		return options;
	}

	@Override
	public boolean overridesF3() {
		return overrideF3.get();
	}

	@Override
	public double getDefaultX() {
		return 0.5;
	}

	@Override
	public double getDefaultY() {
		return 0.5F;
	}

	@Override
	public void render(float delta) {
		if (!(client.options.perspective == 0) && !showInF5.get())
			return;

		GlStateManager.enableAlphaTest();

		GlStateManager.pushMatrix();
		scale();
		Color color = getColor();
		GlStateManager.color((float) color.getRed() / 255, (float) color.getGreen() / 255,
				(float) color.getBlue() / 255, 1F);
		if (color == defaultColor.get() && applyBlend.get()) {
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(775, 769, 1, 0);
		}

		int x = getPos().x;
		int y = getPos().y + 1;
		if (type.get().equals(Crosshair.DOT.toString())) {
			RenderUtil.fillBlend(x + (width / 2) - 1, y + (height / 2) - 2, 3, 3, color);
		} else if (type.get().equals(Crosshair.CROSS.toString())) {
			RenderUtil.fillBlend(x + (width / 2) - 5, y + (height / 2) - 1, 6, 1, color);
			RenderUtil.fillBlend(x + (width / 2) + 1, y + (height / 2) - 1, 5, 1, color);
			RenderUtil.fillBlend(x + (width / 2), y + (height / 2) - 6, 1, 5, color);
			RenderUtil.fillBlend(x + (width / 2), y + (height / 2), 1, 5, color);
		} else if (type.get().equals(Crosshair.TEXTURE.toString())) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);

			// Draw crosshair
			client.inGameHud.drawTexture((int) (((Util.getWindow().getScaledWidth() / getScale()) - 14) / 2),
					(int) (((Util.getWindow().getScaledHeight() / getScale()) - 14) / 2), 0, 0, 16, 16);
		} else if (type.get().equals(Crosshair.CUSTOM.toString())) {
			customTextureGraphics.bindTexture();
			drawTexture(x + (width / 2) - (15 / 2), y + height / 2 - 15 / 2 - 1, 0, 0, 15, 15, 15, 15);
		}
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		GlStateManager.disableAlphaTest();
	}

	public Color getColor() {
		BlockHitResult hit = client.result;
		if (hit == null || hit.type == null) {
			return defaultColor.get();
		} else if (hit.type == BlockHitResult.Type.ENTITY) {
			return entityColor.get();
		} else if (hit.type == BlockHitResult.Type.BLOCK) {
			BlockPos blockPos = hit.getBlockPos();
			World world = this.client.world;
			if (world.getBlockState(blockPos).getBlock() != null
					&& (world.getBlockState(blockPos).getBlock() instanceof ChestBlock
					|| world.getBlockState(blockPos).getBlock() instanceof EnderChestBlock
					|| world.getBlockState(blockPos).getBlock() instanceof HopperBlock)) {
				return containerColor.get();
			}
		}
		return defaultColor.get();
	}

	@Override
	public void renderPlaceholder(float delta) {
		// Shouldn't need this...
	}

	@Override
	public AnchorPoint getAnchor() {
		return AnchorPoint.MIDDLE_MIDDLE;
	}

	public enum Crosshair {
		CROSS, DOT, TEXTURE, CUSTOM
	}
}
