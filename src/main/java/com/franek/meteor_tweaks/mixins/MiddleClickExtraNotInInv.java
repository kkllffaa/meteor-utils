package com.franek.meteor_tweaks.mixins;


import minegame159.meteorclient.events.meteor.MouseButtonEvent;
import minegame159.meteorclient.systems.modules.player.MiddleClickExtra;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static minegame159.meteorclient.utils.Utils.mc;

@Mixin(MiddleClickExtra.class)
public abstract class MiddleClickExtraNotInInv {
	@Inject(method = "onMouseButton", at = @At("HEAD"),remap = false, cancellable = true)
	private void debil(MouseButtonEvent event, CallbackInfo ci){
		if (mc.currentScreen != null) ci.cancel();
	}
}
