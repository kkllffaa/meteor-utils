package com.kkllffaa.meteorutils.modules;

import com.kkllffaa.meteorutils.Addon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;

public class ShulkerDupe extends Module {
	public ShulkerDupe() {
		super(Addon.CATEGORY, "ShulkerDupe", "shulker dupe exploit ported from Coderx-Gamer");
	}
	
	public ShouldDupe shoulddupe = ShouldDupe.NO;
	
	@Override
	public void onActivate() {
		shoulddupe = ShouldDupe.NO;
	}
	
	
	@EventHandler
	public void OnPackedSend(PacketEvent.Sent event) {
		if (mc.player != null && event.packet instanceof PlayerActionC2SPacket actionC2SPacket) {
			if (actionC2SPacket.getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
				if (shoulddupe == ShouldDupe.ONE) {
					InvUtils.quickMove().slotId(0);
					shoulddupe = ShouldDupe.NO;
				} else if (shoulddupe == ShouldDupe.ALL) {
					for (int i = 0; i < 27; i++) {
						InvUtils.quickMove().slotId(i);
					}
					shoulddupe = ShouldDupe.NO;
				}
			}
		}
	}
	
	@EventHandler
	public void OnTick(TickEvent.Post event) {
		if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
		
		if (shoulddupe != ShouldDupe.NO) {
			HitResult hit = mc.crosshairTarget;
			
			
			
			if (hit instanceof BlockHitResult blockHit) {
				
				if (mc.world.getBlockState(blockHit.getBlockPos()).getBlock() instanceof ShulkerBoxBlock && (mc.player.currentScreenHandler instanceof ShulkerBoxScreenHandler)) {
					mc.interactionManager.updateBlockBreakingProgress(blockHit.getBlockPos(), Direction.DOWN);
				} else {
					info("You need to have a shulker box screen open and look at a shulker box.");
					mc.player.closeHandledScreen();
					shoulddupe = ShouldDupe.NO;
				}
			}
		}
	}
	
	public enum ShouldDupe {
		NO,
		ONE,
		ALL
	}
	
}
