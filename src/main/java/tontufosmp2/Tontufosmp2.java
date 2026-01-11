package tontufosmp2;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tontufosmp2.Posion.ModEffects;
import tontufosmp2.blocks.ModBlocks;
import tontufosmp2.effects.SilencioSepulcralEffects;
import tontufosmp2.enchantment.ModEnchantments;
import tontufosmp2.entities.ModEntities;
import tontufosmp2.event.ModEnchantmentsTickHandler;
import tontufosmp2.items.ModItemGroups;
import tontufosmp2.items.ModItems;
import tontufosmp2.net.ModMessages;
import tontufosmp2.tiempo.ComandosTiempo;
import tontufosmp2.tiempo.ControladorTiempoJugador;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.HashMap;
import java.util.Map;
import tontufosmp2.curse.CurseData;
import tontufosmp2.curse.CurseEffects;
import tontufosmp2.curse.CurseManager;
import tontufosmp2.curse.CurseMessages;
import  tontufosmp2.curse.CurseWarningMessages;

public class Tontufosmp2 implements ModInitializer {
	public static final String MOD_ID = "tontufosmp2";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final int WARNING_TICKS = 1200; // 2 minutos
	private static final Map<MinecraftServer, ControladorTiempoJugador> serverTimeControllers = new HashMap<>();

	@Override
	public void onInitialize() {
        ModEnchantments.register();
        ModEnchantmentsTickHandler.register();
        ModBlocks.registerModBlocks();
		SilencioSepulcralEffects.register();ModItems.registerItems();
		ModEntities.registerModEntities();
		ModItemGroups.registerItemGroups();
		ModEffects.registerEffects();
		ComandosTiempo.registrarComandos();
		ModMessages.registrarPaquetesC2S(); // Actualizado a español

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			LOGGER.info("Inicializando ControladorTiempoJugador para el servidor: {}", server.getName());
			ControladorTiempoJugador controller = new ControladorTiempoJugador(server);
			serverTimeControllers.put(server, controller);
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			LOGGER.info("Deteniendo ControladorTiempoJugador para el servidor: {}", server.getName());
			ControladorTiempoJugador controller = serverTimeControllers.remove(server);
			if (controller != null) {
				controller.getPersistencia().guardarDatos();
			}
		});

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

                CurseData curse = CurseManager.get(player);

                if (curse == null) continue;

                long time = player.getWorld().getTime();

                // ⚠️ AVISO PREVIO
                if (!curse.wasWarned() && time >= curse.activateTime() - WARNING_TICKS) {
                    CurseWarningMessages.send(player);
                    curse.markWarned();
                }

                // 💀 ACTIVACIÓN FINAL
                if (time >= curse.activateTime()) {
                    CurseEffects.applyCurse(player, curse.curseId());
                    CurseMessages.send(player, curse.curseId());
                    CurseManager.remove(player);
                }
            }
        });


        LOGGER.info("¡Tontufosmp2 inicializado correctamente con sistema de control de tiempo!");
	}

	public static ControladorTiempoJugador getServerTimeController(MinecraftServer server) {
		return serverTimeControllers.get(server);
	}

}

