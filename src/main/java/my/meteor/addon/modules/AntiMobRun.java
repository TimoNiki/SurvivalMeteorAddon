package my.meteor.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.Vec3;

public class AntiMobRun extends Module {
    // Храним точку, в которую нужно убежать
    private Vec3 escapeTarget = null;

    public AntiMobRun() {
        // Подключаем к вашей кастомной категории из главного класса AddonTemplate
        super(my.meteor.addon.AddonTemplate.CATEGORY, "anti-mob-run", "Автоматически убегает от враждебных мобов в радиусе 8 блоков и перепрыгивает препятствия.");
    }

    @Override
    public void onDeactivate() {
        // Сбрасываем цель и клавиши движения при выключении модуля
        escapeTarget = null;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options != null) {
            mc.options.keyUp.setDown(false);
            mc.options.keyJump.setDown(false);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // 1. Ищем ближайшего враждебного моба в радиусе 8 блоков
        Entity closestMonster = null;
        double closestDistance = 8.0;

        for (Entity entity : mc.level.entitiesForRendering()) {
            // Проверяем, является ли сущность врагом (Enemy) и жива ли она
            if (entity instanceof Enemy && entity.isAlive()) {
                double distance = mc.player.distanceTo(entity);
                if (distance <= closestDistance) {
                    closestDistance = distance;
                    closestMonster = entity;
                }
            }
        }

        // 2. Если враг рядом — рассчитываем точку побега на 16 блоков вперед
        if (closestMonster != null) {
            Vec3 playerPos = mc.player.position();
            Vec3 mobPos = closestMonster.position();

            // Считаем вектор направления ОТ моба К игроку
            Vec3 directionAway = playerPos.subtract(mobPos).normalize();
            escapeTarget = playerPos.add(directionAway.scale(16.0));
        }

        // 3. Логика авто-бега и прыжков
        if (escapeTarget != null) {
            // Рассчитываем дистанцию до точки побега
            double distanceToTarget = mc.player.position().distanceTo(escapeTarget);

            // Если прибежали к безопасной точке ближе, чем на 1.5 блока — останавливаемся
            if (distanceToTarget < 1.5) {
                escapeTarget = null;
                mc.options.keyUp.setDown(false);
                mc.options.keyJump.setDown(false);
                return;
            }

            // Рассчитываем углы и поворачиваем камеру игрока в сторону бега
            double diffX = escapeTarget.x - mc.player.getX();
            double diffZ = escapeTarget.z - mc.player.getZ();
            float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
            
            mc.player.setYRot(yaw);

            // Зажимаем кнопку ходьбы вперед и принудительно включаем спринт
            mc.options.keyUp.setDown(true);
            mc.player.setSprinting(true);

            // --- ЛОГИКА АВТО-ПРЫЖКА ---
            // Получаем координаты блока прямо перед лицом игрока по направлению его взгляда
            BlockPos posInFront = mc.player.blockPosition().relative(mc.player.getDirection());
            
            // Если игрок ударился о стену горизонтальной коллизией или перед ним сплошной твердый блок
            if (mc.player.horizontalCollision || mc.level.getBlockState(posInFront).isCollisionShapeFullBlock(mc.level, posInFront)) {
                mc.options.keyJump.setDown(true); // Прыгаем
            } else {
                mc.options.keyJump.setDown(false); // Отпускаем пробел, если путь чист
            }
        }
    }
}