package elec332.core.api.dimension.teleporter;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;

import java.util.Random;

/**
 * Created by Elec332 on 23-1-2015.
 */
public class Teleporter extends net.minecraft.world.Teleporter {
    private final WorldServer worldServer;
    private final Random random;

    public Teleporter(WorldServer world, Block Portal, Block frame) {
        super(world);
        this.worldServer = world;
        this.random = new Random(world.getSeed());
        this.frame = frame;
        this.portal = Portal;
    }

    static Block frame;
    static Block portal;

    public void placeInPortal(Entity p_77185_1_, double p_77185_2_, double p_77185_4_, double p_77185_6_, float p_77185_8_) {
        if(!this.placeInExistingPortal(p_77185_1_, p_77185_2_, p_77185_4_, p_77185_6_, p_77185_8_)) {
            this.makePortal(p_77185_1_);
            this.placeInExistingPortal(p_77185_1_, p_77185_2_, p_77185_4_, p_77185_6_, p_77185_8_);
        }
    }

    public boolean placeInExistingPortal(Entity entity, double p_77184_2_, double p_77184_4_, double p_77184_6_, float p_77184_8_) {
        int x = MathHelper.floor_double(entity.posX);
        int z = MathHelper.floor_double(entity.posZ);
        int px = -2147483648;
        int py = 0;
        int pz = 0;

        for(int xD = -20; xD <= 20; ++xD) {
            for(int zD = -20; zD <= 20; ++zD) {
                for(int y = -20; y < this.worldServer.getActualHeight() + 5; ++y) {
                    if(this.worldServer.getBlock(x + xD, y, z + zD) == portal) {
                        px = x + xD;
                        py = y;
                        pz = z + zD;
                    }
                }
            }
        }

        if(px == -2147483648) {
            return false;
        } else {
            while(!this.worldServer.getBlock(px, py - 1, pz).getMaterial().isSolid()) {
                --py;
            }

            entity.posX = (double)((float)px + 0.5F);
            entity.posY = (double)py + 0.1D;
            entity.posZ = (double)((float)pz + 0.5F);
            entity.rotationPitch = 0.0F;
            entity.setSneaking(false);
            if(this.worldServer.getBlock(px + 1, py, pz) != portal && this.worldServer.getBlock(px - 1, py, pz) != portal) {
                if(this.worldServer.getBlock(px, py, pz + 1) != portal && this.worldServer.getBlock(px, py, pz - 1) != portal) {
                    entity.rotationPitch = 90.0F;
                } else {
                    entity.rotationYaw = 90.0F;
                }
            } else {
                entity.rotationYaw = 0.0F;
            }

            return true;
        }
    }


    public boolean makePortal(Entity p_85188_1_) {
        byte b0 = 16;
        double d0 = -1.0D;
        int i = MathHelper.floor_double(p_85188_1_.posX);
        int j = MathHelper.floor_double(p_85188_1_.posY);
        int k = MathHelper.floor_double(p_85188_1_.posZ);
        int l = i;
        int i1 = j;
        int j1 = k;
        int k1 = 0;
        int l1 = this.random.nextInt(4);

        int i2;
        double d1;
        int k2;
        double d2;
        int i3;
        int j3;
        int k3;
        int l3;
        int i4;
        int j4;
        int k4;
        int l4;
        int i5;
        double d3;
        double d4;
        int k5;
        for(i2 = i - b0; i2 <= i + b0; ++i2) {
            d1 = (double)i2 + 0.5D - p_85188_1_.posX;

            for(k2 = k - b0; k2 <= k + b0; ++k2) {
                d2 = (double)k2 + 0.5D - p_85188_1_.posZ;

                label272:
                for(i3 = this.worldServer.getActualHeight() - 1; i3 >= 0; --i3) {
                    if(this.worldServer.isAirBlock(i2, i3, k2)) {
                        while(i3 > 0 && this.worldServer.isAirBlock(i2, i3 - 1, k2)) {
                            --i3;
                        }

                        for(j3 = l1; j3 < l1 + 4; ++j3) {
                            k3 = j3 % 2;
                            l3 = 1 - k3;
                            if(j3 % 4 >= 2) {
                                k3 = -k3;
                                l3 = -l3;
                            }

                            for(i4 = 0; i4 < 3; ++i4) {
                                for(j4 = 0; j4 < 4; ++j4) {
                                    for(k4 = -1; k4 < 4; ++k4) {
                                        l4 = i2 + (j4 - 1) * k3 + i4 * l3;
                                        i5 = i3 + k4;
                                        k5 = k2 + (j4 - 1) * l3 - i4 * k3;
                                        if(k4 < 0 && !this.worldServer.getBlock(l4, i5, k5).getMaterial().isSolid() || k4 >= 0 && !this.worldServer.isAirBlock(l4, i5, k5)) {
                                            continue label272;
                                        }
                                    }
                                }
                            }

                            d3 = (double)i3 + 0.5D - p_85188_1_.posY;
                            d4 = d1 * d1 + d3 * d3 + d2 * d2;
                            if(d0 < 0.0D || d4 < d0) {
                                d0 = d4;
                                l = i2;
                                i1 = i3;
                                j1 = k2;
                                k1 = j3 % 4;
                            }
                        }
                    }
                }
            }
        }

        if(d0 < 0.0D) {
            for(i2 = i - b0; i2 <= i + b0; ++i2) {
                d1 = (double)i2 + 0.5D - p_85188_1_.posX;

                for(k2 = k - b0; k2 <= k + b0; ++k2) {
                    d2 = (double)k2 + 0.5D - p_85188_1_.posZ;

                    label220:
                    for(i3 = this.worldServer.getActualHeight() - 1; i3 >= 0; --i3) {
                        if(this.worldServer.isAirBlock(i2, i3, k2)) {
                            while(i3 > 0 && this.worldServer.isAirBlock(i2, i3 - 1, k2)) {
                                --i3;
                            }

                            for(j3 = l1; j3 < l1 + 2; ++j3) {
                                k3 = j3 % 2;
                                l3 = 1 - k3;

                                for(i4 = 0; i4 < 4; ++i4) {
                                    for(j4 = -1; j4 < 4; ++j4) {
                                        k4 = i2 + (i4 - 1) * k3;
                                        l4 = i3 + j4;
                                        i5 = k2 + (i4 - 1) * l3;
                                        if(j4 < 0 && !this.worldServer.getBlock(k4, l4, i5).getMaterial().isSolid() || j4 >= 0 && !this.worldServer.isAirBlock(k4, l4, i5)) {
                                            continue label220;
                                        }
                                    }
                                }

                                d3 = (double)i3 + 0.5D - p_85188_1_.posY;
                                d4 = d1 * d1 + d3 * d3 + d2 * d2;
                                if(d0 < 0.0D || d4 < d0) {
                                    d0 = d4;
                                    l = i2;
                                    i1 = i3;
                                    j1 = k2;
                                    k1 = j3 % 2;
                                }
                            }
                        }
                    }
                }
            }
        }

        k5 = l;
        int j2 = i1;
        k2 = j1;
        int l5 = k1 % 2;
        int l2 = 1 - l5;
        if(k1 % 4 >= 2) {
            l5 = -l5;
            l2 = -l2;
        }

        boolean flag;
        if(d0 < 0.0D) {
            if(i1 < 70) {
                i1 = 70;
            }

            if(i1 > this.worldServer.getActualHeight() - 10) {
                i1 = this.worldServer.getActualHeight() - 10;
            }

            j2 = i1;

            for(i3 = -1; i3 <= 1; ++i3) {
                for(j3 = 1; j3 < 3; ++j3) {
                    for(k3 = -1; k3 < 3; ++k3) {
                        l3 = k5 + (j3 - 1) * l5 + i3 * l2;
                        i4 = j2 + k3;
                        j4 = k2 + (j3 - 1) * l2 - i3 * l5;
                        flag = k3 < 0;
                        this.worldServer.setBlock(l3, i4, j4, flag ? frame : Blocks.air);
                    }
                }
            }
        }

        for(i3 = 0; i3 < 4; ++i3) {
            for(j3 = 0; j3 < 4; ++j3) {
                for(k3 = -1; k3 < 4; ++k3) {
                    l3 = k5 + (j3 - 1) * l5;
                    i4 = j2 + k3;
                    j4 = k2 + (j3 - 1) * l2;
                    flag = j3 == 0 || j3 == 3 || k3 == -1 || k3 == 3;
                    this.worldServer.setBlock(l3, i4, j4, (Block) (flag ? frame : portal), 0, 2);
                }
            }

            for(j3 = 0; j3 < 4; ++j3) {
                for(k3 = -1; k3 < 4; ++k3) {
                    l3 = k5 + (j3 - 1) * l5;
                    i4 = j2 + k3;
                    j4 = k2 + (j3 - 1) * l2;
                    this.worldServer.notifyBlocksOfNeighborChange(l3, i4, j4, this.worldServer.getBlock(l3, i4, j4));
                }
            }
        }

        return true;
    }

    public void removeStalePortalLocations(long l) {
    }
}
