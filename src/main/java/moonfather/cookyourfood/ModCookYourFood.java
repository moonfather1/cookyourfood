package moonfather.cookyourfood;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModCookYourFood implements ModInitializer
{
	public static final Logger LOGGER = LoggerFactory.getLogger("CYF");
	public static CommonConfig CONFIG;

	@Override
	public void onInitialize(ModContainer mod)
	{
		// UseItemCallback.EVENT doesn't work for me - it's usage start, not finish
		ServerLifecycleEvents.READY.register(EventHandlers::onServerStarted);
		CONFIG = CommonConfig.create();
	}
}
