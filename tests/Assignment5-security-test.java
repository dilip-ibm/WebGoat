// DevSheriff auto-generated regression test for CUSTOM-SQL-007
// Tests for SQL injection fix in Assignment5.java

package org.owasp.webgoat.lessons.challenges.challenge5;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.owasp.webgoat.container.assignments.AttackResult;
import org.owasp.webgoat.container.flags.Flags;

class Assignment5SecurityTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private Flags flags;

    private Assignment5 assignment5;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        assignment5 = new Assignment5(dataSource, flags);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    @DisplayName("Exploit Test: SQL injection attack should FAIL (vulnerability closed)")
    void testSqlInjectionAttackBlocked() throws Exception {
        // Arrange: SQL injection payload that would bypass authentication
        String maliciousUsername = "larry' OR '1'='1";
        String maliciousPassword = "anything' OR '1'='1";
        
        // Mock: No results returned (attack blocked by parameterized query)
        when(resultSet.next()).thenReturn(false);

        // Act: Attempt SQL injection
        AttackResult result = assignment5.login(maliciousUsername, maliciousPassword);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "SQL injection attack should be blocked");
        
        // Verify: PreparedStatement was used with parameters (not string concatenation)
        verify(preparedStatement).setString(1, maliciousUsername);
        verify(preparedStatement).setString(2, maliciousPassword);
        verify(preparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Normal Use Test: Legitimate login should work correctly")
    void testLegitimateLoginWorks() throws Exception {
        // Arrange: Valid credentials
        String validUsername = "larry";
        String validPassword = "correctPassword123";
        
        // Mock: Valid user found
        when(resultSet.next()).thenReturn(true);
        when(flags.getFlag(5)).thenReturn("FLAG{test_flag}");

        // Act: Legitimate login
        AttackResult result = assignment5.login(validUsername, validPassword);

        // Assert: Login should succeed
        assertTrue(result.isSuccess(), "Legitimate login should work");
        
        // Verify: Correct parameters were set
        verify(preparedStatement).setString(1, validUsername);
        verify(preparedStatement).setString(2, validPassword);
    }

    @Test
    @DisplayName("Edge Case Test: Special characters in password should be handled safely")
    void testSpecialCharactersHandled() throws Exception {
        // Arrange: Password with special SQL characters
        String username = "larry";
        String passwordWithSpecialChars = "p@ss'word\"--/*;DROP TABLE users;";
        
        // Mock: No match (wrong password)
        when(resultSet.next()).thenReturn(false);

        // Act: Login with special characters
        AttackResult result = assignment5.login(username, passwordWithSpecialChars);

        // Assert: Should fail gracefully without SQL errors
        assertFalse(result.isSuccess(), "Login with wrong password should fail");
        
        // Verify: Special characters were safely parameterized
        verify(preparedStatement).setString(1, username);
        verify(preparedStatement).setString(2, passwordWithSpecialChars);
        verify(preparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Edge Case Test: Empty credentials should be handled")
    void testEmptyCredentials() throws Exception {
        // Arrange: Empty strings
        String emptyUsername = "";
        String emptyPassword = "";
        
        // Mock: No results
        when(resultSet.next()).thenReturn(false);

        // Act: Login with empty credentials
        AttackResult result = assignment5.login(emptyUsername, emptyPassword);

        // Assert: Should fail
        assertFalse(result.isSuccess(), "Empty credentials should fail");
    }

    @Test
    @DisplayName("Edge Case Test: Null values should not cause SQL injection")
    void testNullValuesHandled() throws Exception {
        // Arrange: Null username (simulating missing parameter)
        String nullUsername = null;
        String password = "test";

        // Act & Assert: Should handle gracefully
        assertDoesNotThrow(() -> {
            assignment5.login(nullUsername, password);
        }, "Null values should not cause exceptions");
    }

    @Test
    @DisplayName("Exploit Test: UNION-based SQL injection should FAIL")
    void testUnionBasedSqlInjectionBlocked() throws Exception {
        // Arrange: UNION attack payload
        String unionAttack = "larry' UNION SELECT 'hacked' FROM users--";
        String password = "anything";
        
        // Mock: No results (attack blocked)
        when(resultSet.next()).thenReturn(false);

        // Act: Attempt UNION injection
        AttackResult result = assignment5.login(unionAttack, password);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "UNION-based SQL injection should be blocked");
        
        // Verify: Attack string was safely parameterized
        verify(preparedStatement).setString(1, unionAttack);
    }

    @Test
    @DisplayName("Exploit Test: Comment-based SQL injection should FAIL")
    void testCommentBasedSqlInjectionBlocked() throws Exception {
        // Arrange: Comment attack to bypass password check
        String username = "larry'--";
        String password = "ignored";
        
        // Mock: No results (attack blocked)
        when(resultSet.next()).thenReturn(false);

        // Act: Attempt comment injection
        AttackResult result = assignment5.login(username, password);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "Comment-based SQL injection should be blocked");
    }
}

// Made with Bob
