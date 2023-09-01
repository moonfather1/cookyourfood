package moonfather.cookyourfood;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.quiltmc.loader.api.QuiltLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CommonConfig //extends ReflectiveConfig
{
    public double EasyDifDurationMultiplier;
    public double NormalDifDurationMultiplier;
    public double HardDifDurationMultiplier;



    // i am supposed to use import org.quiltmc.config.api.ReflectiveConfig;
    // ... but it needs quilt loader 0.20 and this template came with 0.17 yesterday.
    // ... and raising version isn't trivial, so i'll use effect config as my own config system.

    public static CommonConfig create()
    {
        Path configPath = QuiltLoader.getConfigDir().resolve("cookyourfood-common.json");
        ConfigInner loaded = null;
        if (configPath.toFile().exists())
        {
            try
            {
                Gson gson = new Gson();
                loaded = gson.fromJson(Files.readString(configPath), ConfigInner.class);
            }
            catch (IOException ignored)
            {
            }
        }
        if (loaded == null)
        {
            loaded = new ConfigInner();
        }
        CommonConfig result = new CommonConfig();
        result.EasyDifDurationMultiplier = loaded.easy_dif_duration_multiplier;
        result.NormalDifDurationMultiplier = loaded.normal_dif_duration_multiplier;
        result.HardDifDurationMultiplier = loaded.hard_dif_duration_multiplier;
        if (! configPath.toFile().exists())
        {
            try
            {
                Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                String text = gson.toJson(loaded, ConfigInner.class);
                Files.writeString(configPath, text, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            }
            catch (IOException ignored)
            {
            }
        }
        return result;
    }

    private CommonConfig() { }

    ////////////////////////////////////////////////////////////////

    private static class ConfigInner
    {
        public String comment = "This is the multiplier which can shorten or lengthen potion effect durations, when you earn them by eating raw food. Default value of 1.0 means author-designed durations.";
        public double hard_dif_duration_multiplier = 0.8;
        public double normal_dif_duration_multiplier = 1.0;
        public double easy_dif_duration_multiplier = 1.3;
    }
}
