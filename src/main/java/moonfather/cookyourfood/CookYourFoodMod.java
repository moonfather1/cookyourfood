package moonfather.cookyourfood;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookYourFoodMod implements ModInitializer
{
    public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MODID);
	public static CommonConfig CONFIG;

	@Override
	public void onInitialize()
	{
		// UseItemCallback.EVENT doesn't work for me - it's usage start, not finish
		ServerLifecycleEvents.SERVER_STARTED.register(EventHandlers::onServerStarted);
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(EventHandlers::onDataPacksReloaded);
		CONFIG = CommonConfig.create();
	}
}