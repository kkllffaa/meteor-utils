package com.kkllffaa.meteorutils.mixins;

import com.kkllffaa.meteorutils.modules.NoPortalHitbox;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetherPortalBlock.class)
public abstract class NoPortalHitboxMixin {

	@Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
	private void getshape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context,
			CallbackInfoReturnable<VoxelShape> cir) {
		Modules modules = Modules.get();
		if (modules == null)
			return;
		if (Modules.get().isActive(NoPortalHitbox.class)) {
			cir.setReturnValue(Shapes.empty());
		}
	}
}
