package com.franek.meteor_tweaks.modules;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.meteor.MouseButtonEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.misc.input.KeyAction;
import net.minecraft.util.Hand;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Ble extends Module {




    public Ble() {
        super(Categories.Player, "Ble", "");
    }





    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action != KeyAction.Press || event.button != GLFW_MOUSE_BUTTON_LEFT || mc.currentScreen != null) return;


        event.cancel();





        mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);

    }
}
