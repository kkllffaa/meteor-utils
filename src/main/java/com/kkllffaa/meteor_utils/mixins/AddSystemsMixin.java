package com.kkllffaa.meteor_utils.mixins;

import com.kkllffaa.meteor_utils.Addon;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = Systems.class, remap = false)
public abstract class AddSystemsMixin {
	
	@Final
	@Shadow
	private static final Map<Class<? extends System<?>>, System<?>> systems = new HashMap<>();
	
	
	@Shadow
	private static System<?> add(System<?> system) {
		throw new NotImplementedException("mixin not working XD");
	}
	
	@Inject(method = "init", at = @At("TAIL"))
	private static void addCustomSystems(CallbackInfo ci) {
		
		Addon.mySystems.forEach(AddSystemsMixin::add);
		
		for (System<?> system : systems.values()) {
			if (Addon.mySystems.contains(system)) {
				MeteorClient.LOG.info("initializing custom system: " + system.getClass().getSimpleName());
				system.init();
			}
		}
	}
}
