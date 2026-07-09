package my.meteor.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.phys.Vec3;

public class AntiTntRun extends Module {
    // Храним позицию, куда нужно убежать
    private Vec3 escapeTarget = null;

    public AntiTntRun() {
        // Подключаем к вашей кастомной категории из главного класса AddonTemplate
        super(my.meteor.addon.AddonTemplate.CATEGORY, "anti-tnt-run", "Автоматически убегает от активированного ТНТ в радиусе 8 блоков.");
    }

    @Override
    public void onDeactivate() {
        // Сбрасываем цель при выключении модуля
        escapeTarget = null;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // Ищем ближайший активированный ТНТ в радиусе 8 блоков
        PrimedTnt targetTnt = null;
        double closestDistance = 8.0; // Максимальное расстояние обнаружения

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof PrimedTnt tnt) {
                double distance = mc.player.distanceTo(tnt);
                if (distance <= closestDistance) {
                    closestDistance = distance;
                    targetTnt = tnt;
                }
            }
        }

        // Если ТНТ найден рядом
        if (targetTnt != null) {
            Vec3 playerPos = mc.player.position();
            Vec3 tntPos = targetTnt.position();

            // Считаем вектор направления ОТ динамита К игроку
            Vec3 directionAway = playerPos.subtract(tntPos).normalize();

            // Рассчитываем финальную точку побега: позиция игрока + 16 блоков в противоположную сторону
            escapeTarget = playerPos.add(directionAway.scale(16.0));
        }

        // Логика автоматического бега к цели
        if (escapeTarget != null) {
            double distanceToTarget = mc.player.position().distanceTo(escapeTarget);

            // Если мы уже убежали на безопасное расстояние, останавливаемся
            if (distanceToTarget < 1.0 || closestDistance > 16.0) {
                escapeTarget = null;
                return;
            }

            // Заставляем игрока смотреть в сторону точки побега
            double diffX = escapeTarget.x - mc.player.getX();
            double diffZ = escapeTarget.z - mc.player.getZ();
            float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
            
            mc.player.setYRot(yaw);

            // Имитируем нажатие клавиши «Вперед» и бег
            mc.options.keyUp.setDown(true);
            mc.player.setSprinting(true);
        }
    }
}