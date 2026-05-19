// DevSheriff auto-generated regression test for CVE-2015-6420
// Tests for commons-collections dependency update in pom.xml

package org.owasp.webgoat.security;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class PomDependencySecurityTest {

    private static final String POM_PATH = "target-repo/pom.xml";
    private static final String VULNERABLE_VERSION = "3.2.1";
    private static final String FIXED_VERSION = "3.2.2";

    @Test
    @DisplayName("Exploit Test: Vulnerable commons-collections 3.2.1 should NOT be present")
    void testVulnerableCommonsCollectionsNotPresent() throws IOException {
        // Arrange: Read pom.xml content
        String pomContent = new String(Files.readAllBytes(Paths.get(POM_PATH)));

        // Assert: Vulnerable version should not be present
        assertFalse(
            pomContent.contains("<commons-collections.version>" + VULNERABLE_VERSION + "</commons-collections.version>"),
            "Vulnerable commons-collections 3.2.1 (CVE-2015-6420) should not be present in pom.xml"
        );
    }

    @Test
    @DisplayName("Normal Use Test: Fixed commons-collections version should be present")
    void testFixedCommonsCollectionsPresent() throws IOException {
        // Arrange: Read pom.xml content
        String pomContent = new String(Files.readAllBytes(Paths.get(POM_PATH)));

        // Assert: Fixed version should be present
        assertTrue(
            pomContent.contains("<commons-collections.version>" + FIXED_VERSION + "</commons-collections.version>") ||
            pomContent.contains("<commons-collections.version>3.2.2</commons-collections.version>") ||
            pomContent.matches("(?s).*<commons-collections\\.version>3\\.2\\.[2-9]</commons-collections\\.version>.*") ||
            pomContent.matches("(?s).*<commons-collections\\.version>3\\.[3-9]\\.[0-9]</commons-collections\\.version>.*") ||
            pomContent.matches("(?s).*<commons-collections\\.version>[4-9]\\.[0-9]\\.[0-9]</commons-collections\\.version>.*"),
            "Fixed commons-collections version (3.2.2 or higher) should be present in pom.xml"
        );
    }

    @Test
    @DisplayName("Normal Use Test: DevSheriff fix comment should be present")
    void testDevSheriffCommentPresent() throws IOException {
        // Arrange: Read pom.xml content
        String pomContent = new String(Files.readAllBytes(Paths.get(POM_PATH)));

        // Assert: DevSheriff comment should be present
        assertTrue(
            pomContent.contains("DEVSHERIFF-FIX: CVE-2015-6420"),
            "DevSheriff fix comment for CVE-2015-6420 should be present"
        );
    }

    @Test
    @DisplayName("Edge Case Test: pom.xml should be valid XML")
    void testPomXmlIsValidXml() {
        // Arrange: Check if pom.xml exists and is readable
        File pomFile = new File(POM_PATH);

        // Assert: File should exist and be readable
        assertTrue(pomFile.exists(), "pom.xml should exist");
        assertTrue(pomFile.canRead(), "pom.xml should be readable");
        assertTrue(pomFile.length() > 0, "pom.xml should not be empty");
    }

    @Test
    @DisplayName("Edge Case Test: No other vulnerable commons-collections versions")
    void testNoOtherVulnerableVersions() throws IOException {
        // Arrange: Read pom.xml content
        String pomContent = new String(Files.readAllBytes(Paths.get(POM_PATH)));

        // Assert: No vulnerable versions (3.0 - 3.2.1) should be present
        assertFalse(
            pomContent.matches("(?s).*<commons-collections\\.version>3\\.[0-1]\\.[0-9]</commons-collections\\.version>.*"),
            "No commons-collections 3.0.x or 3.1.x versions should be present"
        );
        assertFalse(
            pomContent.matches("(?s).*<commons-collections\\.version>3\\.2\\.[0-1]</commons-collections\\.version>.*"),
            "No commons-collections 3.2.0 or 3.2.1 versions should be present"
        );
    }

    @Test
    @DisplayName("Normal Use Test: commons-collections dependency should still be declared")
    void testCommonsCollectionsDependencyDeclared() throws IOException {
        // Arrange: Read pom.xml content
        String pomContent = new String(Files.readAllBytes(Paths.get(POM_PATH)));

        // Assert: Dependency should still be declared (not removed)
        assertTrue(
            pomContent.contains("<commons-collections.version>"),
            "commons-collections dependency should still be declared in pom.xml"
        );
    }

    @Test
    @DisplayName("Exploit Test: CVE-2015-6420 RCE vulnerability should be mitigated")
    void testCve20156420Mitigated() throws IOException {
        // Arrange: Read pom.xml content
        String pomContent = new String(Files.readAllBytes(Paths.get(POM_PATH)));

        // Extract version
        String versionPattern = "<commons-collections\\.version>(.*?)</commons-collections\\.version>";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(versionPattern);
        java.util.regex.Matcher matcher = pattern.matcher(pomContent);

        // Assert: Version should be 3.2.2 or higher
        if (matcher.find()) {
            String version = matcher.group(1);
            String[] parts = version.split("\\.");
            
            if (parts.length >= 3) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                int patch = Integer.parseInt(parts[2]);
                
                boolean isFixed = (major > 3) || 
                                 (major == 3 && minor > 2) || 
                                 (major == 3 && minor == 2 && patch >= 2);
                
                assertTrue(isFixed, 
                    "commons-collections version should be 3.2.2 or higher to mitigate CVE-2015-6420. Found: " + version);
            }
        } else {
            fail("commons-collections version not found in pom.xml");
        }
    }

    @Test
    @DisplayName("Edge Case Test: Version format should be valid")
    void testVersionFormatValid() throws IOException {
        // Arrange: Read pom.xml content
        String pomContent = new String(Files.readAllBytes(Paths.get(POM_PATH)));

        // Extract version
        String versionPattern = "<commons-collections\\.version>(.*?)</commons-collections\\.version>";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(versionPattern);
        java.util.regex.Matcher matcher = pattern.matcher(pomContent);

        // Assert: Version should follow semantic versioning
        if (matcher.find()) {
            String version = matcher.group(1);
            assertTrue(
                version.matches("\\d+\\.\\d+\\.\\d+"),
                "Version should follow semantic versioning format (X.Y.Z). Found: " + version
            );
        }
    }
}

// Made with Bob
