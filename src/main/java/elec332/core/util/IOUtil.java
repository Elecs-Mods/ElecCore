package elec332.core.util;

import elec332.core.main.ElecCore;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Elec332 on 20-7-2016.
 */
@SuppressWarnings("all")
public class IOUtil {

    @Nonnull
    public static InputStream getFromResource(@Nonnull ResourceLocation resourceLocation) throws IOException {
        String location = "/assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath();
        InputStream ret = FileHelper.class.getResourceAsStream(location);
        if (ret != null)
            return ret;
        throw new FileNotFoundException(location);
    }

    public static <T> T readWithPossibleBackup(@Nonnull File file, @Nonnull IObjectIO<T> io) {
        File backup;
        boolean b = false;
        try {
            backup = getBackupFile(file);
            if (!file.exists()){
                if (!backup.exists()){
                    return io.returnOnReadFail();
                }
                b = true;
                return io.read(backup);
            }
            return io.read(file);
        } catch (IOException e) {
            try {
                ElecCore.logger.error("Error reading file, something weird must have happened when you last shutdown MC, unfortunately, some game data will probably be lost. Fixing file now....");
                String date = (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());
                File newFile = new File(file.getCanonicalPath() + "_errored_" + date);
                FileUtils.moveFile(file, newFile);
                if (file.exists()) {
                    file.delete();
                }
                backup = getBackupFile(file);
                if (b){
                    return io.returnOnReadFail();
                }
                return io.read(backup);
            } catch (IOException ex) {
                ElecCore.logger.info("Failed to read backup file: " + file);
            }
            return io.returnOnReadFail();
        }
    }

    public static <T> void writeWithBackup(@Nonnull File file, @Nonnull T obj, @Nonnull IObjectIO<T> io) {
        File fileBack, fileNew;
        try {
            fileBack = getBackupFile(file);
            fileNew = new File(file.getCanonicalPath() + "_new");
            io.write(fileNew, obj);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        if (fileBack.exists()){
            fileBack.delete();
        }
        file.renameTo(fileBack);
        if (file.exists()){
            file.delete();
        }
        fileNew.renameTo(file);
        if (fileNew.exists()){
            fileNew.delete();
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

    public interface IObjectIO<T> {

        default public void write(File file, T obj) throws IOException {
            throw new UnsupportedOperationException();
        }

        default public T read(File file) throws IOException {
            throw new UnsupportedOperationException();
        }

        default public T returnOnReadFail(){
            throw new RuntimeException();
        }

    }

    public static class NBT {

        private static final IObjectIO<NBTTagCompound> NBTIO;

        public static NBTTagCompound readWithPossibleBackup(@Nonnull File file) {
            return IOUtil.readWithPossibleBackup(file, NBTIO);
        }

        public static void writeWithBackup(@Nonnull File file, @Nonnull NBTTagCompound tag) {
            IOUtil.writeWithBackup(file, tag, NBTIO);
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

        static {
            NBTIO = new IObjectIO<NBTTagCompound>() {

                @Override
                public void write(File file, NBTTagCompound obj) throws IOException {
                    NBT.write(file, obj);
                }

                @Override
                public NBTTagCompound read(File file) throws IOException {
                    return NBT.read(file);
                }

                @Override
                public NBTTagCompound returnOnReadFail() {
                    return new NBTTagCompound();
                }

            };
        }

    }

}
