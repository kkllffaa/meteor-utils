package com.kkllffaa.meteor_utils.mixins;

import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
	
	@Mutable @Shadow @Final private RotatingCubeMapRenderer backgroundRenderer;
	
	@Inject(method = "<init>(Z)V", at = @At("TAIL"))
	private void constructor(boolean doBackgroundFade, CallbackInfo ci) {
		//backgroundRenderer = new TitleScreenShaderRenderer(null);
	}
}
