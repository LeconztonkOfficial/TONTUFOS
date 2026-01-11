package tontufosmp2.curse;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CurseWarningMessages {

    public static void send(ServerPlayerEntity player) {
        player.sendMessage(
                Text.literal("Sientes una presencia observándote...")
                        .formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
                true
        );
    }
}
