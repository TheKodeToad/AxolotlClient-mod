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

package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlClientConfig.options.Option;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.gui.layout.Justification;
import io.github.axolotlclient.modules.hud.util.DefaultOptions;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class MemoryHud extends TextHudEntry implements DynamicallyPositionable {

	public static final Identifier ID = new Identifier("axolotlclient", "memoryhud");

	protected final EnumOption justification = new EnumOption("justification", Justification.values(),
			Justification.CENTER.toString());
	protected final EnumOption anchor = DefaultOptions.getAnchorPoint();

	private final Rectangle graph = new Rectangle(0, 0, 0, 0);
	private final ColorOption graphUsedColor = new ColorOption("graphUsedColor",
			Color.SELECTOR_RED.withAlpha(255));
	private final ColorOption graphFreeColor = new ColorOption("graphFreeColor",
			Color.SELECTOR_GREEN.withAlpha(255));

	private final BooleanOption showGraph = new BooleanOption("showGraph", true);
	private final BooleanOption showText = new BooleanOption("showText", false);
	private final BooleanOption showAllocated = new BooleanOption("showAllocated", false);

	public MemoryHud() {
		super(150, 27, true);
	}

	@Override
	public void renderComponent(float delta) {
		DrawPosition pos = getPos();

		if (showGraph.get()) {
			graph.setData(pos.x + 5, pos.y + 5, getBounds().width - 10, getBounds().height - 10);

			fill(graph.x, graph.y, (int) (graph.x + graph.width * (getUsage())), graph.y + graph.height,
					graphUsedColor.get().getAsInt());
			fill((int) (graph.x + graph.width * (getUsage())), graph.y, graph.x + graph.width, graph.y + graph.height,
					graphFreeColor.get().getAsInt());

			outlineRect(graph, Color.BLACK);
		}

		if (showText.get()) {
			String mem = getMemoryLine();
			drawString(mem, pos.x + Justification.valueOf(justification.get()).getXOffset(client.textRenderer.getStringWidth(mem), getWidth() - 4) + 2,
					pos.y + (Math.round((float) height / 2) - 4) - (showAllocated.get() ? 4 : 0),
					textColor.get().getAsInt(), shadow.get());

			if (showAllocated.get()) {
				String alloc = getAllocationLine();
				drawString(alloc, pos.x + Justification.valueOf(justification.get()).getXOffset(client.textRenderer.getStringWidth(alloc), getWidth() - 4) + 2, pos.y + (Math.round((float) height / 2) - 4) + 4,
						textColor.get().getAsInt(), shadow.get());
			}
		}
	}

	@Override
	public void renderPlaceholderComponent(float delta) {
		DrawPosition pos = getPos();

		if (showGraph.get()) {
			graph.setData(pos.x + 5, pos.y + 5, getBounds().width - 10, getBounds().height - 10);

			fill(graph.x, graph.y, (int) (graph.x + graph.width * (0.3)), graph.y + graph.height,
					graphUsedColor.get().getAsInt());
			fill((int) (graph.x + graph.width * (0.3)), graph.y, graph.x + graph.width, graph.y + graph.height,
					graphFreeColor.get().getAsInt());

			outlineRect(graph, Color.BLACK);
		}

		if (showText.get()) {
			String mem = "300MiB/1024MiB";
			drawString(mem, pos.x + Justification.valueOf(justification.get()).getXOffset(client.textRenderer.getStringWidth(mem), getWidth() - 4) + 2,
					pos.y + (Math.round((float) height / 2) - 4) - (showAllocated.get() ? 4 : 0), Color.WHITE,
					shadow.get());
			if (showAllocated.get()) {
				String alloc = I18n.translate("allocated") + ": 976MiB";
				drawString(alloc, pos.x + Justification.valueOf(justification.get()).getXOffset(client.textRenderer.getStringWidth(alloc), getWidth() - 4) + 2,
						pos.y + (Math.round((float) height / 2) - 4) + 4, textColor.get(), shadow.get());
			}
		}

		if (!showGraph.get() && !showText.get()) {
			String value = I18n.translate(ID.getPath());
			drawString(value, pos.x + Justification.valueOf(justification.get()).getXOffset(client.textRenderer.getStringWidth(value), getWidth() - 4) + 2,
					pos.y + (Math.round((float) height / 2) - 4), Color.WHITE,
					shadow.get());
		}
	}

	private String getMemoryLine() {
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long used = total - free;

		return toMiB(used) + "/" + toMiB(max) + " (" + ((int) (getUsage() * 100)) + "%)";
	}

	private String getAllocationLine() {
		long total = Runtime.getRuntime().totalMemory();

		return I18n.translate("allocated") + ": " + toMiB(total);
	}

	private float getUsage() {
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long used = total - free;
		return (float) used / max;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(justification);
		options.add(anchor);
		options.add(showGraph);
		options.add(graphUsedColor);
		options.add(graphFreeColor);
		options.add(showText);
		options.add(showAllocated);
		return options;
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public boolean movable() {
		return true;
	}

	private static String toMiB(long bytes) {
		return (bytes / 1024L / 1024L) + "MiB";
	}

	@Override
	public AnchorPoint getAnchor() {
		return AnchorPoint.valueOf(anchor.get());
	}
}
