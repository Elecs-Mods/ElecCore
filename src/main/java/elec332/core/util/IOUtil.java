package elec332.core.util;

import elec332.core.main.ElecCore;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Elec332 on 20-7-2016.
 */
public class IOUtil {

    public static class NBT {

        public static NBTTagCompound readWithPossibleBackup(@Nonnull File file) throws IOException {
            try {
                if (!file.exists()) {
                    createFile(file);
                    return new NBTTagCompound();
                }
                return read(file);
            } catch (EOFException e) {
                ElecCore.logger.error("Error reading NBT files, something weird must have happened when you last shutdown MC, unfortunately, some game data will be lost. Fixing file now....");
                String date = (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());
                String ext = file.getName().replace('.', ' ').split(" ")[1];
                File newFile = new File(file.getCanonicalPath().replace("." + ext, "-" + date + "." + ext));
                FileUtils.moveFile(file, newFile);
                createFile(file);
                try {
                    File backup = getBackupFile(file);
                    if (backup.exists()) {
                        return read(backup);
                    }
                } catch (IOException ex) {
                    ElecCore.logger.info("Failed to read backup file: " + file);
                }
                return new NBTTagCompound();
            }
        }

        public static void writeWithBackup(@Nonnull File file, @Nonnull NBTTagCompound tag) throws IOException {
            if (file.exists()) {
                FileUtils.copyFile(file, getBackupFile(file));
            }
            write(file, tag);
        }

        public static void safeWrite(@Nonnull File file, @Nonnull NBTTagCompound tag) throws IOException {
            CompressedStreamTools.safeWrite(tag, file);
        }

        public static NBTTagCompound read(@Nonnull File file) throws IOException {
            return CompressedStreamTools.read(file);
        }

        public static void write(@Nonnull File file, @Nonnull NBTTagCompound tag) throws IOException {
            CompressedStreamTools.write(tag, file);
        }

    }

    public static File getBackupFile(@Nonnull File file) throws IOException {
        return new File(file.getCanonicalPath()+"_back");
    }

    public static void createFile(@Nonnull File file) throws IOException {
        if (!file.exists()) {
            if (!file.getParentFile().mkdirs() && !file.createNewFile()) {
                throw new IOException();
            }
        }
    }

}
