package com.example.rpgmod.item;

import com.example.rpgmod.RpgMod;
import com.example.rpgmod.entity.RpgEntities;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

/** Todas las armas y armaduras del mod. */
public final class RpgItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, RpgMod.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RpgMod.MOD_ID);

    /** Todo lo que se muestra en la pestaña creativa, en orden. */
    private static final List<RegistryObject<Item>> ALL = new ArrayList<>();

    // ---------- Armas ----------
    public static final RegistryObject<Item> ESPADA_GUERRERO = reg("espada_guerrero",
        () -> new SwordItem(Tiers.DIAMOND, new Item.Properties()
            .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 4, -2.4f))));

    public static final RegistryObject<Item> BASTON_MAGO = reg("baston_mago",
        () -> new StaffItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ARCO_ARQUERO = reg("arco_arquero",
        () -> new BowItem(new Item.Properties().durability(600)));

    public static final RegistryObject<Item> DAGA_PICARO = reg("daga_picaro",
        () -> new SwordItem(Tiers.IRON, new Item.Properties()
            .attributes(SwordItem.createAttributes(Tiers.IRON, 1, -1.2f))));

    public static final RegistryObject<Item> MAZA_CLERIGO = reg("maza_clerigo",
        () -> new SwordItem(Tiers.IRON, new Item.Properties()
            .attributes(SwordItem.createAttributes(Tiers.IRON, 4, -3.0f))));

    // ---------- Materiales y huevos de enemigos ----------
    public static final RegistryObject<Item> FRAGMENTO_RUNICO = reg("fragmento_runico",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> HUEVO_ESQUELETO_RUNICO = reg("esqueleto_runico_spawn_egg",
        () -> new ForgeSpawnEggItem(RpgEntities.RUNE_SKELETON, 0xD2CDBE, 0x5AD7FF, new Item.Properties()));

    // ---------- Armaduras (un conjunto por clase) ----------
    // defensa: botas, pantalones, peto, casco
    public static final Holder<ArmorMaterial> GUERRERO_MAT =
        material("guerrero", 3, 6, 8, 3, 1.0f, () -> Ingredient.of(Items.IRON_INGOT));
    public static final Holder<ArmorMaterial> MAGO_MAT =
        material("mago", 1, 3, 4, 1, 0.0f, () -> Ingredient.of(Items.AMETHYST_SHARD));
    public static final Holder<ArmorMaterial> ARQUERO_MAT =
        material("arquero", 2, 4, 5, 2, 0.0f, () -> Ingredient.of(Items.LEATHER));
    public static final Holder<ArmorMaterial> PICARO_MAT =
        material("picaro", 2, 4, 5, 2, 0.0f, () -> Ingredient.of(Items.BLACK_WOOL));
    public static final Holder<ArmorMaterial> CLERIGO_MAT =
        material("clerigo", 2, 5, 6, 2, 0.5f, () -> Ingredient.of(Items.GOLD_INGOT));

    static {
        armorSet("guerrero", GUERRERO_MAT, 28);
        armorSet("mago", MAGO_MAT, 20);
        armorSet("arquero", ARQUERO_MAT, 24);
        armorSet("picaro", PICARO_MAT, 24);
        armorSet("clerigo", CLERIGO_MAT, 24);
    }

    // ---------- Pestaña del modo creativo ----------
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("rpgmod",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.rpgmod"))
            .icon(() -> new ItemStack(BASTON_MAGO.get()))
            .displayItems((params, output) -> ALL.forEach(i -> output.accept(i.get())))
            .build());

    // ---------- Ayudas ----------
    private static RegistryObject<Item> reg(String name, Supplier<Item> factory) {
        RegistryObject<Item> obj = ITEMS.register(name, factory);
        ALL.add(obj);
        return obj;
    }

    private static Holder<ArmorMaterial> material(String name, int boots, int legs, int chest,
                                                  int helmet, float toughness,
                                                  Supplier<Ingredient> repair) {
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BOOTS, boots);
        defense.put(ArmorItem.Type.LEGGINGS, legs);
        defense.put(ArmorItem.Type.CHESTPLATE, chest);
        defense.put(ArmorItem.Type.HELMET, helmet);
        defense.put(ArmorItem.Type.BODY, chest);
        return Holder.direct(new ArmorMaterial(defense, 12, SoundEvents.ARMOR_EQUIP_IRON, repair,
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(RpgMod.MOD_ID, name))),
            toughness, 0.0f));
    }

    private static void armorSet(String cls, Holder<ArmorMaterial> mat, int durability) {
        piece(cls + "_casco", mat, ArmorItem.Type.HELMET, durability);
        piece(cls + "_peto", mat, ArmorItem.Type.CHESTPLATE, durability);
        piece(cls + "_pantalones", mat, ArmorItem.Type.LEGGINGS, durability);
        piece(cls + "_botas", mat, ArmorItem.Type.BOOTS, durability);
    }

    private static void piece(String name, Holder<ArmorMaterial> mat, ArmorItem.Type type, int durability) {
        reg(name, () -> new ArmorItem(mat, type, new Item.Properties().durability(type.getDurability(durability))));
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }

    private RpgItems() {}
}
