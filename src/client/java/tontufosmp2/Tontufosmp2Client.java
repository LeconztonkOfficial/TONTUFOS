package tontufosmp2;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import tontufosmp2.client.gui.ConfigDisplayDialog;
import tontufosmp2.client.gui.TiempoAdminWindow;
import tontufosmp2.entities.ModEntities;
import tontufosmp2.client.renderer.InvisibleEntityRenderer;
import tontufosmp2.net.ModMessages;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tontufosmp2Client implements ClientModInitializer {

    public record PlayerData(String name, String timePlayed, String timeAccumulated) {}

    private static TiempoAdminWindow activeAdminWindow = null;

    @Override
    public void onInitializeClient() {
        try {
            System.setProperty("java.awt.headless", "false");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Tontufosmp2.LOGGER.error("No se pudo establecer el Look and Feel del sistema para la GUI de Swing.", e);
        }

        EntityRendererRegistry.register(ModEntities.CONFUSION_PROJECTILE, InvisibleEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWABLE_LIGHT, FlyingItemEntityRenderer::new);
        registrarPaquetesS2C();
    }

    private void registrarPaquetesS2C() {
        // El server nos manda abrir la ventana de admin
        ClientPlayNetworking.registerGlobalReceiver(ModMessages.ABRIR_GUI_ADMIN_S2C, (client, handler, buf, responseSender) -> {
            int playerCount = buf.readInt();
            final List<PlayerData> playerDataList = new ArrayList<>(playerCount);
            for (int i = 0; i < playerCount; i++) {
                playerDataList.add(new PlayerData(buf.readString(), buf.readString(), buf.readString()));
            }

            SwingUtilities.invokeLater(() -> {
                if (activeAdminWindow == null) {
                    activeAdminWindow = new TiempoAdminWindow(playerDataList);
                    activeAdminWindow.setVisible(true);
                } else {
                    activeAdminWindow.updateData(playerDataList);
                    activeAdminWindow.toFront();
                }
            });
        });

        // El server nos manda los datos de la config para mostrarlos
        ClientPlayNetworking.registerGlobalReceiver(ModMessages.ENVIAR_CONFIG_S2C, (client, handler, buf, responseSender) -> {
            int fieldCount = buf.readInt();
            final Map<String, String> configData = new LinkedHashMap<>();
            for (int i = 0; i < fieldCount; i++) {
                configData.put(buf.readString(), buf.readString());
            }
            
            SwingUtilities.invokeLater(() -> {
                ConfigDisplayDialog dialog = new ConfigDisplayDialog(activeAdminWindow, configData);
                dialog.setVisible(true);
            });
        });
    }

    public static void onAdminWindowClosed() {
        activeAdminWindow = null;
    }
}
