package com.kkllffaa.meteor_utils.mixins;

import com.kkllffaa.meteor_utils.Addon;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "meteordevelopment.meteorclient.gui.screens.ModulesScreen$WCategoryController", remap = false)
public abstract class AddCategoriesMixin extends WContainer {
	
	@Inject(method = "init", at = @At("TAIL"))
	private void addprofileswidget(CallbackInfo ci) {
		Addon.myWidgets.forEach(s -> s.accept(theme, this));
	}
}
