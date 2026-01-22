package com.cass.security;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static com.itextpdf.kernel.pdf.PdfName.Properties;

public class ConfigProperties {
    private static final Properties properties = new Properties();

    protected static Properties loadProps() {
        String path = "src/main/resources/application.properties";
        try (InputStream input = ConfigProperties.class.getResourceAsStream("application.properties")) {
            if (input == null) {
                // Fallback to a file system path if the resource stream is not found
                FileInputStream fileInput = new FileInputStream(path);
                properties.load(fileInput);
            } else {
                properties.load(input);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load properties file", e);
        }
        return properties;
    }
}
