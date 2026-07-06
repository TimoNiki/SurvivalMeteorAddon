package my.meteor.addon;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import my.meteor.addon.modules.ForceCreative;
import my.meteor.addon.modules.LavaGrief;
import my.meteor.addon.modules.TntGrief;
import org.slf4j.Logger;

public class Addon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    
  
    public static final Category CATEGORY = new Category("Local World");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Local World Utils Addon for 1.21.11!");

        Modules.get().add(new ForceCreative());
        Modules.get().add(new TntGrief());
        Modules.get().add(new LavaGrief());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        
        return "my.meteor.addon";
    }
}
