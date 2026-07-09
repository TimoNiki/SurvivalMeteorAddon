package my.meteor.addon.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;

public class AutoReply extends Module {
    private final String triggerWord = "привет"; 
    private final String replyText = "Привет! Я тут, играю."; 

    private int ticksSinceJoin = 0; 
    private boolean wasInWorld = false; 

    public AutoReply() {
        super(my.meteor.addon.AddonTemplate.CATEGORY, "auto-reply", "Автоматически отвечает в чат по хитрым правилам времени.");
    }

    @Override
    public void onActivate() {
        ticksSinceJoin = 0;
        wasInWorld = false;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            wasInWorld = false; 
            return;
        }

        if (!wasInWorld) {
            ticksSinceJoin = 0; 
            wasInWorld = true;
        }

        if (ticksSinceJoin < 280) {
            ticksSinceJoin++;
        }
    }

    @EventHandler
    private void onReceiveMessage(ReceiveMessageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // ИСПРАВЛЕНО: Полностью удален вызов getMessage().
        // Мы берем строковое представление самого ивента, куда Meteor Client 
        // автоматически записывает весь текст входящего сообщения.
        String message = event.toString().toLowerCase();

        String myName = mc.player.getGameProfile().name().toLowerCase();

        // Защита от само-ответа (чтобы чит не отвечал сам себе)
        if (message.contains("<" + myName + ">") || message.startsWith(myName + ":")) return;

        // ПРАВИЛО 1: Если 14 секунд еще НЕ истекли
        if (ticksSinceJoin < 280) {
            if (message.contains(triggerWord.toLowerCase())) {
                sendReply();
            }
        } 
        // ПРАВИЛО 2: Если 14 секунд УЖЕ истекли
        else {
            if (message.contains(myName)) {
                sendReply();
            }
        }
    }

    private void sendReply() {
        ChatUtils.sendPlayerMsg(replyText);
        ticksSinceJoin = 0; 
    }
}
