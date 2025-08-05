package homer.tastyworld.frontend.starterpack.base.utils.misc;

import java.io.File;

public class FileDirectories {

    public static void create(File dir) {
        dir.mkdirs();
    }

    private static void deleteFileOrDir(File obj) {
        if (obj.exists()) {
            obj.delete();
        }
    }

    public static void clean(File dir) {
        if (!dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    clean(file);
                }
                deleteFileOrDir(file);
            }
        }
    }

    public static void delete(File dir) {
        clean(dir);
        deleteFileOrDir(dir);
    }

}
