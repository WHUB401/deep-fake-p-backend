package com.pharaoh.deepfake.common.util;
import org.apache.commons.io.FileUtils; //导入方法依赖的package包/类

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class MyFile extends File {


    public MyFile(String pathname) {
        super(pathname);
    }

    public MyFile(String parent, String child) {
        super(parent, child);
    }

    public MyFile(File parent, String child) {
        super(parent, child);
    }

    public MyFile(URI uri) {
        super(uri);
    }

    public void deleteRecursively() throws IOException {
        File file = this;
        if (file.isDirectory()) {
            FileUtils.deleteDirectory(file);
        } else {
            file.delete();
        }
    }


}
