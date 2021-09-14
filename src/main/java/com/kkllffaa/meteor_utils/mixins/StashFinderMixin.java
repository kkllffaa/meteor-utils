package com.kkllffaa.meteor_utils.mixins;

import meteordevelopment.meteorclient.systems.modules.world.StashFinder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = StashFinder.class, remap = false)
public abstract class StashFinderMixin/* extends Module*/ {
	/*
	@Shadow @Final private SettingGroup sgGeneral;
	
	@Shadow public List<StashFinder.Chunk> chunks;
	private Setting<Integer> ignorewaypointdistance;
	private Setting<Integer> ignoreneighbourdistance;
	
	public StashFinderMixin(Category category, String name, String description) {
		super(category, name, description);
		throw new NotImplementedException("mixin");
	}
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(CallbackInfo ci) {
		ignorewaypointdistance =  sgGeneral.add(new IntSetting.Builder()
				.name("minimum-distance")
				.description("The minimum distance you must be from spawn to record a certain chunk.")
				.defaultValue(0)
				.min(0)
				.sliderMax(10000)
				.build()
		);
		ignoreneighbourdistance =  sgGeneral.add(new IntSetting.Builder()
				.name("minimum-distance")
				.description("The minimum distance you must be from spawn to record a certain chunk.")
				.defaultValue(0)
				.min(0)
				.sliderMax(10000)
				.build()
		);
	}
	
	@Inject(method = "onChunkData", at = @At("HEAD"))
	private void debil(ChunkDataEvent event, CallbackInfo ci) {
		chunks.forEach(chunk -> {
			info(String.valueOf(chunk.chunkPos.getChebyshevDistance(event.chunk.getPos())));
			//if (chunk.chunkPos.getChebyshevDistance(event.chunk.getPos()) > 1) {
			
			//}
		});
	}
	*/
}
