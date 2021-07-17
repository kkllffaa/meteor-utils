package com.franek.meteor_tweaks.mixins;

import minegame159.meteorclient.rendering.ShapeMode;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.systems.modules.world.AirPlace;
import minegame159.meteorclient.utils.render.color.SettingColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AirPlace.class, remap = false)
public interface AirPlaceAccessor {
	@Accessor(value = "sideColor")
	Setting<SettingColor> sideColor();
	@Accessor(value = "lineColor")
	Setting<SettingColor> lineColor();
	@Accessor(value = "shapeMode")
	Setting<ShapeMode> shapeMode();
	@Accessor(value = "render")
	Setting<Boolean> render();
}
