package my.meteor.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TntGrief extends Module {
    public TntGrief() {
        super(my.meteor.addon.AddonTemplate.CATEGORY, "tnt-grief", "Ставит и поджигает ТНТ под вами.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.gameMode == null) return;

        BlockPos posUnder = mc.player.blockPosition().below();

        ensureItemInSlot(mc, Items.TNT, 0);
        ensureItemInSlot(mc, Items.FLINT_AND_STEEL, 1);

                if (mc.level.getBlockState(posUnder).isAir()) {
            // ИСПРАВЛЕНО: Вместо .selected = 0 используем метод Майнкрафта
            mc.player.getInventory().pickSlot(0); 
            placeBlock(mc, posUnder);
            
            // ИСПРАВЛЕНО: Переключаемся на 2 слот (индекс 1) методом pickSlot
            mc.player.getInventory().pickSlot(1);
            placeBlock(mc, posUnder);
        }



    }

    private void ensureItemInSlot(Minecraft mc, Item item, int targetSlot) {
        if (mc.player.getInventory().getItem(targetSlot).getItem() != item) {
            mc.player.connection.send(new ServerboundSetCreativeModeSlotPacket(
                36 + targetSlot, new ItemStack(item, 64)
            ));
        }
    }

    private void placeBlock(Minecraft mc, BlockPos pos) {
        Vec3 hitVec = new Vec3(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        BlockHitResult hit = new BlockHitResult(hitVec, Direction.UP, pos, false);
        mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);
    }
}
