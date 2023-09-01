package moonfather.cookyourfood.client;

import moonfather.cookyourfood.FoodResolver;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EventForTooltips
{
    private static final Text messageSevere = Text.translatable("message.shared").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xc84030)));
    private static final Text messageNormal = Text.translatable("message.shared").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xf5a91b)));
    private static final Text messageLight  = Text.translatable("message.shared").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xdde80c)));

    public static void onTooltip(ItemStack itemStack, @Nullable PlayerEntity playerEntity, TooltipContext tooltipContext, List<Text> texts)
    {
        if (itemStack.isEmpty() || itemStack.getItem().getFoodComponent() == null || playerEntity == null)
        {
            // one of the later two above is null once during startup but during gameplay we have everything
            return;
        }
        FoodResolver.RawFoodRank rank = FoodResolver.Resolve(itemStack, playerEntity.world);
        if (rank.equals(FoodResolver.RawFoodRank.Severe) || itemStack.getItem().equals(Items.PUFFERFISH))
        {
            texts.add(messageSevere);
        }
        else if (rank.equals(FoodResolver.RawFoodRank.Light))
        {
            texts.add(messageLight);
        }
        else if (rank.equals(FoodResolver.RawFoodRank.Normal))
        {
            texts.add(messageNormal);
        }
    }
}
