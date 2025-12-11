package tontufosmp2.entities;



import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import tontufosmp2.blocks.ModBlocks;
import tontufosmp2.items.ModItems;

public class ThrowableLightEntity extends ThrownItemEntity {

    // Constructor usado por Fabric
    public ThrowableLightEntity(EntityType<? extends ThrowableLightEntity> type, World world) {
        super(type, world);
    }

    // Constructor para lanzar desde el jugador
    public ThrowableLightEntity(World world, PlayerEntity owner) {
        super(ModEntities.THROWABLE_LIGHT, owner, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient) {
            // Coloca bloque de luz al impactar
            this.getWorld().setBlockState(this.getBlockPos(), ModBlocks.LIGHT_MAGIC_BLOCK.getDefaultState());
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.POCIONLUZ;
    }
}