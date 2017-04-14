package me.mlshv.simpletranslate.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

final class Util {
    static String readStream(InputStream in) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    private Util() {}
}
