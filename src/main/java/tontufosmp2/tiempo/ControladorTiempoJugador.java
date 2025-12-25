package tontufosmp2.tiempo;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import tontufosmp2.Tontufosmp2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ControladorTiempoJugador {
    
    private final ConfiguracionTiempo config;
    private final PersistenciaTiempo persistencia;
    private final MinecraftServer server;
    
    private int contadorTicks = 0;
    private final Map<UUID, Long> ultimaAdvertenciaEnviada = new HashMap<>();
    private static final int INTERVALO_MINIMO_ADVERTENCIAS = 200;
    
    public ControladorTiempoJugador(MinecraftServer server) {
        this.server = server;
        this.config = ConfiguracionTiempo.obtenerInstancia();
        this.persistencia = new PersistenciaTiempo(server.getSavePath(WorldSavePath.ROOT));
        
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        
        ServerTickEvents.END_SERVER_TICK.register(s -> {
            if (s == this.server && s.getTicks() % 6000 == 0) {
                persistencia.guardarDatos();
            }
        });
        
        Tontufosmp2.LOGGER.info("ControladorTiempoJugador inicializado para el servidor: {}", server.getName());
    }
    
    private void onServerTick(MinecraftServer server) {
        if (server != this.server) {
            return;
        }

        contadorTicks++;
        
        if (contadorTicks >= config.intervaloVerificacionTicks) {
            contadorTicks = 0;
            procesarTicks();
        }
    }

    private void procesarTicks() {
        if (persistencia.necesitaResetDiario()) {
            persistencia.resetearTiempoDiario(config);
        }
        
        for (ServerPlayerEntity jugador : server.getPlayerManager().getPlayerList()) {
            procesarJugador(jugador);
        }
    }
    
    private void procesarJugador(ServerPlayerEntity jugador) {
        UUID uuid = jugador.getUuid();
        String playerName = jugador.getName().getString();

        persistencia.actualizarNombreJugador(uuid, playerName);
        persistencia.incrementarTiempoJugadoHoy(uuid, config.intervaloVerificacionTicks);
        
        long ticksJugadosHoy = persistencia.obtenerTiempoJugadoHoy(uuid);
        long ticksPermitidos = config.obtenerTicksPermitidos();
        
        long ticksDisponibles = ticksPermitidos;
        if (config.permitirAcumulacionTiempo) {
            long ticksAcumulados = persistencia.obtenerTiempoAcumulado(uuid);
            
            int playerLevelIndex = persistencia.obtenerNivelAcumulacionJugador(uuid, config);
            ConfiguracionTiempo.NivelAcumulacion currentTier = config.getNivelAcumulacion(playerLevelIndex);
            long limiteAcumuladoDelJugador = currentTier.getMaxTicksAcumulados();
            
            if (ticksAcumulados > limiteAcumuladoDelJugador) {
                persistencia.establecerTiempoAcumulado(uuid, limiteAcumuladoDelJugador);
                ticksAcumulados = limiteAcumuladoDelJugador;
            }

            ticksDisponibles += ticksAcumulados;
            
            if (ticksJugadosHoy > ticksPermitidos) {
                long tiempoExtraUsado = ticksJugadosHoy - ticksPermitidos;
                long nuevoTiempoAcumulado = Math.max(0, ticksAcumulados - tiempoExtraUsado);
                persistencia.establecerTiempoAcumulado(uuid, nuevoTiempoAcumulado);
                ticksDisponibles = ticksPermitidos + nuevoTiempoAcumulado;
            }
        }
        
        long ticksRestantes = ticksDisponibles - ticksJugadosHoy;
        
        if (ticksJugadosHoy >= ticksDisponibles) {
            expulsarJugador(jugador);
            return;
        }
        
        long ticksUmbral = config.obtenerTicksUmbralAdvertencia();
        if (ticksRestantes <= ticksUmbral && ticksRestantes > 0) {
            enviarAdvertencia(jugador, ticksRestantes);
        }
    }
    
    private void expulsarJugador(ServerPlayerEntity jugador) {
        String mensaje = config.mensajeExpulsion;
        jugador.networkHandler.disconnect(Text.literal(mensaje).formatted(Formatting.RED));
        Tontufosmp2.LOGGER.info("Jugador {} expulsado por exceder el tiempo de juego permitido", jugador.getName().getString());
    }
    
    private void enviarAdvertencia(ServerPlayerEntity jugador, long ticksRestantes) {
        UUID uuid = jugador.getUuid();
        long tiempoActual = System.currentTimeMillis();
        
        Long ultimaAdvertencia = ultimaAdvertenciaEnviada.get(uuid);
        if (ultimaAdvertencia != null && (tiempoActual - ultimaAdvertencia) < (INTERVALO_MINIMO_ADVERTENCIAS * 50)) {
            return;
        }
        
        long segundosRestantes = ticksRestantes / 20;
        long minutosRestantes = segundosRestantes / 60;
        long segundos = segundosRestantes % 60;
        
        String mensaje = String.format(config.mensajeAdvertencia, minutosRestantes, segundos);
        jugador.sendMessage(Text.literal(mensaje).formatted(Formatting.YELLOW), false);
        
        ultimaAdvertenciaEnviada.put(uuid, tiempoActual);
        Tontufosmp2.LOGGER.debug("Advertencia enviada a {}: {} minutos y {} segundos restantes", jugador.getName().getString(), minutosRestantes, segundos);
    }
    
    public long obtenerTiempoRestanteTicks(ServerPlayerEntity jugador) {
        UUID uuid = jugador.getUuid();
        long ticksJugadosHoy = persistencia.obtenerTiempoJugadoHoy(uuid);
        long ticksPermitidos = config.obtenerTicksPermitidos();
        
        long ticksDisponibles = ticksPermitidos;
        if (config.permitirAcumulacionTiempo) {
            long ticksAcumulados = persistencia.obtenerTiempoAcumulado(uuid);
            
            int playerLevelIndex = persistencia.obtenerNivelAcumulacionJugador(uuid, config);
            ConfiguracionTiempo.NivelAcumulacion currentTier = config.getNivelAcumulacion(playerLevelIndex);
            long limiteAcumuladoDelJugador = currentTier.getMaxTicksAcumulados();

            if (ticksAcumulados > limiteAcumuladoDelJugador) {
                ticksAcumulados = limiteAcumuladoDelJugador;
            }
            ticksDisponibles += ticksAcumulados;
        }
        
        return Math.max(0, ticksDisponibles - ticksJugadosHoy);
    }
    
    public long obtenerTiempoAcumuladoTicks(ServerPlayerEntity jugador) {
        if (!config.permitirAcumulacionTiempo) {
            return 0;
        }
        return persistencia.obtenerTiempoAcumulado(jugador.getUuid());
    }
    
    public void recargarConfiguracion() {
        config.recargar();
        Tontufosmp2.LOGGER.info("Configuración recargada por ControladorTiempoJugador");
    }

    public PersistenciaTiempo getPersistencia() {
        return persistencia;
    }
}
