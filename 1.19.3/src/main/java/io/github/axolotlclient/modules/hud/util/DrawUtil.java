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

package io.github.axolotlclient.modules.hud.util;

import io.github.axolotlclient.AxolotlClientConfig.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class DrawUtil extends DrawableHelper {

	public static void fillRect(MatrixStack matrices, Rectangle rectangle, Color color) {
		fillRect(matrices, rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.getAsInt());
	}

	public static void fillRect(MatrixStack matrices, int x, int y, int width, int height, Color color) {
		RenderUtil.drawRectangle(matrices, x, y, x + width, y + height, color.getAsInt());
	}

	public static void fillRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
		DrawableHelper.fill(matrices, x, y, x + width, y + height, color);
	}

	public static void outlineRect(MatrixStack matrices, Rectangle rectangle, Color color) {
		outlineRect(matrices, rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.getAsInt());
	}

	public static void outlineRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
		fillRect(matrices, x, y, 1, height - 1, color);
		fillRect(matrices, x + width - 1, y + 1, 1, height - 1, color);
		fillRect(matrices, x + 1, y, width - 1, 1, color);
		fillRect(matrices, x, y + height - 1, width - 1, 1, color);
	}

	public static void drawCenteredString(MatrixStack matrices, TextRenderer renderer, String text, int x, int y,
										  Color color, boolean shadow) {
		drawCenteredString(matrices, renderer, text, x, y, color.getAsInt(), shadow);
	}

	public static void drawCenteredString(MatrixStack matrices, TextRenderer renderer, String text, int x, int y,
										  int color, boolean shadow) {
		drawString(matrices, text, (float) (x - renderer.getWidth(text) / 2), (float) y, color, shadow);
	}

	public static void drawString(MatrixStack matrices, String text, float x, float y, Color color, boolean shadow) {
		drawString(matrices, text, x, y, color.getAsInt(), shadow);
	}

	public static void drawString(MatrixStack matrices, TextRenderer textRenderer, String text, float x, float y,
								  int color, boolean shadow) {
		drawString(matrices, text, x, y, color, shadow);
	}

	public static void drawString(MatrixStack matrices, String text, float x, float y, int color, boolean shadow) {
		if (shadow) {
			MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, text, x, y, color);
		} else {
			MinecraftClient.getInstance().textRenderer.draw(matrices, text, x, y, color);
		}
	}
}
