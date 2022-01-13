package com.kkllffaa.meteorutils.mixins;

import com.kkllffaa.meteorutils.test.TitleScreenShaderRenderer;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
	
	@Redirect(method = "<init>(Z)V", at = @At(value = "NEW", target = "net/minecraft/client/gui/RotatingCubeMapRenderer"))
	private RotatingCubeMapRenderer ddd(CubeMapRenderer cubemap) {
		return new TitleScreenShaderRenderer(cubemap);
	}
	
	
	
}
