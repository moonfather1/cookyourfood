package moonfather.cookyourfood;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Constants
{ 
    public static final String MODID = "cookyourfood";

    public static class Tags
    {
        public static final TagKey<Item> RAW_FOOD_LIGHT = TagKey.of(Registry.ITEM_KEY, new Identifier(Constants.MODID, "raw_food_light"));
        public static final TagKey<Item> RAW_FOOD_NORMAL = TagKey.of(Registry.ITEM_KEY, new Identifier(Constants.MODID, "raw_food_normal"));
        public static final TagKey<Item> RAW_FOOD_SEVERE = TagKey.of(Registry.ITEM_KEY, new Identifier(Constants.MODID, "raw_food_severe"));
        public static final TagKey<Item> OK_TO_EAT_RAW = TagKey.of(Registry.ITEM_KEY, new Identifier(Constants.MODID, "ok_to_eat_raw"));
    }
} 
