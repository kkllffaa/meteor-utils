package com.kkllffaa.meteorutils.mixins;

import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.Map;

@Mixin(value = Waypoints.class, remap = false)
public abstract class WaypointsSystemMixin {
	@Shadow public Map<String, Waypoint> waypoints;
	
	@Shadow public abstract File getFile();
	
	@Inject(method = "toTag", at = @At("HEAD"), cancellable = true)
	private void noSaveWhenEmpty(CallbackInfoReturnable<NbtCompound> cir) {
		if (waypoints.isEmpty()) {
			getFile().delete();
			cir.setReturnValue(null);
		}
	}
}
