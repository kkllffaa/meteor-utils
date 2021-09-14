package com.kkllffaa.meteor_utils.mixins;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
	
	//https://github.com/TheRandomLabs/RandomPatches/blob/1.16-fabric/src/main/java/com/therandomlabs/randompatches/mixin/client/PlayerEntityRendererMixin.java
	@Redirect(method = "setupTransforms", at = @At(value = "INVOKE", target = "Ljava/lang/Math;acos(D)D"))
	private double noNAN(double value) {
		//Sometimes, Math#acos(double) is called with a value larger than 1.0, which results in
		//a rotation angle of NaN, thus causing the player model to disappear.
		return Math.acos(Math.min(value, 1));
	}
	
}
