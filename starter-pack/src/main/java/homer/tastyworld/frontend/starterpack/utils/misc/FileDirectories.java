package homer.tastyworld.frontend.starterpack.utils.misc;

import java.io.File;

public class FileDirectories {

    public static void createDir(File dir) {
        dir.mkdirs();
    }

    private static void deleteFileOrDir(File obj) {
        if (obj.exists()) {
            obj.delete();
        }
    }

    public static void cleanDir(File dir) {
        if (!dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    cleanDir(file);
                }
                deleteFileOrDir(file);
            }
        }
    }

    public static void delete(File fileOrDir) {
        cleanDir(fileOrDir);
        deleteFileOrDir(fileOrDir);
    }

}
