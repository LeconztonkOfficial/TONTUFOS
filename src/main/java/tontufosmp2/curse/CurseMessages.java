package tontufosmp2.curse;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class CurseMessages {

    private static final Map<String, Text> CURSE_MESSAGES = new HashMap<>();

    static {
        CURSE_MESSAGES.put("agoniaveterano",
                Text.literal("Un dolor que no es tuyo se despierta en tus huesos.")
                        .formatted(Formatting.DARK_RED, Formatting.ITALIC));

        CURSE_MESSAGES.put("cerebrofragmento",
                Text.literal("Tus pensamientos se fragmentan lentamente.")
                        .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));

        CURSE_MESSAGES.put("corazonfragmentado",
                Text.literal("Sientes tu corazón latir con dificultad.")
                        .formatted(Formatting.DARK_RED, Formatting.ITALIC));

        CURSE_MESSAGES.put("decadenciadelciclo",
                Text.literal("El ciclo natural de tu cuerpo comienza a romperse.")
                        .formatted(Formatting.DARK_GRAY, Formatting.ITALIC));

        CURSE_MESSAGES.put("ecodeldolor",
                Text.literal("El dolor regresa una y otra vez, como un eco.")
                        .formatted(Formatting.RED, Formatting.ITALIC));

        CURSE_MESSAGES.put("ecodelpasado",
                Text.literal("Recuerdos que no viviste susurran en tu mente.")
                        .formatted(Formatting.GRAY, Formatting.ITALIC));

        CURSE_MESSAGES.put("fatigaancestral",
                Text.literal("Un cansancio antiguo se posa sobre ti.")
                        .formatted(Formatting.DARK_GRAY, Formatting.ITALIC));

        CURSE_MESSAGES.put("hambredelantiguo",
                Text.literal("Tu hambre no parece tener fin.")
                        .formatted(Formatting.DARK_GREEN, Formatting.ITALIC));

        CURSE_MESSAGES.put("hambresombra",
                Text.literal("La sombra devora tu saciedad.")
                        .formatted(Formatting.DARK_GRAY, Formatting.ITALIC));

        CURSE_MESSAGES.put("lenguaplomo",
                Text.literal("Tu lengua pesa como metal.")
                        .formatted(Formatting.GRAY, Formatting.ITALIC));

        CURSE_MESSAGES.put("ligamentodelalma",
                Text.literal("Algo tira de tu alma hacia dentro.")
                        .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));

        CURSE_MESSAGES.put("mantodelolvido",
                Text.literal("Tus recuerdos comienzan a desvanecerse.")
                        .formatted(Formatting.DARK_GRAY, Formatting.ITALIC));

        CURSE_MESSAGES.put("ojomaldito",
                Text.literal("Sientes que algo te observa desde dentro.")
                        .formatted(Formatting.DARK_RED, Formatting.ITALIC));

        CURSE_MESSAGES.put("piepesado",
                Text.literal("Cada paso pesa más que el anterior.")
                        .formatted(Formatting.GRAY, Formatting.ITALIC));

        CURSE_MESSAGES.put("sedeterna",
                Text.literal("La sed jamás se apaga.")
                        .formatted(Formatting.BLUE, Formatting.ITALIC));

        CURSE_MESSAGES.put("tormentonocturno",
                Text.literal("La noche ya no te deja descansar.")
                        .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));
    }

    public static void send(ServerPlayerEntity player, String curseId) {
        Text message = CURSE_MESSAGES.getOrDefault(
                curseId,
                Text.literal("Una presencia oscura se aferra a ti.")
                        .formatted(Formatting.GRAY, Formatting.ITALIC)
        );

        player.sendMessage(message, true);
    }
}

