package tontufosmp2.items;




import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MazojgornetItem extends Item {



    private static final String HIT_COUNT = "HitCount";
    private static final int REQUIRED_HITS = 3;
    private static final int COOLDOWN_TICKS = 10;
    private static final int HUNGER_COST = 1;

    public MazojgornetItem(Settings settings) {
        super(settings);
    }



    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;

        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;

        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        // Solo piedra y minerales
        if (!state.isIn(BlockTags.PICKAXE_MINEABLE)) {
            return ActionResult.PASS;
        }

        // Consumo de hambre
        if (player.getHungerManager().getFoodLevel() <= 0) {
            return ActionResult.FAIL;
        }
        player.getHungerManager().add(-HUNGER_COST, 0.0F);

        ItemStack stack = context.getStack();
        NbtCompound nbt = stack.getOrCreateNbt();

        int hits = nbt.getInt(HIT_COUNT) + 1;
        nbt.putInt(HIT_COUNT, hits);

        // Daño visual del bloque (grietas)
        mostrarProgresoBloque(world, player, pos, hits);

        // Cooldown + peso del mazo
        player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
        aplicarRetroceso(player);

        // Sonido de impacto
        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_ANVIL_HIT,
                SoundCategory.PLAYERS,
                1.0F,
                0.8F
        );

        // Partículas
        spawnImpactParticles(world, pos, state);

        // Aún no se rompe
        if (hits < REQUIRED_HITS) {
            return ActionResult.FAIL;
        }

        // Reset de golpes
        nbt.putInt(HIT_COUNT, 0);

        // Romper área
        romperArea3x3(context, player, stack);

        return ActionResult.SUCCESS;
    }

    /* ROMPER AREA 3x3*/

    private void romperArea3x3(ItemUsageContext context, PlayerEntity player, ItemStack stack) {

        World world = context.getWorld();
        BlockPos center = context.getBlockPos();
        Direction face = context.getSide();

        int bloquesRotos = 0;

        for (BlockPos pos : obtenerArea3x3(center, face)) {

            BlockState state = world.getBlockState(pos);

            if (state.isAir()) continue;
            if (!state.isIn(BlockTags.PICKAXE_MINEABLE)) continue;
            if (state.getHardness(world, pos) < 0) continue;

            // Limpiar daño visual
            if (world instanceof ServerWorld server) {
                server.setBlockBreakingInfo(player.getId(), pos, -1);
            }

            world.breakBlock(pos, true, player);
            bloquesRotos++;
        }

        // Desgaste proporcional
        stack.damage(
                bloquesRotos,
                player,
                p -> p.sendToolBreakStatus(context.getHand())
        );
    }



    private void mostrarProgresoBloque(World world, PlayerEntity player, BlockPos pos, int hits) {

        if (!(world instanceof ServerWorld server)) return;

        // 3 golpes → progreso 0–9
        int stage = Math.min(9, hits * 3);

        server.setBlockBreakingInfo(
                player.getId(),
                pos,
                stage
        );
    }

    /* =========================
       PARTICULAS
       ========================= */

    private void spawnImpactParticles(World world, BlockPos pos, BlockState state) {

        if (!(world instanceof ServerWorld server)) return;

        server.spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                12,
                0.2, 0.2, 0.2,
                0.05
        );
    }

    /*  RETROCESO / PESO*/

    private void aplicarRetroceso(PlayerEntity player) {
        Vec3d look = player.getRotationVec(1.0F);
        player.addVelocity(
                -look.x * 0.25,
                0.05,
                -look.z * 0.25
        );
        player.velocityModified = true;
    }

    /*AREA 3x3 ORIENTADA */

    private Iterable<BlockPos> obtenerArea3x3(BlockPos center, Direction face) {

        List<BlockPos> positions = new ArrayList<>();
        Direction.Axis axis = face.getAxis();

        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {

                BlockPos offset;

                if (axis == Direction.Axis.Y) {
                    offset = center.add(a, 0, b);
                } else if (axis == Direction.Axis.X) {
                    offset = center.add(0, a, b);
                } else {
                    offset = center.add(a, b, 0);
                }

                positions.add(offset);
            }
        }
        return positions;
    }
}