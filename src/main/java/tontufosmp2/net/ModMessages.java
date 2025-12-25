package tontufosmp2.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import tontufosmp2.Tontufosmp2;
import tontufosmp2.tiempo.ConfiguracionTiempo;

import java.lang.reflect.Field;
import java.util.List;

/**
 * ACA SE MANEJAN LOS MENSAJES AL SERVER.
 * Definimos los paquetes para que el Cliente y el Servidor se entiendan.
 * S2C = Server to Client (Servidor -> Cliente)
 * C2S = Client to Server (Cliente -> Servidor)
 */

/**
 * CREAR DOCUMENTACION PARA PODER CREAR MÁS MENSAJES A FUTURO
 */
public class ModMessages {

    // --- Paquetes para la GUI ADMIN ---
    // El server le manda al cliente para abrir el gui
    public static final Identifier ABRIR_GUI_ADMIN_S2C = new Identifier(Tontufosmp2.MOD_ID, "open_time_admin_gui_s2c");
    // El cliente le manda al server a ejecutar un comandop
    public static final Identifier EJECUTAR_COMANDO_C2S = new Identifier(Tontufosmp2.MOD_ID, "execute_time_command_c2s");

    // --- Paquetes para tocar la Configuración ---
    // Cliente: le pide la info al server
    public static final Identifier PEDIR_CONFIG_C2S = new Identifier(Tontufosmp2.MOD_ID, "request_config_file_c2s");
    // Server: le envia la info en json
    public static final Identifier ENVIAR_CONFIG_S2C = new Identifier(Tontufosmp2.MOD_ID, "send_config_file_s2c");
    // Cliente: pide actualizar el config en el server
    public static final Identifier ACTUALIZAR_CONFIG_C2S = new Identifier(Tontufosmp2.MOD_ID, "update_config_c2s");


    // aca registramos lo que llega al SERVIDOR (C2S)
    public static void registrarPaquetesC2S() {
        
        // *1
        // ejecutar comandos desde los botones de la GUI
        ServerPlayNetworking.registerGlobalReceiver(EJECUTAR_COMANDO_C2S, (server, player, handler, buf, responseSender) -> {
            String comando = buf.readString();
            server.execute(() -> {
                ServerCommandSource source = player.getCommandSource();
                //  solo OPs nivel 2 o más
                if (source.hasPermissionLevel(2)) {
                    // Ejecutamos el comando y luego actualizamos la GUI para que se vean los cambios
                    server.getCommandManager().executeWithPrefix(source, "tiempo " + comando);
                    server.getCommandManager().executeWithPrefix(source, "tiempo test gui");
                } else {
                    player.sendMessage(Text.literal("¡Quieto ahí! No tienes permisos para esto :( ."), true);
                }
            });
        });

        // *2
        // El cliente pide ver la configuración
        ServerPlayNetworking.registerGlobalReceiver(PEDIR_CONFIG_C2S, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player.hasPermissionLevel(2)) {
                    try {
                        // Usamos (magia negra de Java) para leer los campos de la clase de config
                        // así no tenemos que actualizar esto cada vez que añadimos una opción nueva.
                        // dudo demasiado que sea la forma correcta de hacerlo pero los otros mods no cambian
                        // mucho la manera que lo hacen y no se si vale la pena agregar una libreria extra solo para
                        // esto
                        ConfiguracionTiempo config = ConfiguracionTiempo.obtenerInstancia();
                        Field[] campos = config.getClass().getFields();
                        
                        PacketByteBuf responseBuf = PacketByteBufs.create();
                        responseBuf.writeInt(campos.length); // Decimos cuántos campos vamos a mandar

                        Gson gson = new GsonBuilder().setPrettyPrinting().create();

                        for (Field campo : campos) {
                            String nombre = campo.getName();
                            Object valor = campo.get(config);
                            String valorStr = (valor instanceof List) ? gson.toJson(valor) : String.valueOf(valor);
                            
                            responseBuf.writeString(nombre);
                            responseBuf.writeString(valorStr);
                        }
                        // Se lo mandamos de vuelta al cliente
                        ServerPlayNetworking.send(player, ENVIAR_CONFIG_S2C, responseBuf);
                    } catch (Exception e) {
                        Tontufosmp2.LOGGER.error("*****Error al leer la config para enviarla: {}", e.getMessage());
                    }
                }
            });
        });

        // *3
        // Cliente manda cambios para guardar en la config
        ServerPlayNetworking.registerGlobalReceiver(ACTUALIZAR_CONFIG_C2S, (server, player, handler, buf, responseSender) -> {
            int cantidadCampos = buf.readInt();
            // Copiamos el buffer porque lo vamos a usar dentro del hilo del server
            final PacketByteBuf datosRecibidos = PacketByteBufs.copy(buf);

            server.execute(() -> {
                if (player.hasPermissionLevel(2)) {
                    try {
                        ConfiguracionTiempo config = ConfiguracionTiempo.obtenerInstancia();
                        for (int i = 0; i < cantidadCampos; i++) {
                            String nombreCampo = datosRecibidos.readString();
                            String valorStr = datosRecibidos.readString();
                            
                            // buscamos el campo por nombre y le metemos el valor nuevo
                            Field campo = config.getClass().getField(nombreCampo);
                            Object valor = null;
                            
                            // convertimos el String al tipo que toque
                            if (campo.getType() == int.class) valor = Integer.parseInt(valorStr);
                            else if (campo.getType() == boolean.class) valor = Boolean.parseBoolean(valorStr);
                            else if (campo.getType() == String.class) valor = valorStr;
                            
                            // si pudimos convertirlo, lo guardamos en el objeto
                            if (valor != null) campo.set(config, valor);
                        }
                        
                        // guardamos a disco
                        config.guardarConfiguracion();
                        player.sendMessage(Text.literal("Configuración guardada y lista.").formatted(Formatting.GREEN), false);

                        // --- AVISO A LOS OPS ---
                        // Avisamos a todos los admins conectados de que alguien tocó la config
                        // TODO: CREAR UN LOG DE LOS CAMBIOS HECHOS O DE LAS COSAS O COMANDOS QUE USAN LOS JUGADORES

                        Text mensajeAviso = Text.literal("[ControlTiempo] ")
                                .append(player.getName().copy().formatted(Formatting.YELLOW))
                                .append(Text.literal(" ha modificado la configuración.").formatted(Formatting.GRAY));

                        List<ServerPlayerEntity> jugadores = server.getPlayerManager().getPlayerList();
                        for (ServerPlayerEntity p : jugadores) {
                            if (p.hasPermissionLevel(2)) { // Solo a los OPs
                                p.sendMessage(mensajeAviso, false);
                            }
                        }

                    } catch (Exception e) {
                        Tontufosmp2.LOGGER.error("Error guardando la config desde el cliente: {}", e.getMessage(), e);
                        player.sendMessage(Text.literal("Error al guardar: " + e.getMessage()).formatted(Formatting.RED), true);
                    }
                }
            });
        });
    }

    public static void registrarPaquetesS2C() {
        // Esto se registra en el cliente (Tontufosmp2Client)
    }
}
