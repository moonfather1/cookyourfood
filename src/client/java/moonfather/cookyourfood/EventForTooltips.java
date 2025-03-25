package moonfather.cookyourfood;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipePropertySet;

import java.util.List;

public class EventForTooltips
{
    private static final Component messageSevere = Component.translatable("message.shared").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xc84030)));
    private static final Component messageNormal = Component.translatable("message.shared").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xf5a91b)));
    private static final Component messageLight  = Component.translatable("message.shared").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xdde80c)));

    public static void onTooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> components)
    {
        if (itemStack.isEmpty() || itemStack.get(DataComponents.FOOD) == null)
        {
            return;
        }
        //todo disabled FoodResolver.RawFoodRank rank = FoodResolver.Resolve(itemStack, Minecraft.getInstance().level);
        //  resolver should work for clients too
        FoodResolver.RawFoodRank rank = FoodResolver.Resolve(itemStack, Minecraft.getInstance().level);
        if (rank.equals(FoodResolver.RawFoodRank.NotACookableFood)) //?!! temporary
        {
            if (Minecraft.getInstance().level.recipeAccess().propertySet(RecipePropertySet.CAMPFIRE_INPUT).test(itemStack))
            {
                rank = FoodResolver.RawFoodRank.Normal; // on client we won't check if cooking result is edible.
            }
        }

        if (rank.equals(FoodResolver.RawFoodRank.Severe) || itemStack.getItem().equals(Items.PUFFERFISH))
        {
            components.add(messageSevere);
        }
        else if (rank.equals(FoodResolver.RawFoodRank.Light))
        {
            components.add(messageLight);
        }
        else if (rank.equals(FoodResolver.RawFoodRank.Normal))
        {
            components.add(messageNormal);
        }
    }
}
