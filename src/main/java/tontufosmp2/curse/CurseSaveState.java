package tontufosmp2.curse;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CurseSaveState extends PersistentState {

    private final Map<UUID, CurseData> curses = new HashMap<>();

    public static CurseSaveState get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                CurseSaveState::fromNbt,
                CurseSaveState::new,
                "tontufosmp2_curses"
        );
    }

    public void setCurse(UUID playerId, CurseData data) {
        curses.put(playerId, data);
        markDirty();
    }

    public CurseData getCurse(UUID playerId) {
        return curses.get(playerId);
    }

    public void remove(UUID playerId) {
        curses.remove(playerId);
        markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound list = new NbtCompound();

        curses.forEach((uuid, curse) -> {
            NbtCompound tag = new NbtCompound();
            tag.putUuid("uuid", uuid);
            tag.putString("curseId", curse.curseId());
            tag.putLong("time", curse.activateTime());
            list.put(uuid.toString(), tag);
        });

        nbt.put("curses", list);
        return nbt;
    }

    public static CurseSaveState fromNbt(NbtCompound nbt) {
        CurseSaveState state = new CurseSaveState();
        NbtCompound list = nbt.getCompound("curses");

        for (String key : list.getKeys()) {
            NbtCompound tag = list.getCompound(key);
            UUID uuid = tag.getUuid("uuid");
            state.curses.put(uuid,
                    new CurseData(
                            tag.getString("curseId"),
                            tag.getLong("time")
                    ));
        }
        return state;
    }
}
