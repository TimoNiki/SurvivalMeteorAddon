package my.meteor.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class TntGrief extends Module {
    public TntGrief() {
        super(Categories.Misc, "tnt-grief", "Ставит и поджигает ТНТ под вами (выдает вещи в 1 и 2 слот).");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.world == null || mc.player == null) return;

        BlockPos posUnder = mc.player.getBlockPos().down();

        // Проверяем и выдаем ТНТ в 1 слот, огниво во 2 слот
        ensureItemInSlot(Items.TNT, 0);
        ensureItemInSlot(Items.FLINT_AND_STEEL, 1);

        if (mc.world.getBlockState(posUnder).isAir()) {
            mc.player.getInventory().selectedSlot = 0; // Переключились на ТНТ
            placeBlock(posUnder);
            
            mc.player.getInventory().selectedSlot = 1; // Переключились на огниво
            placeBlock(posUnder);
        }
    }

    private void ensureItemInSlot(net.minecraft.item.Item item, int targetSlot) {
        if (mc.player.getInventory().getStack(targetSlot).getItem() != item) {
            mc.player.networkHandler.sendPacket(new net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket(
                36 + targetSlot, new ItemStack(item, 64)
            ));
        }
    }

    private void placeBlock(BlockPos pos) {
        BlockHitResult hit = new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
    }
}
