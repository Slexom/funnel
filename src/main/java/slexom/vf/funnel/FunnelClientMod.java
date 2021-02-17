package slexom.vf.funnel;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import slexom.vf.funnel.screen.FunnelScreen;

@Environment(EnvType.CLIENT)
public class FunnelClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(FunnelMod.FUNNEL_SCREEN_HANDLER, FunnelScreen::new);
    }

}
