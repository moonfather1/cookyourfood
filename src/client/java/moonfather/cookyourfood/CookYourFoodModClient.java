package moonfather.cookyourfood;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class CookYourFoodModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (ClientConfig.create().ShowWarningTooltipsOnItems)
		{
			ItemTooltipCallback.EVENT.register(EventForTooltips::onTooltip);
		}
	}
}