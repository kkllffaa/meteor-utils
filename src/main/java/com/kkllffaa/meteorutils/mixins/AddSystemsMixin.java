package com.kkllffaa.meteorutils.mixins;

import com.kkllffaa.meteorutils.Addon;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Systems.class, remap = false)
public abstract class AddSystemsMixin {
	
	
	@Shadow
	private static System<?> add(System<?> system) {
		throw new NotImplementedException("mixin not working XD");
	}
	
	@Inject(method = "init", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/systems/proxies/Proxies;<init>()V", shift = At.Shift.AFTER))
	private static void addCustomSystems(CallbackInfo ci) {
		for (System<?> system : Addon.mySystems) {
			add(system);
		}
	}
	
	@Redirect(method = "init", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/systems/System;init()V"))
	private static void logInit(System<?> system) {
		Addon.LOG.info("initializing system: "+system.getClass().getSimpleName());
		system.init();
	}
}
