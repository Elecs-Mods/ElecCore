package elec332.core.api.dimension;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.helper.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Elec332 on 23-1-2015.
 */

public class PortalBlock extends BlockPortal{
    public PortalBlock(String name, Block frameBlock, int DimID) {
        this.setBlockName(name);
        //this.setTickRandomly(true);
        this.setBlockUnbreakable();
        this.DimID = DimID;
        this.frame = frameBlock;
        this.portal = this;
        RegisterHelper.registerBlock(this, name);
    }

    int DimID;
    static PortalBlock portal;
    static Block frame;

    boolean vanillaTexture = true;
    static Integer timePortal = 10;
    boolean hasToBeSneaking = false;

    public PortalBlock vanillaTexture(boolean b) {
        this.vanillaTexture = b;
        return this;
    }

    public PortalBlock setTeleportTime(int Time) {
        this.timePortal = Time;
        return this;
    }

    public PortalBlock hasToBeSneaking() {
        //this.timePortal = 0;
        this.hasToBeSneaking = true;
        return this;
    }

    public boolean tryToCreatePortal(World world, int x, int y, int z){
        return func_150000_e(world, x, y, z);
    }

    public boolean func_150000_e(World p_150000_1_, int p_150000_2_, int p_150000_3_, int p_150000_4_) {
        PortalBlock.Size size = new PortalBlock.Size(p_150000_1_, p_150000_2_, p_150000_3_, p_150000_4_, 1);
        PortalBlock.Size size1 = new PortalBlock.Size(p_150000_1_, p_150000_2_, p_150000_3_, p_150000_4_, 2);
        if(size.func_150860_b() && size.field_150864_e == 0) {
            size.func_150859_c();
            return true;
        } else if(size1.func_150860_b() && size1.field_150864_e == 0) {
            size1.func_150859_c();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity par5Entity){
        if(par5Entity.ridingEntity == null && par5Entity.riddenByEntity == null && par5Entity instanceof EntityPlayerMP) {
            EntityPlayerMP thePlayer = (EntityPlayerMP) par5Entity;
            if (hasToBeSneaking){
                if (thePlayer.isSneaking())
                    executeTP(thePlayer);
            }else {
                if (par5Entity.timeUntilPortal > 0) {
                    par5Entity.timeUntilPortal = timePortal;
                } else {
                    executeTP(thePlayer);
                }
            }
        }
    }

    void executeTP(EntityPlayerMP thePlayer){
        if(thePlayer.dimension != DimID) {
            thePlayer.timeUntilPortal = timePortal;
            Util.TPPlayerToDim(thePlayer, frame, portal, DimID);
        } else {
            thePlayer.timeUntilPortal = timePortal;
            Util.TPPlayerToDim(thePlayer, frame, portal, 0);
        }
    }



    public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_) {
        int l = func_149999_b(p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_));
        PortalBlock.Size size = new PortalBlock.Size(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, 1);
        PortalBlock.Size size1 = new PortalBlock.Size(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, 2);
        if(l == 1 && (!size.func_150860_b() || size.field_150864_e < size.field_150868_h * size.field_150862_g)) {
            p_149695_1_.setBlock(p_149695_2_, p_149695_3_, p_149695_4_, Blocks.air);
        } else if(l == 2 && (!size1.func_150860_b() || size1.field_150864_e < size1.field_150868_h * size1.field_150862_g)) {
            p_149695_1_.setBlock(p_149695_2_, p_149695_3_, p_149695_4_, Blocks.air);
        } else if(l == 0 && !size.func_150860_b() && !size1.func_150860_b()) {
            p_149695_1_.setBlock(p_149695_2_, p_149695_3_, p_149695_4_, Blocks.air);
        }

    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
    }

    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
    }

    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(vanillaTexture ? "portal" : this.getTextureName());
    }

    public static class Size {
        private final World world;
        private final int field_150865_b;
        private final int field_150866_c;
        private final int field_150863_d;
        private int field_150864_e = 0;
        private ChunkCoordinates field_150861_f;
        private int field_150862_g;
        private int field_150868_h;

        public Size(World p_world, int p_i45415_2_, int p_i45415_3_, int p_i45415_4_, int p_i45415_5_) {
            this.world = p_world;
            this.field_150865_b = p_i45415_5_;
            this.field_150863_d = BlockPortal.field_150001_a[p_i45415_5_][0];
            this.field_150866_c = BlockPortal.field_150001_a[p_i45415_5_][1];

            int j1;
            for(j1 = p_i45415_3_; p_i45415_3_ > j1 - 21 && p_i45415_3_ > 0 && this.func_150857_a(p_world.getBlock(p_i45415_2_, p_i45415_3_ - 1, p_i45415_4_)); --p_i45415_3_) {
                ;
            }

            j1 = this.func_150853_a(p_i45415_2_, p_i45415_3_, p_i45415_4_, this.field_150863_d) - 1;
            if(j1 >= 0) {
                this.field_150861_f = new ChunkCoordinates(p_i45415_2_ + j1 * Direction.offsetX[this.field_150863_d], p_i45415_3_, p_i45415_4_ + j1 * Direction.offsetZ[this.field_150863_d]);
                this.field_150868_h = this.func_150853_a(this.field_150861_f.posX, this.field_150861_f.posY, this.field_150861_f.posZ, this.field_150866_c);
                if(this.field_150868_h < 1 || this.field_150868_h > 21) {
                    this.field_150861_f = null;
                    this.field_150868_h = 0;
                }
            }

            if(this.field_150861_f != null) {
                this.field_150862_g = this.func_150858_a();
            }

        }

        protected int func_150853_a(int p_150853_1_, int p_150853_2_, int p_150853_3_, int p_150853_4_) {
            int j1 = Direction.offsetX[p_150853_4_];
            int k1 = Direction.offsetZ[p_150853_4_];

            int i1;
            Block block;
            for(i1 = 0; i1 < 22; ++i1) {
                block = this.world.getBlock(p_150853_1_ + j1 * i1, p_150853_2_, p_150853_3_ + k1 * i1);
                if(!this.func_150857_a(block)) {
                    break;
                }

                Block block1 = this.world.getBlock(p_150853_1_ + j1 * i1, p_150853_2_ - 1, p_150853_3_ + k1 * i1);
                if(block1 != frame) {
                    break;
                }
            }

            block = this.world.getBlock(p_150853_1_ + j1 * i1, p_150853_2_, p_150853_3_ + k1 * i1);
            return block == frame?i1:0;
        }

        protected int func_150858_a() {
            int i;
            int j;
            int k;
            int l;
            label56:
            for(this.field_150862_g = 0; this.field_150862_g < 21; ++this.field_150862_g) {
                i = this.field_150861_f.posY + this.field_150862_g;

                for(j = 0; j < this.field_150868_h; ++j) {
                    k = this.field_150861_f.posX + j * Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][1]];
                    l = this.field_150861_f.posZ + j * Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][1]];
                    Block block = this.world.getBlock(k, i, l);
                    if(!this.func_150857_a(block)) {
                        break label56;
                    }

                    if(block == portal) {
                        ++this.field_150864_e;
                    }

                    if(j == 0) {
                        block = this.world.getBlock(k + Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][0]], i, l + Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][0]]);
                        if(block != frame) {
                            break label56;
                        }
                    } else if(j == this.field_150868_h - 1) {
                        block = this.world.getBlock(k + Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][1]], i, l + Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][1]]);
                        if(block != frame) {
                            break label56;
                        }
                    }
                }
            }

            for(i = 0; i < this.field_150868_h; ++i) {
                j = this.field_150861_f.posX + i * Direction.offsetX[BlockPortal.field_150001_a[this.field_150865_b][1]];
                k = this.field_150861_f.posY + this.field_150862_g;
                l = this.field_150861_f.posZ + i * Direction.offsetZ[BlockPortal.field_150001_a[this.field_150865_b][1]];
                if(this.world.getBlock(j, k, l) != frame) {
                    this.field_150862_g = 0;
                    break;
                }
            }

            if(this.field_150862_g <= 21 && this.field_150862_g >= 2) {
                return this.field_150862_g;
            } else {
                this.field_150861_f = null;
                this.field_150868_h = 0;
                this.field_150862_g = 0;
                return 0;
            }
        }

        protected boolean func_150857_a(Block p_150857_1_) {
            return p_150857_1_.getMaterial() == Material.air || p_150857_1_ == Blocks.fire || p_150857_1_ == portal;
        }

        public boolean func_150860_b() {
            return this.field_150861_f != null && this.field_150868_h >= 1 && this.field_150868_h <= 21 && this.field_150862_g >= 2 && this.field_150862_g <= 21;
        }

        public void func_150859_c() {
            for(int i = 0; i < this.field_150868_h; ++i) {
                int j = this.field_150861_f.posX + Direction.offsetX[this.field_150866_c] * i;
                int k = this.field_150861_f.posZ + Direction.offsetZ[this.field_150866_c] * i;

                for(int l = 0; l < this.field_150862_g; ++l) {
                    int i1 = this.field_150861_f.posY + l;
                    this.world.setBlock(j, i1, k, portal, this.field_150865_b, 2);
                }
            }

        }
    }
}

