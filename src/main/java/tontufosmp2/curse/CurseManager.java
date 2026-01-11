package tontufosmp2.curse;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class CurseManager {

    public static void addCurse(ServerPlayerEntity player, String curseId, long activateTime) {
        ServerWorld world = player.getServerWorld();
        CurseSaveState.get(world)
                .setCurse(player.getUuid(), new CurseData(curseId, activateTime));
    }

    public static CurseData get(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        return CurseSaveState.get(world).getCurse(player.getUuid());
    }

    public static void remove(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        CurseSaveState.get(world).remove(player.getUuid());
    }

    public static boolean hasCurse(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        return CurseSaveState.get(world).getCurse(player.getUuid()) != null;
    }
}
