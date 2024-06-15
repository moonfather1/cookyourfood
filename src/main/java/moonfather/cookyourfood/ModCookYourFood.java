package moonfather.cookyourfood;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Constants.MODID)
public class ModCookYourFood
{
    public ModCookYourFood(IEventBus modBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.COMMON, OptionsHolder.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, OptionsHolder.CLIENT_SPEC);
    }
}
