package tontufosmp2.tiempo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tontufosmp2.Tontufosmp2;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PersistenciaTiempo {
    
    private final Path archivoDatos;
    private final Map<UUID, Long> tiempoJugadoHoy = new HashMap<>();
    private final Map<UUID, Long> tiempoAcumulado = new HashMap<>();
    private final Map<UUID, String> nombresJugadores = new HashMap<>();
    private final Map<UUID, Integer> nivelAcumulacionJugador = new HashMap<>();
    private final Map<UUID, Integer> diasProgresoSubirNivel = new HashMap<>();
    
    private int ultimoDiaReset = -1;
    private static final long LIMITE_ACUMULACION_DIARIA_TICKS = 2L * 60 * 60 * 20;
    
    public PersistenciaTiempo(Path worldSavePath) {
        this.archivoDatos = worldSavePath.resolve("tontufosmp2").resolve("tiempo_jugadores.json");
        cargarDatos();
    }
    
    public long obtenerTiempoJugadoHoy(UUID uuid) {
        return tiempoJugadoHoy.getOrDefault(uuid, 0L);
    }
    
    public void establecerTiempoJugadoHoy(UUID uuid, long ticks) {
        tiempoJugadoHoy.put(uuid, ticks);
    }
    
    public void incrementarTiempoJugadoHoy(UUID uuid, long ticksIncremento) {
        long tiempoActual = obtenerTiempoJugadoHoy(uuid);
        establecerTiempoJugadoHoy(uuid, tiempoActual + ticksIncremento);
    }
    
    public long obtenerTiempoAcumulado(UUID uuid) {
        return tiempoAcumulado.getOrDefault(uuid, 0L);
    }
    
    public void establecerTiempoAcumulado(UUID uuid, long ticks) {
        tiempoAcumulado.put(uuid, ticks);
    }

    public void actualizarNombreJugador(UUID uuid, String name) {
        nombresJugadores.put(uuid, name);
    }

    public String obtenerNombreJugador(UUID uuid) {
        return nombresJugadores.getOrDefault(uuid, uuid.toString());
    }

    public int obtenerNivelAcumulacionJugador(UUID uuid, ConfiguracionTiempo config) {
        return nivelAcumulacionJugador.computeIfAbsent(uuid, k -> config.indiceNivelAcumulacionPorDefecto);
    }

    public void establecerNivelAcumulacionJugador(UUID uuid, int levelIndex) {
        nivelAcumulacionJugador.put(uuid, levelIndex);
    }

    public int obtenerDiasProgresoSubirNivel(UUID uuid) {
        return diasProgresoSubirNivel.getOrDefault(uuid, 0);
    }

    public void establecerDiasProgresoSubirNivel(UUID uuid, int days) {
        diasProgresoSubirNivel.put(uuid, days);
    }
    
    public void resetearTiempoDiario(ConfiguracionTiempo config) {
        long ticksPermitidos = config.obtenerTicksPermitidos();
        
        for (Map.Entry<UUID, Long> entrada : tiempoJugadoHoy.entrySet()) {
            UUID uuid = entrada.getKey();
            long tiempoUsado = entrada.getValue();
            
            int currentLevelIndex = obtenerNivelAcumulacionJugador(uuid, config);
            ConfiguracionTiempo.NivelAcumulacion currentTier = config.getNivelAcumulacion(currentLevelIndex);

            if (tiempoUsado >= config.getHorasJugadasParaSubirNivelTicks()) {
                int currentProgressDays = obtenerDiasProgresoSubirNivel(uuid);
                establecerDiasProgresoSubirNivel(uuid, currentProgressDays + 1);

                if (currentTier.diasRequeridosParaSiguienteNivel > 0 && (currentProgressDays + 1) >= currentTier.diasRequeridosParaSiguienteNivel) {
                    if (currentLevelIndex + 1 < config.nivelesAcumulacion.size()) {
                        establecerNivelAcumulacionJugador(uuid, currentLevelIndex + 1);
                        establecerDiasProgresoSubirNivel(uuid, 0);
                        Tontufosmp2.LOGGER.info("¡El jugador {} ha subido al Nivel de Acumulación {}!", obtenerNombreJugador(uuid), currentLevelIndex + 2);
                    } else {
                        establecerDiasProgresoSubirNivel(uuid, 0);
                    }
                }
            } else {
                establecerDiasProgresoSubirNivel(uuid, 0); 
            }

            if (config.permitirAcumulacionTiempo && tiempoUsado < ticksPermitidos) { 
                long tiempoNoUsado = ticksPermitidos - tiempoUsado;
                long tiempoAAcumular = Math.min(tiempoNoUsado, LIMITE_ACUMULACION_DIARIA_TICKS);
                long tiempoAcumuladoActual = obtenerTiempoAcumulado(uuid);
                long limiteAcumuladoDelJugador = currentTier.getMaxTicksAcumulados(); 
                long nuevoTiempoAcumulado = Math.min(tiempoAcumuladoActual + tiempoAAcumular, limiteAcumuladoDelJugador);
                establecerTiempoAcumulado(uuid, nuevoTiempoAcumulado);
                
                Tontufosmp2.LOGGER.info("Tiempo acumulado para {}: {} ticks ({} minutos). Se añadió {} minutos este día.", 
                    obtenerNombreJugador(uuid), nuevoTiempoAcumulado, nuevoTiempoAcumulado / 1200, tiempoAAcumular / 1200);
            }
            
            establecerTiempoJugadoHoy(uuid, 0L);
        }
        
        guardarDatos();
        Tontufosmp2.LOGGER.info("Tiempo diario reseteado para todos los jugadores.");
    }
    
    public boolean necesitaResetDiario() {
        int diaActual = java.time.LocalDate.now().getDayOfYear();
        if (ultimoDiaReset == -1) {
            ultimoDiaReset = diaActual;
            return false;
        }
        if (diaActual != ultimoDiaReset) {
            ultimoDiaReset = diaActual;
            return true;
        }
        return false;
    }
    
    private void cargarDatos() {
        if (!Files.exists(archivoDatos)) {
            Tontufosmp2.LOGGER.info("Archivo de datos de tiempo no encontrado en {}. Iniciando con datos vacíos.", archivoDatos);
            return;
        }
        
        try (FileReader reader = new FileReader(archivoDatos.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            
            if (json.has("ultimo_dia_reset")) ultimoDiaReset = json.get("ultimo_dia_reset").getAsInt();
            
            if (json.has("tiempo_jugado_hoy")) {
                json.getAsJsonObject("tiempo_jugado_hoy").entrySet().forEach(e -> tiempoJugadoHoy.put(UUID.fromString(e.getKey()), e.getValue().getAsLong()));
            }
            if (json.has("tiempo_acumulado")) {
                json.getAsJsonObject("tiempo_acumulado").entrySet().forEach(e -> tiempoAcumulado.put(UUID.fromString(e.getKey()), e.getValue().getAsLong()));
            }
            if (json.has("nombres_jugadores")) {
                json.getAsJsonObject("nombres_jugadores").entrySet().forEach(e -> nombresJugadores.put(UUID.fromString(e.getKey()), e.getValue().getAsString()));
            }
            if (json.has("nivel_acumulacion_jugador")) {
                json.getAsJsonObject("nivel_acumulacion_jugador").entrySet().forEach(e -> nivelAcumulacionJugador.put(UUID.fromString(e.getKey()), e.getValue().getAsInt()));
            }
            if (json.has("dias_progreso_subir_nivel")) {
                json.getAsJsonObject("dias_progreso_subir_nivel").entrySet().forEach(e -> diasProgresoSubirNivel.put(UUID.fromString(e.getKey()), e.getValue().getAsInt()));
            }
            
            Tontufosmp2.LOGGER.info("Datos de tiempo cargados desde {}: {} jugadores", archivoDatos, tiempoJugadoHoy.size());
        } catch (IOException e) {
            Tontufosmp2.LOGGER.error("Error al cargar datos de tiempo desde {}: {}", archivoDatos, e.getMessage());
        }
    }
    
    public void guardarDatos() {
        try {
            Files.createDirectories(archivoDatos.getParent());
            
            JsonObject json = new JsonObject();
            json.addProperty("ultimo_dia_reset", ultimoDiaReset);
            
            JsonObject tiempoHoyJson = new JsonObject();
            tiempoJugadoHoy.forEach((uuid, ticks) -> tiempoHoyJson.addProperty(uuid.toString(), ticks));
            json.add("tiempo_jugado_hoy", tiempoHoyJson);
            
            JsonObject tiempoAcumJson = new JsonObject();
            tiempoAcumulado.forEach((uuid, ticks) -> tiempoAcumJson.addProperty(uuid.toString(), ticks));
            json.add("tiempo_acumulado", tiempoAcumJson);

            JsonObject namesJson = new JsonObject();
            nombresJugadores.forEach((uuid, name) -> namesJson.addProperty(uuid.toString(), name));
            json.add("nombres_jugadores", namesJson);

            JsonObject levelsJson = new JsonObject();
            nivelAcumulacionJugador.forEach((uuid, level) -> levelsJson.addProperty(uuid.toString(), level));
            json.add("nivel_acumulacion_jugador", levelsJson);

            JsonObject progressJson = new JsonObject();
            diasProgresoSubirNivel.forEach((uuid, days) -> progressJson.addProperty(uuid.toString(), days));
            json.add("dias_progreso_subir_nivel", progressJson);
            
            try (FileWriter writer = new FileWriter(archivoDatos.toFile())) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(json, writer);
            }
            
            Tontufosmp2.LOGGER.debug("Datos de tiempo guardados en {}.", archivoDatos);
        } catch (IOException e) {
            Tontufosmp2.LOGGER.error("Error al guardar datos de tiempo en {}: {}", archivoDatos, e.getMessage());
        }
    }
    
    public Map<UUID, Long> obtenerTodosLosTiempos() {
        return new HashMap<>(tiempoJugadoHoy);
    }
    
    public Map<UUID, Long> obtenerTodosLosTiemposAcumulados() {
        return new HashMap<>(tiempoAcumulado);
    }
}
