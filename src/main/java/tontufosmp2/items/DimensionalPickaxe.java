package tontufosmp2.items;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class DimensionalPickaxe extends PickaxeItem {

    public DimensionalPickaxe(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {

        if (world.isClient || !(miner instanceof PlayerEntity player)) {
            return super.postMine(stack, world, state, pos, miner);
        }

        if (state.isAir()) return false;

        RegistryKey<World> dimension = world.getRegistryKey();

        //  OVERWORLD → ITEM TOTALMENTE ALEATORIO
        if (dimension.equals(World.OVERWORLD)) {
            dropAnyRandomItem(world, pos);
        }

        //  NETHER y 🌌 END → DOBLE DROP NORMAL
        else if (dimension.equals(World.NETHER) || dimension.equals(World.END)) {
            dropDouble(world, pos, state, player);
        }

        //  Daño al pico
        stack.damage(1, miner, e -> e.sendToolBreakStatus(Hand.MAIN_HAND));

        return true;
    }

    //  DROPEA CUALQUIER ITEM DEL JUEGO
    private void dropAnyRandomItem(World world, BlockPos pos) {

        Random random = world.getRandom();

        ItemStack randomStack = Registries.ITEM
                .getRandom(random)
                .map(entry -> new ItemStack(entry.value()))
                .orElse(ItemStack.EMPTY);

        if (!randomStack.isEmpty()) {
            Block.dropStack(world, pos, randomStack);
        }
    }

    // DOBLE DROP NORMAL DEL BLOQUE
    private void dropDouble(World world, BlockPos pos, BlockState state, PlayerEntity player) {

        if (!(world instanceof ServerWorld serverWorld)) return;

        List<ItemStack> drops = Block.getDroppedStacks(
                state,
                serverWorld,
                pos,
                null,
                player,
                player.getMainHandStack()
        );

        for (ItemStack stack : drops) {
            if (!stack.isEmpty()) {
                ItemStack extra = stack.copy();
                Block.dropStack(world, pos, extra);
            }
        }
    }
}