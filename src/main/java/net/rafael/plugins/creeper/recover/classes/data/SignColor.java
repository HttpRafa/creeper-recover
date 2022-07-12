package net.rafael.plugins.creeper.recover.classes.data;

//------------------------------
//
// This class was developed by Rafael K.
// On 07/12/2022 at 10:23 PM
// In the project CreeperRecover
//
//------------------------------

import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public record SignColor(DyeColor dyeColor) implements IBlockData {

    @Override
    public void apply(Block block, RecoverPhase phase) {
        if(phase == RecoverPhase.POST_STATE_UPDATE && block.getState() instanceof Sign sign) {
            sign.setColor(dyeColor);
            sign.update(true, false);
        }
    }

}
