package com.franek.meteor_tweaks.mixins;

import minegame159.meteorclient.rendering.ShapeMode;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.systems.modules.world.AirPlace;
import minegame159.meteorclient.utils.render.color.SettingColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AirPlace.class)
public interface AirPlaceAccessor {
	@Accessor(value = "sideColor",remap = false)
	Setting<SettingColor> sideColor();
	@Accessor(value = "lineColor",remap = false)
	Setting<SettingColor> lineColor();
	@Accessor(value = "shapeMode",remap = false)
	Setting<ShapeMode> shapeMode();
	@Accessor(value = "render" ,remap = false)
	Setting<Boolean> render();
}
