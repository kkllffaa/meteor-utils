package com.franek.meteor_tweaks.mixins;

import com.franek.meteor_tweaks.Addon;
import com.franek.meteor_tweaks.modules.ChatFilter;
import minegame159.meteorclient.events.game.ReceiveMessageEvent;
import minegame159.meteorclient.mixin.ChatHudAccessor;
import minegame159.meteorclient.systems.modules.Modules;
import minegame159.meteorclient.systems.modules.misc.BetterChat;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import static minegame159.meteorclient.utils.Utils.mc;

@Mixin(BetterChat.class)
public abstract class ChatFilterMixin {
    @Inject(method = "onMessageRecieve",at = @At("HEAD"),remap = false, cancellable = true)
    private void filterMessage(ReceiveMessageEvent event, CallbackInfo ci){

        if (!Modules.get().get(ChatFilter.class).isActive()) return;



        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getVisibleMessages().removeIf((message) -> message.getId() == event.id && event.id != 0);
        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getMessages().removeIf((message) -> message.getId() == event.id && event.id != 0);

        Text message = event.message;

        Addon.LOG.info(message.getSiblings());
        Addon.LOG.info(event.message.getSiblings());





/*
        for (String msg : Modules.get().get(ChatFilter.class).messages){
            if (StringUtils.containsIgnoreCase(event.message.getString(),msg)){

                event.cancel();
                Addon.LOG.info(event.message);
                Addon.LOG.info("debil");
                break;
            }
        }
        */
    }
}
