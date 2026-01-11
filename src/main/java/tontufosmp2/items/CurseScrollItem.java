package tontufosmp2.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import tontufosmp2.curse.CurseManager;

public class CurseScrollItem extends Item {

    private final String curseId;
    private static final int DELAY_TICKS = 3600; // 15 minutos

    public CurseScrollItem(Settings settings, String curseId) {
        super(settings);
        this.curseId = curseId;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {

        if (!world.isClient && player instanceof ServerPlayerEntity caster) {

            ServerPlayerEntity target = getTargetPlayer(caster);

            long activateTime = world.getTime() + DELAY_TICKS;
            CurseManager.addCurse(target, curseId, activateTime);

            // Consumir pergamino
            player.getStackInHand(hand).decrement(1);
        }

        return TypedActionResult.success(player.getStackInHand(hand), world.isClient());
    }

    /**
     * Devuelve el jugador al que se está mirando.
     * Si no hay ninguno, devuelve al propio jugador.
     */
    private ServerPlayerEntity getTargetPlayer(ServerPlayerEntity caster) {

        double range = 5.0D;

        Vec3d start = caster.getCameraPosVec(1.0F);
        Vec3d direction = caster.getRotationVec(1.0F);
        Vec3d end = start.add(direction.multiply(range));

        HitResult hit = caster.getWorld().raycast(
                new RaycastContext(
                        start,
                        end,
                        RaycastContext.ShapeType.OUTLINE,
                        RaycastContext.FluidHandling.NONE,
                        caster
                )
        );

        if (hit.getType() == HitResult.Type.ENTITY && hit instanceof EntityHitResult entityHit) {

            Entity entity = entityHit.getEntity();

            if (entity instanceof ServerPlayerEntity targetPlayer) {
                return targetPlayer;
            }
        }

        // Si no hay jugador objetivo → se maldice a sí mismo
        return caster;
    }
}
