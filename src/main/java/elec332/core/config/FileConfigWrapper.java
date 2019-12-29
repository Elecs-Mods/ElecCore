package elec332.core.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.WritingMode;
import elec332.core.util.FieldPointer;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by Elec332 on 29-12-2019
 */
public class FileConfigWrapper extends AbstractConfigWrapper {

    public FileConfigWrapper(File location) {
        this(location.toPath());
    }

    public FileConfigWrapper(Path location) {
        this(CommentedFileConfig.builder(location).sync().autosave().writingMode(WritingMode.REPLACE).build());
    }

    public FileConfigWrapper(CommentedConfig config) {
        this.cfg = config;
    }

    private final CommentedConfig cfg;

    @Override
    protected void registerConfigSpec() {
        getSpec().setConfig(cfg);
        try {
            if (cfg instanceof FileConfig && hasAutoReload.test(((FileConfig) cfg).getFile().toPath())) {
                FileWatcher.defaultInstance().setWatch(((FileConfig) cfg).getFile(), this::load);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void postRegister() {
        load();
    }

    /**
     * Refreshes the config values
     */
    public void load() {
        if (getSpec() == null) {
            throw new IllegalStateException();
        }
        if (blockLoad()) {
            return;
        }
        if (cfg instanceof FileConfig) {
            ((FileConfig) cfg).load();
        }
        runLoadTasks();
    }

    private static final Predicate<Path> hasAutoReload;

    static {
        final FieldPointer<FileWatcher, Map> watchedFiles = new FieldPointer<>(FileWatcher.class, "watchedFiles");
        hasAutoReload = p -> watchedFiles.get(FileWatcher.defaultInstance()).containsKey(p.toAbsolutePath());
    }

}
