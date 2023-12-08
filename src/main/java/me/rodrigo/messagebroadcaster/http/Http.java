package me.rodrigo.messagebroadcaster.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Http {
    public static void DownloadFile(String uri, String path) throws IOException {
        URL url = new URL(uri);
        InputStream inputStream = url.openStream();
        Path savePath = Path.of(path, url.getFile());

        Files.copy(inputStream, savePath, StandardCopyOption.REPLACE_EXISTING);
    }
}
