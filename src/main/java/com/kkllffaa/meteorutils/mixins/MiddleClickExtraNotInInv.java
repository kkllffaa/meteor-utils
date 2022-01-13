package com.kkllffaa.meteorutils.mixins;


import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.systems.modules.player.MiddleClickExtra;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = MiddleClickExtra.class, remap = false)
public abstract class MiddleClickExtraNotInInv {
	@Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
	private void cancelIfInInventory(MouseButtonEvent event, CallbackInfo ci){
		if (mc.currentScreen != null) ci.cancel();
	}
}
