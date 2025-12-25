package tontufosmp2.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TiempoAdminScreen extends Screen {

    private final List<String> playerData;

    public TiempoAdminScreen(List<String> playerData) {
        super(Text.literal("Administración de Tiempo").formatted(Formatting.BOLD));
        this.playerData = playerData;
    }

    @Override
    protected void init() {
        super.init();
    }

    //DrawContext
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //llama al renderBackground por defecto
        this.renderBackground(context);
        
        //titulo
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        
        // información de los jugadores
        int yPos = 40;
        for (String line : playerData) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(line), this.width / 2, yPos, 0xFFFFFF);
            yPos += 12;
        }

        //render de la clase padre
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
