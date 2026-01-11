package tontufosmp2.curse;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class CurseEffects {

    // Definimos cada maldición con sus efectos por fase
    private static final Map<String, StatusEffect[][]> CURSES = new HashMap<>();

    static {
        // Cada fila es una fase: { efecto1, efecto2, ... }
        CURSES.put("agoniaveterano", new StatusEffect[][]{
                {StatusEffects.WEAKNESS},
                {StatusEffects.WEAKNESS, StatusEffects.SLOWNESS},
                {StatusEffects.WEAKNESS, StatusEffects.SLOWNESS}
        });

        CURSES.put("cerebrofragmento", new StatusEffect[][]{
                {StatusEffects.NAUSEA},
                {StatusEffects.NAUSEA, StatusEffects.MINING_FATIGUE},
                {StatusEffects.NAUSEA, StatusEffects.MINING_FATIGUE}
        });

        CURSES.put("corazonfragmentado", new StatusEffect[][]{
                {StatusEffects.WITHER},
                {StatusEffects.WITHER},
                {StatusEffects.WITHER}
        });

        CURSES.put("decadenciadelciclo", new StatusEffect[][]{
                {StatusEffects.HUNGER},
                {StatusEffects.HUNGER, StatusEffects.WEAKNESS},
                {StatusEffects.HUNGER, StatusEffects.WEAKNESS}
        });

        CURSES.put("ecodeldolor", new StatusEffect[][]{
                {StatusEffects.WITHER},
                {StatusEffects.WITHER},
                {StatusEffects.WITHER}
        });

        CURSES.put("ecodelpasado", new StatusEffect[][]{
                {StatusEffects.NAUSEA},
                {StatusEffects.NAUSEA, StatusEffects.SLOWNESS},
                {StatusEffects.NAUSEA, StatusEffects.SLOWNESS}
        });

        CURSES.put("fatigaancestral", new StatusEffect[][]{
                {StatusEffects.MINING_FATIGUE},
                {StatusEffects.MINING_FATIGUE},
                {StatusEffects.MINING_FATIGUE}
        });

        CURSES.put("hambredelantiguo", new StatusEffect[][]{
                {StatusEffects.HUNGER},
                {StatusEffects.HUNGER},
                {StatusEffects.HUNGER}
        });

        CURSES.put("hambresombra", new StatusEffect[][]{
                {StatusEffects.HUNGER},
                {StatusEffects.HUNGER, StatusEffects.BLINDNESS},
                {StatusEffects.HUNGER, StatusEffects.BLINDNESS}
        });

        CURSES.put("lenguaplomo", new StatusEffect[][]{
                {StatusEffects.WEAKNESS},
                {StatusEffects.WEAKNESS},
                {StatusEffects.WEAKNESS}
        });

        CURSES.put("ligamentodelalma", new StatusEffect[][]{
                {StatusEffects.SLOWNESS},
                {StatusEffects.SLOWNESS, StatusEffects.WEAKNESS},
                {StatusEffects.SLOWNESS, StatusEffects.WEAKNESS}
        });

        CURSES.put("mantodelolvido", new StatusEffect[][]{
                {StatusEffects.BLINDNESS},
                {StatusEffects.BLINDNESS, StatusEffects.NAUSEA},
                {StatusEffects.BLINDNESS, StatusEffects.NAUSEA}
        });

        CURSES.put("ojomaldito", new StatusEffect[][]{
                {StatusEffects.BLINDNESS},
                {StatusEffects.BLINDNESS},
                {StatusEffects.BLINDNESS}
        });

        CURSES.put("piepesado", new StatusEffect[][]{
                {StatusEffects.SLOWNESS},
                {StatusEffects.SLOWNESS},
                {StatusEffects.SLOWNESS}
        });

        CURSES.put("sedeterna", new StatusEffect[][]{
                {StatusEffects.HUNGER},
                {StatusEffects.HUNGER, StatusEffects.WEAKNESS},
                {StatusEffects.HUNGER, StatusEffects.WEAKNESS}
        });

        CURSES.put("tormentonocturno", new StatusEffect[][]{
                {StatusEffects.BLINDNESS},
                {StatusEffects.BLINDNESS, StatusEffects.WEAKNESS},
                {StatusEffects.BLINDNESS, StatusEffects.WEAKNESS}
        });
    }

    public static void applyCurse(ServerPlayerEntity player, String curseId) {
        CurseData data = CurseManager.get(player);
        if (data == null) return;

        long elapsed = player.getWorld().getTime() - data.activateTime();
        int fase;

        if (elapsed < 5 * 60 * 20) fase = 0;        // fase 1
        else if (elapsed < 15 * 60 * 20) fase = 1;  // fase 2
        else fase = 2;                              // fase 3 (máxima)

        StatusEffect[][] cursePhases = CURSES.get(curseId);
        if (cursePhases == null) return;

        // Para tormento nocturno, aplicar solo si es de noche
        boolean nightOnly = curseId.equals("tormentonocturno");
        if (nightOnly && player.getWorld().isDay()) return;

        // Aplicar todos los efectos de la fase correspondiente con duración infinita
        for (StatusEffect effect : cursePhases[fase]) {
            player.addStatusEffect(new StatusEffectInstance(effect, Integer.MAX_VALUE, fase, false, false));
        }
    }
}


