package moonfather.cookyourfood.client;

import moonfather.cookyourfood.Constants;
import moonfather.cookyourfood.FoodResolver;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class EventForTooltips
{
	private static final Component messageSevere = Component.translatable("message.shared").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xc84030)));
	private static final Component messageNormal = Component.translatable("message.shared").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xf5a91b)));
	private static final Component messageLight  = Component.translatable("message.shared").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xdde80c)));

	@SubscribeEvent
	public static void OnItemTooltip(ItemTooltipEvent event)
	{
		if (event.getItemStack().isEmpty() || event.getItemStack().getItem().getFoodProperties() == null || event.getEntity() == null)
		{
			// one of the later two above is null once during startup but during gameplay we have everything
			return;
		}
		FoodResolver.RawFoodRank rank = FoodResolver.Resolve(event.getItemStack(), event.getEntity().level());
		if (rank.equals(FoodResolver.RawFoodRank.Severe) || event.getItemStack().getItem().equals(Items.PUFFERFISH))
		{
			event.getToolTip().add(messageSevere);
		}
		else if (rank.equals(FoodResolver.RawFoodRank.Light))
		{
			event.getToolTip().add(messageLight);
		}
		else if (rank.equals(FoodResolver.RawFoodRank.Normal))
		{
			event.getToolTip().add(messageNormal);
		}
	}
}
