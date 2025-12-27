package tontufosmp2.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorItem;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tontufosmp2.Tontufosmp2;
import tontufosmp2.registry.ModArmorMaterials;
import net.minecraft.item.ToolMaterials;



public class ModItems {
    //Item Garra Dimencional
    public static final Item GARRADIMENCIONAL = registrerItem("garradimencional", new Item(new Item.Settings()));
    // Nuevo ítem fantasma para usar como icono del ItemGroup
    public static final Item ICONO_MAGIA = registrerItem("icono_magia", new Item(new Item.Settings()));
    // Item Collardelojoinverso Inverso
    public static final Item COJOINVERSO = registrerItem("cojoinverso", new Item(new Item.Settings()));
    // Item Cristal del Recuerdo
    public static final Item CRISTALDELRECUERDO = Registry.register(Registries.ITEM, new Identifier("tontufosmp2", "cristaldelrecuerdo"), new CristalDeRecuerdo(new Item.Settings().maxCount(16)));
    // Item Huevo Purificador
    public static final Item HUEVOPURIFICADOR = Registry.register(Registries.ITEM, new Identifier("tontufosmp2", "huevopurificador"), new HuevoPurificadorItem(new Item.Settings().maxCount(16)));
    // Item Máscara del Sepulcral
    public static final Item MASCARADELSILENCIOSEPULCRAL = registrerItem("mascaradelsileciosepulcral", new SilencioSepulcralItem(ModArmorMaterials.SILENCIO_SEPULCRAL, ArmorItem.Type.HELMET, new Item.Settings()));
    // Item Mazo Jgornet (
    public static final Item MAZJGORNET = registrerItem("mazojgornet", new MazojgornetItem(new Item.Settings().maxDamage(750)));
    // Item Orbe Potenciador
    public static final Item ORBEPOTENCIADOR = registrerItem("orbepotenciador", new Item(new Item.Settings()));
    // Item Pico Interdimencional
    public static final Item PICOINTERDIMENCIONAL = Registry.register(Registries.ITEM, new Identifier("tontufosmp2", "picointerdimencional"), new DimensionalPickaxe(ToolMaterials.DIAMOND, 2, -2.8f, new FabricItemSettings().maxDamage(1561)));
    // Item Poción de Luz
    public static final Item POCIONLUZ = registrerItem("pocionluz", new PocionLuzItem(new Item.Settings()));
    // Item Polvo de Confucio
    public static final Item POLVODECONFUCION = registrerItem("polvodeconfucion", new PolvoDeConfusionItem(new Item.Settings()));
    // Item Sándwich de la Muerte
    public static final Item SANDIWCHDELAMUERTE = registrerItem("sandwichdelamuerte", new SandwichDeLaMuerteItem(new Item.Settings()));
    // Item Taladro Excavador
    public static final Item TALADROEXCAVADOR = registrerItem("taladroexcavador", new Item(new Item.Settings()));
    // Item Talismán Protector
    public static final Item TALISMANPROTECTOR = registrerItem("talismanprotector", new Item(new Item.Settings()));
    // Item Tiempo Comprimido
    public static final Item TIEMPOCOMPRIMIDO = Registry.register(Registries.ITEM, new Identifier("tontufosmp2", "tiempocomprimido"), new TiempoComprimidoItem(new Item.Settings().maxDamage(64)));

    //Aqui comienzan las maldiciones
    //Icono Maldiciones
    public static final Item ICONO_MALDICIONES = registrerItem("icono_maldiciones", new Item(new Item.Settings()));
    //Maldicion Agonia del Veterano
    public static final Item AGONIAVETERANO = registrerItem("agoniaveterano", new Item(new Item.Settings()));
    //Maldicion Cerebro Fragmentado
    public static final Item CEREBROFRAGMENTO = registrerItem("cerebrofragmento", new Item(new Item.Settings()));
    //Maldicion Corazon Fragmentado
    public static final Item CORAZONFRAGMENTADO = registrerItem("corazonfragmentado", new Item(new Item.Settings()));
    //Maldicion Decadencia del ciclo
    public static final Item DECADENCIADELCICLO = registrerItem("decadenciadelciclo", new Item(new Item.Settings()));
    //Maldicion Eco del dolor
    public static final Item ECODELDOLOR = registrerItem("ecodeldolor", new Item(new Item.Settings()));
    //Maldicion Eco del pasado
    public static final Item ECODELPASADO = registrerItem("ecodelpasado", new Item(new Item.Settings()));
    //Maldicion Fatiga Ancestral
    public static final Item FATIGAANCESTRAL = registrerItem("fatigaancestral", new Item(new Item.Settings()));
    //Maldicion Hambre del Antiguo
    public static final Item HAMBREDELANTIGUO = registrerItem("hambredelantiguo", new Item(new Item.Settings()));
    //Maldicion Hambre de la Sombra
    public static final Item HAMBRESOMBRA = registrerItem("hambresombra", new Item(new Item.Settings()));
    //Maldicion Lenguaje del plomo
    public static final Item LENGUAPLOMO = registrerItem("lenguaplomo", new Item(new Item.Settings()));
    //Maldicion Ligamento del alma
    public static final Item LIGAMENTODELALMA = registrerItem("ligamentodelalma", new Item(new Item.Settings()));
    //Maldicion Manto del Olvido
    public static final Item MANTODELOLVIDO = registrerItem("mantodelolvido", new Item(new Item.Settings()));
    //Maldiciones Ojo maldito
    public static final Item OJOMALDITO = registrerItem("ojomaldito", new Item(new Item.Settings()));
    //Malciciones Pies Pesado
    public static final Item PIEPESADO = registrerItem("piepesado", new Item(new Item.Settings()));
    //Maldiciones Sed Eterna
    public static final Item SEDETERNA = registrerItem("sedeterna", new Item(new Item.Settings()));
    //Maldiciones Tormento Nocturno
    public static final Item TORMENTONOCTURNO = registrerItem("tormentonocturno", new Item(new Item.Settings()));

     // Encantamientos
     public static final Item ICONO_ENCANTAMIENTO = registrerItem("icono_encantamiento", new Item(new Item.Settings()));
     public static final Item ALIENTODELALBA = registrerItem("alientodelalba", new Item(new Item.Settings()));
    public static final Item ALIENTODEVIDA = registrerItem("alientodevida", new Item(new Item.Settings()));
    public static final Item GRANFORJA = registrerItem("granforja", new Item(new Item.Settings()));
    public static final Item MEDIANOCHE = registrerItem("medianoche", new Item(new Item.Settings()));
    public static final Item OJODECAZADOR = registrerItem("ojodecazador", new Item(new Item.Settings()));
    public static final Item PULSOVENENOSO = registrerItem("pulsovenenoso", new Item(new Item.Settings()));
    public static final Item REFUGIOSOLAR = registrerItem("refugiosolar", new Item(new Item.Settings()));
    public static final Item SALTOVITAL = registrerItem("saltovital", new Item(new Item.Settings()));
    public static final Item PULSOVITAL = registrerItem("pulsovital", new Item(new Item.Settings()));



    private static Item registrerItem (String itemId, Item item){
       return Registry.register(Registries.ITEM, new Identifier(Tontufosmp2.MOD_ID, itemId), item);
    }
    public static void registerItems(){
    Tontufosmp2.LOGGER.info("Registrando items...");
    }
}
