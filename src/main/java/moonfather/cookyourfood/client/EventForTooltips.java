package moonfather.cookyourfood.client;

import moonfather.cookyourfood.Constants;
import moonfather.cookyourfood.FoodResolver;
import moonfather.cookyourfood.OptionsHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
public class EventForTooltips
{
	private static final Component messageSevere = Component.translatable("message.shared").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xc84030)));
	private static final Component messageNormal = Component.translatable("message.shared").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xf5a91b)));
	private static final Component messageLight  = Component.translatable("message.shared").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xdde80c)));

	public static void OnItemTooltip(ItemTooltipEvent event)
	{
		if (event.getItemStack().isEmpty() || ! event.getItemStack().has(DataComponents.FOOD) || event.getEntity() == null)
		{
			// last one is null once during startup but during gameplay we have everything
			return;
		}
		FoodResolver.RawFoodRank rank = FoodResolver.Resolve(event.getItemStack(), event.getEntity().level(), event.getEntity());
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



	@SubscribeEvent
	public static void Initialize(FMLClientSetupEvent event)
	{
		if (OptionsHolder.CLIENT.ShowWarningsInTooltip.get())
		{
			NeoForge.EVENT_BUS.addListener(EventForTooltips::OnItemTooltip);
		}
	}
}
