/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.moonLandscapeGenerator.generator.Rasterizer;

import org.terasology.math.ChunkMath;
import org.terasology.math.Region3i;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.moonLandscapeGenerator.generator.data.MineShaft;
import org.terasology.moonLandscapeGenerator.generator.facet.MineShaftFacet;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

import java.util.Map.Entry;

public class MineShaftRasterizer implements WorldRasterizer {
    private Block brick;
    private Block glass;

    @Override
    public void initialize() {
        brick = CoreRegistry.get(BlockManager.class).getBlock("Core:Brick");
        glass = CoreRegistry.get(BlockManager.class).getBlock("Core:Glass");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        MineShaftFacet shaftFacet = chunkRegion.getFacet(MineShaftFacet.class);

        for (Entry<BaseVector3i, MineShaft> entry : shaftFacet.getWorldEntries().entrySet()) {
            // there should be a mine shaft here
            // create a couple 3d regions to help iterate through the cube shape, inside and out
            Vector3i centerShaftPosition = new Vector3i(entry.getKey());
            int extent = entry.getValue().getExtent();
            centerShaftPosition.add(0, extent, 0);
            Region3i walls = Region3i.createFromCenterExtents(centerShaftPosition, extent);
            Region3i inside = Region3i.createFromCenterExtents(centerShaftPosition, extent - 1);

            // loop through each of the positions in the cube, ignoring the is
            for (Vector3i newBlockPosition : walls) {
                if (chunkRegion.getRegion().encompasses(newBlockPosition) && inside.encompasses(newBlockPosition) && newBlockPosition.x == centerShaftPosition.x && newBlockPosition.z == centerShaftPosition.z) {
                    chunk.setBlock(ChunkMath.calcBlockPos(newBlockPosition), glass);
                    continue;
                }

                if (chunkRegion.getRegion().encompasses(newBlockPosition)
                        && !inside.encompasses(newBlockPosition)) {
                    chunk.setBlock(ChunkMath.calcBlockPos(newBlockPosition), brick);
                }
            }
        }
    }
}
