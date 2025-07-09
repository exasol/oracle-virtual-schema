package com.exasol.adapter.dialects.oracle;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exasol.adapter.dialects.oracle.release.ExasolDbVersion;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolDockerImageReference;

class ExasolVersionCheck {

    private ExasolVersionCheck() {
        // Not instantiable
    }

    static void assumeExasolVersion834OrLater(ExasolContainer<?> exasolContainer) {
        final ExasolDockerImageReference imageReference = exasolContainer.getDockerImageReference();
        final ExasolDbVersion exasolDbVersion = ExasolDbVersion.of(imageReference.getMajor(), imageReference.getMinor(), imageReference.getFixVersion());
        assumeTrue(exasolDbVersion.isGreaterOrEqualThan(ExasolDbVersion.parse("8.34.0")), "Expected Exasol version 8.34 or higher, but got '" + getExasolDbVersion(imageReference) + "'");
    }

    private static String getExasolDbVersion(ExasolDockerImageReference imageReference) {
        return String.format("%d.%d.%d", imageReference.getMajor(), imageReference.getMinor(), imageReference.getFixVersion());
    }
}
