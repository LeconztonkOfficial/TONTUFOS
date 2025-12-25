package tontufosmp2.tiempo;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import tontufosmp2.Tontufosmp2;
import tontufosmp2.net.ModMessages;

import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ComandosTiempo {

    public static void registrarComandos() {
        CommandRegistrationCallback.EVENT.register(ComandosTiempo::registrar);
        Tontufosmp2.LOGGER.info("Comandos de control de tiempo registrados");
    }

    private static void registrar(CommandDispatcher<ServerCommandSource> dispatcher,
                                  CommandRegistryAccess registryAccess,
                                  CommandManager.RegistrationEnvironment environment) {

        //comandos

        dispatcher.register(literal("tiempo")
            .then(literal("restante")
                .executes(ComandosTiempo::ejecutarTiempoRestante))
            
            .then(literal("recargar")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ComandosTiempo::ejecutarRecargar))
            
            .then(literal("acumulado")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ComandosTiempo::ejecutarMostrarAcumulado))

            .then(literal("acumulacion")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("estado", BoolArgumentType.bool())
                    .executes(ComandosTiempo::ejecutarAcumulacion)))

            .then(literal("test")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("set_jugado")
                    .then(argument("jugador", EntityArgumentType.player())
                        .then(argument("minutos", IntegerArgumentType.integer(0))
                            .executes(ComandosTiempo::ejecutarSetJugado))))
                .then(literal("set_acumulado")
                    .then(argument("jugador", EntityArgumentType.player())
                        .then(argument("minutos", IntegerArgumentType.integer(0))
                            .executes(ComandosTiempo::ejecutarSetAcumulado))))
                .then(literal("reset_day")
                    .executes(ComandosTiempo::ejecutarResetDia))
                .then(literal("gui")
                    .executes(ComandosTiempo::ejecutarGui)))
            
            .then(literal("info_acumulado")
                .executes(ComandosTiempo::ejecutarInfoAcumulado))
        );
    }

    private static int ejecutarGui(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("Este comando solo puede ser ejecutado por un jugador."));
            return 0;
        }

        ControladorTiempoJugador controller = getController(source);
        if (controller == null) return 0;

        PersistenciaTiempo persistencia = controller.getPersistencia();
        Map<UUID, Long> tiemposJugados = persistencia.obtenerTodosLosTiempos();
        Map<UUID, Long> tiemposAcumulados = persistencia.obtenerTodosLosTiemposAcumulados();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(tiemposJugados.size());

        for (Map.Entry<UUID, Long> entry : tiemposJugados.entrySet()) {
            UUID uuid = entry.getKey();
            String nombre = persistencia.obtenerNombreJugador(uuid);
            String tiempoJugado = formatTicksToHoursMinutes(entry.getValue());
            String tiempoAcumulado = formatTicksToHoursMinutes(tiemposAcumulados.getOrDefault(uuid, 0L));

            buf.writeString(nombre);
            buf.writeString(tiempoJugado);
            buf.writeString(tiempoAcumulado);
        }

        ServerPlayNetworking.send(player, ModMessages.ABRIR_GUI_ADMIN_S2C, buf);
        return 1;
    }
    
    private static ControladorTiempoJugador getController(ServerCommandSource source) {
        ControladorTiempoJugador controller = Tontufosmp2.getServerTimeController(source.getServer());
        if (controller == null) {
            source.sendError(Text.literal("El sistema de control de tiempo no está inicializado en el servidor."));
        }
        return controller;
    }

    private static String formatTicksToHoursMinutes(long ticks) {
        if (ticks <= 0) return "0m";
        long totalMinutes = ticks / 1200;
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return (hours > 0) ? String.format("%dh %02dm", hours, minutes) : String.format("%dm", minutes);
    }

    private static int ejecutarTiempoRestante(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity jugador)) {
            source.sendError(Text.literal("Este comando solo puede ser ejecutado por un jugador."));
            return 0;
        }
        
        ControladorTiempoJugador controlador = getController(source);
        if (controlador == null) return 0;

        ConfiguracionTiempo config = ConfiguracionTiempo.obtenerInstancia();
        long ticksRestantes = controlador.obtenerTiempoRestanteTicks(jugador);
        String tiempoRestanteFormateado = formatTicksToHoursMinutes(ticksRestantes);

        MutableText mensajeFinal = Text.literal("Tiempo restante: ").formatted(Formatting.GREEN);
        mensajeFinal.append(Text.literal(tiempoRestanteFormateado).formatted(Formatting.WHITE));

        if (config.permitirAcumulacionTiempo) {
            long ticksAcumulados = controlador.obtenerTiempoAcumuladoTicks(jugador);
            String tiempoAcumuladoFormateado = formatTicksToHoursMinutes(ticksAcumulados);
            
            int playerLevelIndex = controlador.getPersistencia().obtenerNivelAcumulacionJugador(jugador.getUuid(), config);
            ConfiguracionTiempo.NivelAcumulacion currentTier = config.getNivelAcumulacion(playerLevelIndex);
            long limiteAcumuladoJugadorTicks = currentTier.getMaxTicksAcumulados();
            String limiteAcumuladoFormateado = formatTicksToHoursMinutes(limiteAcumuladoJugadorTicks);
            
            MutableText acumuladoYLímite = Text.literal(" (Acumulado: ").formatted(Formatting.WHITE)
                                .append(Text.literal(tiempoAcumuladoFormateado).formatted(Formatting.AQUA))
                                .append(Text.literal(" / Límite: ").formatted(Formatting.WHITE))
                                .append(Text.literal(limiteAcumuladoFormateado).formatted(Formatting.GOLD))
                                .append(Text.literal(")").formatted(Formatting.WHITE));
            mensajeFinal.append(acumuladoYLímite);
        }
        jugador.sendMessage(mensajeFinal, false);
        return 1;
    }
    
    private static int ejecutarRecargar(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ControladorTiempoJugador controlador = getController(source);
        if (controlador == null) return 0;
        controlador.recargarConfiguracion();
        source.sendFeedback(() -> Text.literal("Configuración de control de tiempo recargada exitosamente.").formatted(Formatting.GREEN), true);
        Tontufosmp2.LOGGER.info("Configuración recargada por operador: {}", source.getName());
        return 1;
    }
    
    private static int ejecutarMostrarAcumulado(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ControladorTiempoJugador controlador = getController(source);
        if (controlador == null) return 0;

        ConfiguracionTiempo config = ConfiguracionTiempo.obtenerInstancia();
        PersistenciaTiempo persistencia = controlador.getPersistencia();

        if (!config.permitirAcumulacionTiempo) {
            source.sendFeedback(() -> Text.literal("La acumulación de tiempo está deshabilitada.").formatted(Formatting.YELLOW), false);
            return 1;
        }
        
        Map<UUID, Long> tiemposAcumulados = persistencia.obtenerTodosLosTiemposAcumulados();
        if (tiemposAcumulados.isEmpty()) {
            source.sendFeedback(() -> Text.literal("No hay jugadores con tiempo acumulado.").formatted(Formatting.YELLOW), false);
            return 1;
        }
        
        source.sendFeedback(() -> Text.literal("=== Estado de Acumulación de Tiempo ===").formatted(Formatting.GOLD), false);
        
        for (Map.Entry<UUID, Long> entrada : tiemposAcumulados.entrySet()) {
            UUID uuid = entrada.getKey();
            long ticksAcumulados = entrada.getValue();
            String nombreJugador = persistencia.obtenerNombreJugador(uuid);

            int playerLevelIndex = persistencia.obtenerNivelAcumulacionJugador(uuid, config);
            ConfiguracionTiempo.NivelAcumulacion currentTier = config.getNivelAcumulacion(playerLevelIndex);
            long limiteAcumuladoJugadorTicks = currentTier.getMaxTicksAcumulados();
            
            String tiempoAcumuladoFormateado = formatTicksToHoursMinutes(ticksAcumulados);
            String limiteAcumuladoFormateado = formatTicksToHoursMinutes(limiteAcumuladoJugadorTicks);

            MutableText mensaje = Text.literal(nombreJugador + ": ").formatted(Formatting.WHITE)
                                .append(Text.literal(tiempoAcumuladoFormateado).formatted(Formatting.AQUA))
                                .append(Text.literal(" acumulado (Límite: ").formatted(Formatting.WHITE))
                                .append(Text.literal(limiteAcumuladoFormateado).formatted(Formatting.GOLD))
                                .append(Text.literal(")").formatted(Formatting.WHITE));
            source.sendFeedback(() -> mensaje, false);
        }
        return 1;
    }

    private static int ejecutarAcumulacion(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        boolean estadoAcumulacion = BoolArgumentType.getBool(context, "estado");
        ControladorTiempoJugador controlador = getController(source);
        if (controlador == null) return 0;

        ConfiguracionTiempo config = ConfiguracionTiempo.obtenerInstancia();
        config.permitirAcumulacionTiempo = estadoAcumulacion;
        config.guardarConfiguracion();

        String mensajeFeedback = estadoAcumulacion ? "La acumulación de tiempo ha sido HABILITADA." : "La acumulación de tiempo ha sido DESHABILITADA.";
        source.sendFeedback(() -> Text.literal(mensajeFeedback).formatted(Formatting.GREEN), true);
        Tontufosmp2.LOGGER.info("Acumulación de tiempo {} por operador: {}", estadoAcumulacion ? "habilitada" : "deshabilitada", source.getName());
        return 1;
    }

    private static int ejecutarSetJugado(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "jugador");
        int minutos = IntegerArgumentType.getInteger(context, "minutos");

        ControladorTiempoJugador controlador = getController(source);
        if (controlador == null) return 0;
        PersistenciaTiempo persistencia = controlador.getPersistencia();

        long ticks = (long) minutos * 60 * 20;
        persistencia.establecerTiempoJugadoHoy(targetPlayer.getUuid(), ticks);
        persistencia.actualizarNombreJugador(targetPlayer.getUuid(), targetPlayer.getName().getString());
        persistencia.guardarDatos();

        source.sendFeedback(() -> Text.literal(String.format("Tiempo jugado de %s establecido a %s.", targetPlayer.getName().getString(), formatTicksToHoursMinutes(ticks))).formatted(Formatting.GREEN), true);
        Tontufosmp2.LOGGER.info("Operador {} estableció el tiempo jugado de {} a {} minutos.", source.getName(), targetPlayer.getName().getString(), minutos);
        return 1;
    }

    private static int ejecutarSetAcumulado(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "jugador");
        int minutos = IntegerArgumentType.getInteger(context, "minutos");

        ControladorTiempoJugador controlador = getController(source);
        if (controlador == null) return 0;
        PersistenciaTiempo persistencia = controlador.getPersistencia();

        long ticks = (long) minutos * 60 * 20;
        persistencia.establecerTiempoAcumulado(targetPlayer.getUuid(), ticks);
        persistencia.actualizarNombreJugador(targetPlayer.getUuid(), targetPlayer.getName().getString());
        persistencia.guardarDatos();

        source.sendFeedback(() -> Text.literal(String.format("Tiempo acumulado de %s establecido a %s.", targetPlayer.getName().getString(), formatTicksToHoursMinutes(ticks))).formatted(Formatting.GREEN), true);
        Tontufosmp2.LOGGER.info("Operador {} estableció el tiempo acumulado de {} a {} minutos.", source.getName(), targetPlayer.getName().getString(), minutos);
        return 1;
    }

    private static int ejecutarResetDia(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ControladorTiempoJugador controlador = getController(source);
        if (controlador == null) return 0;

        PersistenciaTiempo persistencia = controlador.getPersistencia();
        ConfiguracionTiempo config = ConfiguracionTiempo.obtenerInstancia();
        persistencia.resetearTiempoDiario(config);
        
        source.sendFeedback(() -> Text.literal("Simulación de reseteo diario completada.").formatted(Formatting.GREEN), true);
        Tontufosmp2.LOGGER.info("Operador {} simuló el reseteo diario del tiempo.", source.getName());
        return 1;
    }

    private static int ejecutarInfoAcumulado(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity jugador)) {
            source.sendError(Text.literal("Este comando solo puede ser ejecutado por un jugador."));
            return 0;
        }

        ControladorTiempoJugador controlador = getController(source);
        if (controlador == null) return 0;

        ConfiguracionTiempo config = ConfiguracionTiempo.obtenerInstancia();
        PersistenciaTiempo persistencia = controlador.getPersistencia();
        UUID uuid = jugador.getUuid();

        if (!config.permitirAcumulacionTiempo) {
            jugador.sendMessage(Text.literal("La acumulación de tiempo está deshabilitada en este servidor.").formatted(Formatting.YELLOW), false);
            return 1;
        }

        long tiempoAcumuladoTicks = persistencia.obtenerTiempoAcumulado(uuid);
        int playerLevelIndex = persistencia.obtenerNivelAcumulacionJugador(uuid, config);
        ConfiguracionTiempo.NivelAcumulacion currentTier = config.getNivelAcumulacion(playerLevelIndex);
        
        String tiempoAcumuladoFormateado = formatTicksToHoursMinutes(tiempoAcumuladoTicks);
        String limiteAcumuladoFormateado = formatTicksToHoursMinutes(currentTier.getMaxTicksAcumulados());
        String horasJugadasParaAumentarLimiteFormateado = formatTicksToHoursMinutes(config.getHorasJugadasParaSubirNivelTicks());

        jugador.sendMessage(Text.literal("=== Información de Acumulación de Tiempo ===").formatted(Formatting.GOLD), false);
        jugador.sendMessage(Text.literal("Nivel de Acumulación: ").formatted(Formatting.AQUA).append(Text.literal(String.valueOf(playerLevelIndex + 1)).formatted(Formatting.WHITE)), false);
        jugador.sendMessage(Text.literal("Tiempo acumulado actual: ").formatted(Formatting.AQUA).append(Text.literal(tiempoAcumuladoFormateado).formatted(Formatting.WHITE)), false);
        jugador.sendMessage(Text.literal("Límite de acumulación personal: ").formatted(Formatting.AQUA).append(Text.literal(limiteAcumuladoFormateado).formatted(Formatting.GOLD)), false);

        if (playerLevelIndex + 1 < config.nivelesAcumulacion.size()) {
            ConfiguracionTiempo.NivelAcumulacion nextTier = config.getNivelAcumulacion(playerLevelIndex + 1);
            String nextLimiteAcumuladoFormateado = formatTicksToHoursMinutes(nextTier.getMaxTicksAcumulados());
            int diasFaltantes = nextTier.diasRequeridosParaSiguienteNivel - persistencia.obtenerDiasProgresoSubirNivel(uuid);

            jugador.sendMessage(Text.literal("Próximo Nivel (").formatted(Formatting.GRAY).append(Text.literal(String.valueOf(playerLevelIndex + 2)).formatted(Formatting.WHITE)).append(Text.literal("): Límite ").formatted(Formatting.GRAY)).append(Text.literal(nextLimiteAcumuladoFormateado).formatted(Formatting.DARK_GREEN)), false);
            jugador.sendMessage(Text.literal(String.format("Juega al menos %s en un día para progresar.", horasJugadasParaAumentarLimiteFormateado)).formatted(Formatting.GRAY), false);
            jugador.sendMessage(Text.literal(String.format("Te falta %d día(s) de juego para alcanzar el Nivel %d.", diasFaltantes, playerLevelIndex + 2)).formatted(Formatting.GRAY), false);
        } else {
            jugador.sendMessage(Text.literal("¡Has alcanzado el nivel máximo de acumulación!").formatted(Formatting.GREEN), false);
        }
        return 1;
    }
}
