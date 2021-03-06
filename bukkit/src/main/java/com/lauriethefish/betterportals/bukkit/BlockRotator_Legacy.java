package com.lauriethefish.betterportals.bukkit;

import com.lauriethefish.betterportals.bukkit.math.MathUtils;
import com.lauriethefish.betterportals.bukkit.network.BlockDataArrayRequest;

import org.bukkit.block.BlockState;
import org.bukkit.material.Directional;
import org.bukkit.util.Vector;

// Alternative implementation that uses MaterialData, since BlockData doesn't exist in 1.12 and below
public class BlockRotator_Legacy implements BlockRotator {
    private BlockDataArrayRequest request;
    public BlockRotator_Legacy(BlockDataArrayRequest request)  {
        this.request = request;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void rotateToOrigin(BlockState state)    {
        // Rotating blocks is not necessary if the two portals face in the same direction
        if(request.getOriginPos().getDirection() == request.getDestPos().getDirection())    {return;}
        org.bukkit.material.MaterialData data = state.getData();

        if(data instanceof Directional) {
            Directional rotatable = (Directional) data;

            Vector currentDir = ReflectUtils.getDirection(rotatable.getFacing());
            Vector finalDir = MathUtils.round(request.getTransformations().rotateToOrigin(currentDir));

            // Get the face as a vector, and rotate it with the portals matrix, then set the direction to the new one
            rotatable.setFacingDirection(ReflectUtils.getBlockFace(finalDir));
            state.setData(data);
        }
    }
}
