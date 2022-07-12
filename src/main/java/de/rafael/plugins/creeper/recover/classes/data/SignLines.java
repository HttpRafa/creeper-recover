package de.rafael.plugins.creeper.recover.classes.data;

//------------------------------
//
// This class was developed by Rafael K.
// On 07/12/2022 at 9:47 PM
// In the project CreeperRecover
//
//------------------------------

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public record SignLines(String[] lines) implements IBlockData {

    @Override
    public void apply(Block block, RecoverPhase phase) {
        if(phase == RecoverPhase.POST_STATE_UPDATE && block.getState() instanceof Sign sign) {
            for (int i = 0; i < lines.length; i++) {
                sign.setLine(i, lines[i]);
            }
            sign.update(true, false);
        }
    }

}
