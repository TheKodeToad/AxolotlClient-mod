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

package io.github.axolotlclient.mixin;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(ScreenshotUtils.class)
public abstract class ScreenshotUtilsMixin {

	private static File currentFile;

	@Redirect(method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/gl/Framebuffer;)Lnet/minecraft/text/Text;", at = @At(value = "INVOKE", target = "Ljava/io/File;getName()Ljava/lang/String;"))
	private static String axolotlclient$getImageFile(File instance) {
		currentFile = instance;
		return instance.getName();
	}

	@Inject(method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/gl/Framebuffer;)Lnet/minecraft/text/Text;", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
	private static void axolotlclient$onScreenshotSaveSuccess(File parent, String name, int textureWidth, int textureHeight,
															  Framebuffer buffer, CallbackInfoReturnable<Text> cir) {
		cir.setReturnValue(io.github.axolotlclient.modules.screenshotUtils.ScreenshotUtils.getInstance()
				.onScreenshotTaken(cir.getReturnValue(), currentFile));
	}
}
