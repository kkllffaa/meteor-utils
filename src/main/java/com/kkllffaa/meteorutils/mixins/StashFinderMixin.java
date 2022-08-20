package com.kkllffaa.meteorutils.mixins;

import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.world.StashFinder;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = StashFinder.class, remap = false)
public abstract class StashFinderMixin extends Module {
	
	@Shadow @Final private final SettingGroup sgGeneral;
	@Shadow public List<StashFinder.Chunk> chunks;
	
	private Setting<Integer> ignoreWaypointDistance;
	private Setting<Integer> ignoreNeighbourDistance;
	private Setting<Boolean> noMessageWhenEquals;
	
	public StashFinderMixin(Category category, String name, String description) {
		super(category, name, description);
		throw new NotImplementedException("mixin");
	}
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(CallbackInfo ci) {
		ignoreWaypointDistance =  sgGeneral.add(new IntSetting.Builder()
				.name("ignore-if-waypoint")
				.description("ignore chunk if distance to nearest waypoint is below this number in blocks")
				.defaultValue(0)
				.min(0).max(400)
				.build()
		);
		ignoreNeighbourDistance =  sgGeneral.add(new IntSetting.Builder()
				.name("ignore-if-neighbour")
				.description("ignore chunk if distance to existing stash if below this number in chunks.")
				.defaultValue(0)
				.min(0).max(10)
				.build()
		);
		noMessageWhenEquals = sgGeneral.add(new BoolSetting.Builder()
				.name("no notification when equals")
				.description("dont send notification when chunk is alredy saved.")
				.defaultValue(true)
				.build()
		);
	}
	
	
	
	@Inject(method = "onChunkData", at = @At("HEAD"), cancellable = true)
	private void debil(ChunkDataEvent event, CallbackInfo ci) {
		
		for (StashFinder.Chunk chunk : chunks) {
			if (noMessageWhenEquals.get() && chunk.chunkPos.equals(event.chunk.getPos())) cancelMessage = true;
			
			if (chunk.chunkPos.getChebyshevDistance(event.chunk.getPos()) < ignoreNeighbourDistance.get()) {
				ci.cancel();
				return;
			}
		}
		
		for (Waypoint waypoint : Waypoints.get()) {
			if (waypoint.dimension.get() == Dimension.Overworld && new Vec3i(waypoint.pos.get().getX(), waypoint.pos.get().getY(), 0).isWithinDistance(event.chunk.getPos().getCenterAtY(0), ignoreWaypointDistance.get())) {
				ci.cancel();
				return;
			}
		}
	}
	
	
	
	private boolean cancelMessage;
	
	@Inject(method = "onChunkData", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/systems/modules/world/StashFinder;saveCsv()V", shift = At.Shift.AFTER), cancellable = true)
	private void debil2(ChunkDataEvent event, CallbackInfo ci) {
		if (cancelMessage) ci.cancel();
		cancelMessage = false;
	}
	@Inject(method = "onChunkData", at = @At(value = "RETURN"))
	private void reset(ChunkDataEvent event, CallbackInfo ci) {cancelMessage = false;}
	
}
