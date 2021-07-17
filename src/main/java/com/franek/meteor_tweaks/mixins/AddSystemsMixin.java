package com.franek.meteor_tweaks.mixins;

import com.franek.meteor_tweaks.systems.chestmemory.ChestMemory;
import minegame159.meteorclient.systems.System;
import minegame159.meteorclient.systems.Systems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = Systems.class, remap = false)
public abstract class AddSystemsMixin {
	
	@SuppressWarnings("rawtypes")
	@Final
	@Shadow
	private static final Map<Class<? extends System>, System<?>> systems = new HashMap<>();
	
	
	@Shadow
	private static System<?> add(System<?> system) {
		throw new AssertionError();
	}
	
	@Inject(method = "init", at = @At("TAIL"))
	private static void debil(CallbackInfo ci) {
		
		List<System<?>> mySystems = new ArrayList<>();
		
		mySystems.add(new ChestMemory());
		
		
		
		
		
		for (System<?> sys : mySystems) {
			add(sys);
		}
		
		for (System<?> system : systems.values()) {
			//noinspection ConstantConditions
			if (mySystems.contains(system)) system.init();
		}
	}
}
