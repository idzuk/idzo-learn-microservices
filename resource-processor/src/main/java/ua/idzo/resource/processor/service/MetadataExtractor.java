package ua.idzo.resource.processor.service;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class MetadataExtractor {

    private final Metadata metadata;

    private MetadataExtractor(Metadata metadata) {
        this.metadata = metadata;
    }

    public static MetadataExtractor getExtractor(byte[] fileData) {
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);

        try (InputStream stream = new ByteArrayInputStream(fileData)) {
            parser.parse(stream, handler, metadata, new ParseContext());
        } catch (Exception e) {
            throw new RuntimeException("Failed at parsing file metadata", e);
        }

        return new MetadataExtractor(metadata);
    }

    public String getName() {
        return getValue(TikaCoreProperties.TITLE);
    }

    public String getArtist() {
        return getValue(XMPDM.ARTIST, TikaCoreProperties.CREATOR);
    }

    public String getAlbum() {
        return getValue(XMPDM.ALBUM);
    }

    public String getYear() {
        String releaseDate = getValue(XMPDM.RELEASE_DATE, TikaCoreProperties.METADATA_DATE);
        if (releaseDate != null && releaseDate.length() >= 4) {
            return releaseDate.substring(0, 4);
        }
        return null;
    }

    public String getDuration() {
        String durationInSeconds = getValue(XMPDM.DURATION);
        if (durationInSeconds == null) {
            return null;
        }
        try {
            double millis = Double.parseDouble(durationInSeconds);
            double seconds = millis / 1000.0;
            long totalSeconds = Math.round(seconds);

            long minutes = totalSeconds / 60;
            long remainingSeconds = totalSeconds % 60;

            return String.format("%02d:%02d", minutes, remainingSeconds);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getValue(Property... properties) {
        return Arrays.stream(properties)
                .map(metadata::get)
                .filter(value -> value != null && !value.isEmpty())
                .findFirst()
                .orElse(null);
    }
}