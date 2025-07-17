package ua.idzo.resource.core.util;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

@Component
public class FileUtil {

    public final static String AUDIO_MPEG_CONTENT_TYPE = "audio/mpeg";
    private final static Tika tika = new Tika();

    public static boolean isAudioMpeg(byte[] data) {
        String detectedType = tika.detect(data);
        return AUDIO_MPEG_CONTENT_TYPE.equals(detectedType);
    }
}
