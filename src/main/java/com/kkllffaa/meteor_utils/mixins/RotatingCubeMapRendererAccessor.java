package com.kkllffaa.meteor_utils.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RotatingCubeMapRenderer.class)
public interface RotatingCubeMapRendererAccessor {
	
	@Accessor float getTime();
	@Accessor void setTime(float time);
	@Accessor MinecraftClient getClient();
	
}
