package moonfather.cookyourfood;

import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class Constants
{ 
    public static final String MODID = "cookyourfood";

    public static class Tags
    {
        public static final TagKey<Item> RAW_FOOD_LIGHT = TagKey.of(RegistryKeys.ITEM, new Identifier(Constants.MODID, "raw_food_light"));
        public static final TagKey<Item> RAW_FOOD_NORMAL = TagKey.of(RegistryKeys.ITEM, new Identifier(Constants.MODID, "raw_food_normal"));
        public static final TagKey<Item> RAW_FOOD_SEVERE = TagKey.of(RegistryKeys.ITEM, new Identifier(Constants.MODID, "raw_food_severe"));
        public static final TagKey<Item> OK_TO_EAT_RAW = TagKey.of(RegistryKeys.ITEM, new Identifier(Constants.MODID, "ok_to_eat_raw"));
    }
} 
