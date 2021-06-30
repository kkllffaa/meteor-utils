package com.franek.meteor_tweaks.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class Debil {
    @Mutable
    @Shadow
    @Final
    private boolean isDemo;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "<init>",at = @At("TAIL"),remap = false)
    private void init(RunArgs args, CallbackInfo ci){
        LOGGER.info("debil");
        //isDemo = true;
    }
}
