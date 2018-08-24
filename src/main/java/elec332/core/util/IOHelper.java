package elec332.core.util;

import elec332.core.ElecCore;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Elec332 on 13-8-2018.
 */
public class IOHelper {

    public static final IObjectIO<NBTTagCompound> NBT_IO, NBT_COMPRESSED_IO;

    @Nonnull
    public static InputStream getFromResource(@Nonnull ResourceLocation resourceLocation) throws IOException {
        String location = "/assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath();
        InputStream ret = ElecCore.class.getResourceAsStream(location);
        if (ret != null) {
            return ret;
        }
        throw new FileNotFoundException(location);
    }

    public static void ensureExists(File folder){
        if (!folder.exists() && !folder.mkdir()){
            throw new RuntimeException();
        }
    }

    public static void deleteIfExists(File file){
        if (file.exists() && !file.delete()) {
            throw new RuntimeException();
        }
    }

    public static <T> T readWithPossibleBackup(@Nonnull File file, @Nonnull IObjectIO<T> io) {
        File backup = null;
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
                file = b ? backup : file;
                File newFile = new File(file.getCanonicalPath() + "_errored_" + date);
                FileUtils.moveFile(file, newFile);
                deleteIfExists(file);
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
        if (file.exists()) {
            deleteIfExists(fileBack);
            file.renameTo(fileBack);
        }
        deleteIfExists(file);
        fileNew.renameTo(file);
        deleteIfExists(fileNew);
    }

    public static File getBackupFile(@Nonnull File file) throws IOException {
        return new File(file.getCanonicalPath()+"_back");
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

    static {
        NBT_IO = new IObjectIO<NBTTagCompound>() {

            @Override
            public void write(File file, NBTTagCompound obj) throws IOException {
                CompressedStreamTools.write(obj, file);
            }

            @Override
            public NBTTagCompound read(File file) throws IOException {
                return CompressedStreamTools.read(file);
            }

            @Override
            public NBTTagCompound returnOnReadFail() {
                return new NBTTagCompound();
            }

        };

        NBT_COMPRESSED_IO = new IObjectIO<NBTTagCompound>() {

            @Override
            public void write(File file, NBTTagCompound obj) throws IOException {
                CompressedStreamTools.writeCompressed(obj, new FileOutputStream(file));
            }

            @Override
            public NBTTagCompound read(File file) throws IOException {
                return CompressedStreamTools.readCompressed(new FileInputStream(file));
            }

            @Override
            public NBTTagCompound returnOnReadFail() {
                return new NBTTagCompound();
            }

        };
    }

}
