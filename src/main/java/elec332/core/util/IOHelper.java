package elec332.core.util;

import elec332.core.ElecCore;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Elec332 on 13-8-2018.
 */
public class IOHelper {

    /**
     * Gets the configuration file with the specified name
     * in the config directory
     *
     * @param fileName The config file name
     * @return The config file
     */
    public static File getConfigFile(String fileName) {
        return new File(getConfigDir().toFile(), fileName);
    }

    /**
     * @return The configuration directory
     */
    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static final IObjectIO<CompoundNBT> NBT_IO, NBT_COMPRESSED_IO;

    /**
     * Gets the {@link InputStream} from the provided {@link ResourceLocation},
     * also works on the {@link net.minecraftforge.api.distmarker.Dist#DEDICATED_SERVER}
     * (MC's method doesn't)
     *
     * @param resourceLocation The location of the resource
     * @return The {@link InputStream} from the provided {@link ResourceLocation}
     */
    @Nonnull
    public static InputStream getFromResource(@Nonnull ResourceLocation resourceLocation) throws IOException {
        String location = "/assets/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath();
        InputStream ret = ElecCore.class.getResourceAsStream(location);
        if (ret != null) {
            return ret;
        }
        throw new FileNotFoundException(location);
    }

    /**
     * Makes sure the folder exists, tries to create it if it doesn't.
     * If it doesn't exist, and fails to creates it, it throws a {@link RuntimeException}
     *
     * @param folder The folder to be checked
     */
    public static void ensureExists(File folder) {
        if (!folder.exists() && !folder.mkdir()) {
            throw new RuntimeException();
        }
    }

    /**
     * Checkes whether the specified files exists, and if it does, it tries to delete it.
     * Throws an {@link RuntimeException} if it fails to delete it.
     *
     * @param file The file to be deleted
     */
    public static void deleteIfExists(File file) {
        if (file.exists() && !file.delete()) {
            throw new RuntimeException();
        }
    }

    /**
     * tries to read an object from a file. If the specified file has errored,
     * or doesn't exist at all, it tries to read from a backup file
     *
     * @param file The file ro be read
     * @param io   The IO implementation
     * @return The object read from the specified file
     */
    public static <T> T readWithPossibleBackup(@Nonnull File file, @Nonnull IObjectIO<T> io) {
        File backup = null;
        boolean b = false;
        try {
            backup = getBackupFile(file);
            if (!file.exists()) {
                if (!backup.exists()) {
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
                if (b) {
                    return io.returnOnReadFail();
                }
                return io.read(backup);
            } catch (IOException ex) {
                ElecCore.logger.info("Failed to read backup file: " + file);
            }
            return io.returnOnReadFail();
        }
    }

    /**
     * Writes the specified object ({@param obj}) to a file,
     * uses the old save file as a backup in case the write fails, of the program is suddenly stopped
     *
     * @param file The file to write to
     * @param obj  The object to be written to the specified file
     * @param io   The IO implementation
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static <T> void writeWithBackup(@Nonnull File file, @Nonnull T obj, @Nonnull IObjectIO<T> io) {
        File fileBack, fileNew;
        try {
            fileBack = getBackupFile(file);
            fileNew = new File(file.getCanonicalPath() + "_new");
            io.write(fileNew, obj);
        } catch (Exception e) {
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

    /**
     * Gets the backup filename of a file
     *
     * @param file The file
     * @return The backup filename of the specified file
     */
    public static File getBackupFile(@Nonnull File file) throws IOException {
        return new File(file.getCanonicalPath() + "_back");
    }

    /**
     * Utility class used to write objects to a file
     */
    public interface IObjectIO<T> {

        /**
         * Attempts to write an object to a file
         *
         * @param file The file
         * @param obj  The object to be written to the specified file
         * @throws IOException When the operation has failed
         */
        default public void write(File file, T obj) throws IOException {
            throw new UnsupportedOperationException();
        }

        /**
         * Attempts to read an object from a file
         *
         * @param file The file
         * @return The object read from the specified file
         * @throws IOException When the operation has failed
         */
        default public T read(File file) throws IOException {
            throw new UnsupportedOperationException();
        }

        /**
         * @return A default value, gets called when the read has failed
         */
        default public T returnOnReadFail() {
            throw new RuntimeException();
        }

    }

    static {
        NBT_IO = new IObjectIO<CompoundNBT>() {

            @Override
            public void write(File file, CompoundNBT obj) throws IOException {
                CompressedStreamTools.write(obj, file);
            }

            @Override
            public CompoundNBT read(File file) throws IOException {
                return CompressedStreamTools.read(file);
            }

            @Override
            public CompoundNBT returnOnReadFail() {
                return new CompoundNBT();
            }

        };

        NBT_COMPRESSED_IO = new IObjectIO<CompoundNBT>() {

            @Override
            public void write(File file, CompoundNBT obj) throws IOException {
                CompressedStreamTools.writeCompressed(obj, new FileOutputStream(file));
            }

            @Override
            public CompoundNBT read(File file) throws IOException {
                return CompressedStreamTools.readCompressed(new FileInputStream(file));
            }

            @Override
            public CompoundNBT returnOnReadFail() {
                return new CompoundNBT();
            }

        };
    }

}
