package de.rafael.plugins.creeper.recover.classes.data;

//------------------------------
//
// This class was developed by Rafael K.
// On 07/12/2022 at 9:43 PM
// In the project CreeperRecover
//
//------------------------------

import org.bukkit.block.Block;

public interface IBlockData {

    void apply(Block block, RecoverPhase phase);

    enum RecoverPhase {
        PRE_STATE_UPDATE,
        POST_STATE_UPDATE
    }

}
