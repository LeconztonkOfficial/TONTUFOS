package tontufosmp2.tiempo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import tontufosmp2.Tontufosmp2;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracionTiempo {
    private static ConfiguracionTiempo instancia;
    
    public static final Path ARCHIVO_CONFIG = FabricLoader.getInstance().getConfigDir()
            .resolve("tontufosmp2").resolve("control_tiempo.json");
    
    public int minutosPermitidos = 480;
    public int umbralAdvertenciaSegundos = 300;
    public String mensajeExpulsion = "Has alcanzado tu límite de tiempo de juego diario.";
    public String mensajeAdvertencia = "¡Advertencia! Te quedan %d minutos y %d segundos antes de ser expulsado.";
    public String mensajeTiempoRestante = "Tiempo restante: %d minutos y %d segundos.";
    public int intervaloVerificacionTicks = 20;
    public boolean permitirAcumulacionTiempo = false;
    public int indiceNivelAcumulacionPorDefecto = 0;
    public int horasJugadasParaAumentarLimite = 2;
    public List<NivelAcumulacion> nivelesAcumulacion = new ArrayList<>();

    public static class NivelAcumulacion {
        public int nivel; 
        public int maxHorasAcumuladas; 
        public int diasRequeridosParaSiguienteNivel; 

        public NivelAcumulacion() {}

        public NivelAcumulacion(int nivel, int maxHorasAcumuladas, int diasRequeridosParaSiguienteNivel) {
            this.nivel = nivel;
            this.maxHorasAcumuladas = maxHorasAcumuladas;
            this.diasRequeridosParaSiguienteNivel = diasRequeridosParaSiguienteNivel;
        }

        public long getMaxTicksAcumulados() {
            return (long) maxHorasAcumuladas * 60 * 60 * 20;
        }
    }
    
    private ConfiguracionTiempo() {
        if (nivelesAcumulacion.isEmpty()) {

            //TODO: ACOMODAR BIEN LOS NIVELES DE ACUMULACION

            nivelesAcumulacion.add(new NivelAcumulacion(0, 2, 2));
            nivelesAcumulacion.add(new NivelAcumulacion(1, 4, 2));
            nivelesAcumulacion.add(new NivelAcumulacion(2, 8, 2));
            nivelesAcumulacion.add(new NivelAcumulacion(3, 12, 0));
        }
    }
    
    public static ConfiguracionTiempo obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionTiempo();
            instancia.cargarConfiguracion();
        }
        return instancia;
    }
    
    public void cargarConfiguracion() {
        if (Files.exists(ARCHIVO_CONFIG)) {
            try (FileReader reader = new FileReader(ARCHIVO_CONFIG.toFile())) {
                Gson gson = new Gson();
                ConfiguracionTiempo configCargada = gson.fromJson(reader, ConfiguracionTiempo.class);
                
                this.minutosPermitidos = configCargada.minutosPermitidos;
                this.umbralAdvertenciaSegundos = configCargada.umbralAdvertenciaSegundos;
                this.mensajeExpulsion = configCargada.mensajeExpulsion;
                this.mensajeAdvertencia = configCargada.mensajeAdvertencia;
                this.mensajeTiempoRestante = configCargada.mensajeTiempoRestante;
                this.intervaloVerificacionTicks = configCargada.intervaloVerificacionTicks;
                this.permitirAcumulacionTiempo = configCargada.permitirAcumulacionTiempo;
                this.indiceNivelAcumulacionPorDefecto = configCargada.indiceNivelAcumulacionPorDefecto;
                this.horasJugadasParaAumentarLimite = configCargada.horasJugadasParaAumentarLimite;

                if (configCargada.nivelesAcumulacion != null && !configCargada.nivelesAcumulacion.isEmpty()) {
                    this.nivelesAcumulacion = configCargada.nivelesAcumulacion;
                } else {
                    Tontufosmp2.LOGGER.warn("La lista de niveles de acumulación en la configuración está vacía o es nula. Usando valores por defecto.");
                    if (this.nivelesAcumulacion.isEmpty()) {
                        // Niveles de acumulacion POR DEFECTO
                        // TODO: acomodarlo bien
                        nivelesAcumulacion.add(new NivelAcumulacion(0, 2, 2));
                        nivelesAcumulacion.add(new NivelAcumulacion(1, 4, 2));
                        nivelesAcumulacion.add(new NivelAcumulacion(2, 8, 2));
                        nivelesAcumulacion.add(new NivelAcumulacion(3, 12, 0));
                    }
                }
                
                Tontufosmp2.LOGGER.info("Configuración de control de tiempo cargada desde: {}", ARCHIVO_CONFIG);
            } catch (IOException e) {
                Tontufosmp2.LOGGER.error("Error al cargar la configuración de control de tiempo: {}", e.getMessage());
                guardarConfiguracion();
            }
        } else {
            Tontufosmp2.LOGGER.info("Archivo de configuración no encontrado. Creando uno nuevo con valores por defecto.");
            guardarConfiguracion();
        }
    }
    
    public void guardarConfiguracion() {
        try {
            Files.createDirectories(ARCHIVO_CONFIG.getParent());
            try (FileWriter writer = new FileWriter(ARCHIVO_CONFIG.toFile())) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(this, writer);
                Tontufosmp2.LOGGER.info("Configuración de control de tiempo guardada en: {}", ARCHIVO_CONFIG);
            }
        } catch (IOException e) {
            Tontufosmp2.LOGGER.error("Error al guardar la configuración de control de tiempo: {}", e.getMessage());
        }
    }
    
    public void recargar() {
        cargarConfiguracion();
        Tontufosmp2.LOGGER.info("Configuración de control de tiempo recargada.");
    }
    
    public long obtenerTicksPermitidos() {
        return (long) minutosPermitidos * 60 * 20;
    }
    
    public long obtenerTicksUmbralAdvertencia() {
        return (long) umbralAdvertenciaSegundos * 20;
    }

    public long getHorasJugadasParaSubirNivelTicks() {
        return (long) horasJugadasParaAumentarLimite * 60 * 60 * 20;
    }

    public NivelAcumulacion getNivelAcumulacion(int index) {
        if (nivelesAcumulacion == null || nivelesAcumulacion.isEmpty()) {
            Tontufosmp2.LOGGER.error("La lista de niveles de acumulación está vacía. Devolviendo un nivel por defecto de 0 horas.");
            return new NivelAcumulacion(0, 0, 0);
        }
        if (index < 0 || index >= nivelesAcumulacion.size()) {
            Tontufosmp2.LOGGER.warn("Índice de nivel de acumulación inválido: {}. Devolviendo el nivel máximo (último de la lista).", index);
            return nivelesAcumulacion.get(nivelesAcumulacion.size() - 1);
        }
        return nivelesAcumulacion.get(index);
    }
}
