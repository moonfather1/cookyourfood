package moonfather.cookyourfood.client;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.tooltip.api.client.ItemTooltipCallback;

public class ClientInitializer implements ClientModInitializer
{
    @Override
    public void onInitializeClient(ModContainer mod)
    {
        if (ClientConfig.create().ShowWarningTooltipsOnItems)
        {
            ItemTooltipCallback.EVENT.register(EventForTooltips::onTooltip);
        }
    }
}
