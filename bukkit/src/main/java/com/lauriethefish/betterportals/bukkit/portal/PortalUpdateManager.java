package com.lauriethefish.betterportals.bukkit.portal;

import com.lauriethefish.betterportals.bukkit.BetterPortals;
import com.lauriethefish.betterportals.bukkit.network.BlockDataArrayRequest;

public class PortalUpdateManager {
    private BetterPortals pl;
    private Portal portal;

    private int ticksSinceActivated = -1;

    public PortalUpdateManager(BetterPortals pl, Portal portal) {
        this.pl = pl;
        this.portal = portal;
    }

    public void playerUpdate() {
        // Update the entity list if need need to - for cross-server portals this will only update the origin entities
        if (ticksSinceActivated % pl.getLoadedConfig().getEntityCheckInterval() == 0) {
            portal.updateNearbyEntities();
        }

        // Entities aren't processed for cross-server portals
        if(!portal.isCrossServer()) {
            portal.checkEntityTeleportation(); // Teleport entities through the portal if they have crossed in the last tick
        }

        // Update blocks if we need to
        if(ticksSinceActivated % pl.getLoadedConfig().getRendering().getBlockUpdateInterval() == 0)   {
            pl.logDebug("Updating current blocks");
            portal.updateCurrentBlocks();
        }

        ticksSinceActivated++;
    }

    public void onActivate() {
        pl.logDebug("Portal activated by player");
        ticksSinceActivated = 0;
        // Destination forceloading is not performed for cross-server portals.
        if(!portal.isCrossServer()) {
            portal.forceloadDestinationChunks();
        }
    }

    public void onDeactivate() {
        pl.logDebug("Portal no longer activated by player");
        ticksSinceActivated = -1;

        // Destination forceloading is not performed for cross-server portals.
        if(!portal.isCrossServer()) {
            portal.unforceloadDestinationChunks();
        }

        // Clear the cached array when the player no longer activates the portal to avoid leaking memory
        pl.getBlockArrayProcessor().clearCachedArray(portal.createBlockDataRequest(BlockDataArrayRequest.Mode.CLEAR), false);
    }

    public boolean isActivatedByPlayer() {
        return ticksSinceActivated != -1;
    }
}
