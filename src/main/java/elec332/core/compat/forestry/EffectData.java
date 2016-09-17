package elec332.core.compat.forestry;

import forestry.api.genetics.IEffectData;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 25-8-2016.
 */
public class EffectData implements IEffectData {


    public EffectData(int intCount, int boolCount, int floatCount) {
        this.intData = new int[intCount];
        this.boolData = new boolean[boolCount];
        this.floatData = new float[floatCount];
    }

    private int[] intData;
    private boolean[] boolData;
    private float[] floatData;

    public void setInteger(int index, int i) {
        if(index >= 0 && index < this.intData.length) {
            this.intData[index] = i;
        }
    }

    public void setFloat(int index, float f) {
        if(index >= 0 && index < this.floatData.length) {
            this.floatData[index] = f;
        }
    }

    public void setBoolean(int index, boolean b) {
        if(index >= 0 && index < this.boolData.length) {
            this.boolData[index] = b;
        }
    }

    public int getInteger(int index) {
        int i = 0;
        if(index >= 0 && index < this.intData.length) {
            i = this.intData[index];
        }
        return i;
    }

    public float getFloat(int index) {
        float f = 0.0F;
        if(index >= 0 && index < this.floatData.length) {
            f = this.floatData[index];
        }
        return f;
    }

    public boolean getBoolean(int index) {
        boolean b = false;
        if (index >= 0 && index < this.boolData.length) {
            b = this.boolData[index];
        }
        return b;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        return nbttagcompound;
    }

}

