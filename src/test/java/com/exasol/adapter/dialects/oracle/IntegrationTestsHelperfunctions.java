package com.exasol.adapter.dialects.oracle;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IntegrationTestsHelperfunctions {
    public static String getPropertyFromFile(final String resourcesDialectName, final String propertyName) {
        final String pathToPropertyFile = getPathToPropertyFile(resourcesDialectName);
        try (final InputStream inputStream = new FileInputStream(pathToPropertyFile)) {
            final Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty(propertyName);
        } catch (final IOException exception) {
            throw new IllegalArgumentException(
                    "Cannot access the properties file or read from it. Check if the path spelling is correct"
                            + " and if the file exists.",exception);
        }
    }
    public static String getPathToPropertyFile(final String resourcesDialectName) {
        return "src/test/resources/integration/driver/" + resourcesDialectName + "/" + resourcesDialectName
                + ".properties";
    }
}
