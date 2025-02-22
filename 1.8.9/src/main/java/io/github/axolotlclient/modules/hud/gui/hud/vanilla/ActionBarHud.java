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

import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.options.Option;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import lombok.Getter;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class ActionBarHud extends TextHudEntry {

	public static final Identifier ID = new Identifier("kronhud", "actionbarhud");

	public IntegerOption timeShown = new IntegerOption("timeshown", 60, 40, 300);
	public BooleanOption customTextColor = new BooleanOption("customtextcolor", false);

	@Getter
	private String actionBar;
	private int ticksShown;
	private int color;
	private final String placeholder = "Action Bar";

	public ActionBarHud() {
		super(115, 13, false);
	}

	public void setActionBar(String bar, int color) {
		this.actionBar = bar;
		this.color = color;
	}

	@Override
	public void renderComponent(float delta) {
		if (ticksShown >= timeShown.get()) {
			this.actionBar = null;
		}
		Color vanillaColor = new Color(color);
		if (this.actionBar != null) {
			if (shadow.get()) {
				client.textRenderer
						.drawWithShadow(actionBar,
								(float) getPos().x() + Math.round((float) getWidth() / 2)
										- (float) client.textRenderer.getStringWidth(actionBar) / 2,
								(float) getPos().y() + 3,
								customTextColor.get()
										? (textColor.get().getAlpha() == 255
										? new Color(textColor.get().getRed(), textColor.get().getGreen(),
										textColor.get().getBlue(), vanillaColor.getAlpha()).getAsInt()
										: textColor.get().getAsInt())
										: color);
			} else {
				client.textRenderer
						.draw(actionBar,
								(float) getPos().x() + Math.round((float) getWidth() / 2)
										- ((float) client.textRenderer.getStringWidth(actionBar) / 2),
								(float) getPos().y() + 3,
								customTextColor.get()
										? (textColor.get().getAlpha() == 255
										? new Color(textColor.get().getRed(), textColor.get().getGreen(),
										textColor.get().getBlue(), vanillaColor.getAlpha()).getAsInt()
										: textColor.get().getAsInt())
										: color,
								false);
			}
			ticksShown++;
		} else {
			ticksShown = 0;
		}
	}

	@Override
	public void renderPlaceholderComponent(float delta) {
		client.textRenderer.draw(placeholder,
				(float) getPos().x() + Math.round((float) getWidth() / 2)
						- (float) client.textRenderer.getStringWidth(placeholder) / 2,
				(float) getPos().y() + 3, -1, shadow.get());
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(timeShown);
		options.add(customTextColor);
		return options;
	}
}
