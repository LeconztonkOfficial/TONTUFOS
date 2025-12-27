package tontufosmp2.items;

import net.minecraft.util.Rarity;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;


import java.util.List;

public class TiempoComprimidoItem extends Item {

    private static final int RADIO = 5;
    private static final int USOS = 64;
    private static final int COOLDOWN = 60;

    public TiempoComprimidoItem(Settings settings) {
        super(settings.maxDamage(USOS));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {

        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient) {
            return TypedActionResult.success(stack);
        }

        ServerWorld serverWorld = (ServerWorld) world;
        BlockPos center = player.getBlockPos();

        // 🌱 ACELERAR BLOQUES
        for (BlockPos pos : BlockPos.iterate(
                center.add(-RADIO, -1, -RADIO),
                center.add(RADIO, 3, RADIO))) {

            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            // Cultivos y plantas con crecimiento
            if (block instanceof Fertilizable fertilizable) {
                for (int i = 0; i < 4; i++) {
                    if (fertilizable.isFertilizable(world, pos, state, false)) {
                        fertilizable.grow(serverWorld, world.random, pos, state);
                    }
                }
            }

            // Caña de azúcar y bambú
            if (block instanceof SugarCaneBlock || block instanceof BambooBlock) {
                block.randomTick(state, serverWorld, pos, world.random);
            }

            // Árboles pequeños (saplings)
            if (block instanceof SaplingBlock) {
                ((SaplingBlock) block).generate(serverWorld, pos, state, world.random);
            }
        }

        // 🐄 ACELERAR ANIMALES
        List<AnimalEntity> animals = world.getEntitiesByClass(
                AnimalEntity.class,
                new Box(center).expand(RADIO),
                Entity::isAlive
        );

        for (AnimalEntity animal : animals) {
            if (animal.isBaby()) {
                animal.growUp(1000);
            }
        }

        // ✨ EFECTOS VISUALES
        serverWorld.spawnParticles(
                ParticleTypes.HAPPY_VILLAGER,
                center.getX() + 0.5,
                center.getY() + 1,
                center.getZ() + 0.5,
                80, 2, 2, 2, 0.15
        );

        // 🔊 SONIDO
        world.playSound(
                null,
                center,
                SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
        );

        // ⏳ COOLDOWN
        player.getItemCooldownManager().set(this, COOLDOWN);

        // 🔧 DAÑO (USO)
        stack.damage(1, player,
                p -> p.sendToolBreakStatus(hand));

        return TypedActionResult.success(stack);
    }
}