// DevSheriff auto-generated regression test for CUSTOM-SQL-004
// Tests for SQL injection fix in SqlInjectionChallenge.java

package org.owasp.webgoat.lessons.sqlinjection.advanced;

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

class SqlInjectionChallengeSecurityTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement checkStatement;

    @Mock
    private PreparedStatement insertStatement;

    @Mock
    private ResultSet resultSet;

    private SqlInjectionChallenge challenge;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        challenge = new SqlInjectionChallenge(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    @DisplayName("Exploit Test: SQL injection in username check should FAIL")
    void testSqlInjectionInUsernameCheckBlocked() throws Exception {
        // Arrange: SQL injection to bypass user existence check
        String maliciousUsername = "admin' OR '1'='1";
        String email = "test@example.com";
        String password = "password123";
        
        // Mock: PreparedStatement for check query
        when(connection.prepareStatement("select userid from sql_challenge_users where userid = ?"))
            .thenReturn(checkStatement);
        when(checkStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // User doesn't exist
        
        // Mock: PreparedStatement for insert
        when(connection.prepareStatement("INSERT INTO sql_challenge_users VALUES (?, ?, ?)"))
            .thenReturn(insertStatement);

        // Act: Attempt registration with SQL injection
        AttackResult result = challenge.registerNewUser(maliciousUsername, email, password);

        // Assert: Should create user with exact malicious string (not execute injection)
        assertTrue(result.getMessage().contains("user.created"), "User should be created with malicious string as username");
        
        // Verify: PreparedStatement was used with parameters
        verify(checkStatement).setString(1, maliciousUsername);
        verify(insertStatement).setString(1, maliciousUsername);
    }

    @Test
    @DisplayName("Normal Use Test: Legitimate user registration should work")
    void testLegitimateRegistrationWorks() throws Exception {
        // Arrange: Valid registration data
        String username = "newuser";
        String email = "newuser@example.com";
        String password = "SecurePass123!";
        
        // Mock: User doesn't exist
        when(connection.prepareStatement("select userid from sql_challenge_users where userid = ?"))
            .thenReturn(checkStatement);
        when(checkStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        
        // Mock: Insert succeeds
        when(connection.prepareStatement("INSERT INTO sql_challenge_users VALUES (?, ?, ?)"))
            .thenReturn(insertStatement);

        // Act: Legitimate registration
        AttackResult result = challenge.registerNewUser(username, email, password);

        // Assert: Registration should succeed
        assertTrue(result.getMessage().contains("user.created"), "Legitimate registration should work");
        
        // Verify: Correct parameters were set
        verify(checkStatement).setString(1, username);
        verify(insertStatement).setString(1, username);
        verify(insertStatement).setString(2, email);
        verify(insertStatement).setString(3, password);
    }

    @Test
    @DisplayName("Normal Use Test: Duplicate username should be rejected")
    void testDuplicateUsernameRejected() throws Exception {
        // Arrange: Username that already exists
        String existingUsername = "existinguser";
        String email = "test@example.com";
        String password = "password";
        
        // Mock: User already exists
        when(connection.prepareStatement("select userid from sql_challenge_users where userid = ?"))
            .thenReturn(checkStatement);
        when(checkStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true); // User exists

        // Act: Attempt to register duplicate
        AttackResult result = challenge.registerNewUser(existingUsername, email, password);

        // Assert: Should fail with user exists message
        assertFalse(result.isSuccess(), "Duplicate username should be rejected");
        assertTrue(result.getMessage().contains("user.exists"), "Should indicate user exists");
        
        // Verify: Only check was performed, no insert
        verify(checkStatement).setString(1, existingUsername);
        verify(insertStatement, never()).execute();
    }

    @Test
    @DisplayName("Edge Case Test: Username with SQL keywords should be handled")
    void testSqlKeywordsInUsernameHandled() throws Exception {
        // Arrange: Username containing SQL keywords
        String sqlKeywordUsername = "SELECT_FROM_WHERE";
        String email = "test@example.com";
        String password = "password";
        
        // Mock: User doesn't exist
        when(connection.prepareStatement("select userid from sql_challenge_users where userid = ?"))
            .thenReturn(checkStatement);
        when(checkStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        
        // Mock: Insert succeeds
        when(connection.prepareStatement("INSERT INTO sql_challenge_users VALUES (?, ?, ?)"))
            .thenReturn(insertStatement);

        // Act: Register with SQL keywords
        AttackResult result = challenge.registerNewUser(sqlKeywordUsername, email, password);

        // Assert: Should work (keywords are just text)
        assertTrue(result.getMessage().contains("user.created"), "SQL keywords as text should be allowed");
        
        // Verify: Keywords were safely parameterized
        verify(checkStatement).setString(1, sqlKeywordUsername);
    }

    @Test
    @DisplayName("Exploit Test: UNION-based injection in username should FAIL")
    void testUnionInjectionBlocked() throws Exception {
        // Arrange: UNION attack in username
        String unionAttack = "admin' UNION SELECT password FROM sql_challenge_users--";
        String email = "test@example.com";
        String password = "password";
        
        // Mock: No existing user (attack blocked)
        when(connection.prepareStatement("select userid from sql_challenge_users where userid = ?"))
            .thenReturn(checkStatement);
        when(checkStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        
        // Mock: Insert with attack string
        when(connection.prepareStatement("INSERT INTO sql_challenge_users VALUES (?, ?, ?)"))
            .thenReturn(insertStatement);

        // Act: Attempt UNION injection
        AttackResult result = challenge.registerNewUser(unionAttack, email, password);

        // Assert: Attack string stored as literal text (not executed)
        assertTrue(result.getMessage().contains("user.created"), "UNION attack should be stored as text");
        
        // Verify: Attack was parameterized
        verify(checkStatement).setString(1, unionAttack);
    }

    @Test
    @DisplayName("Edge Case Test: Empty username should be handled")
    void testEmptyUsernameHandled() throws Exception {
        // Arrange: Empty username
        String emptyUsername = "";
        String email = "test@example.com";
        String password = "password";
        
        // Mock: No existing user
        when(connection.prepareStatement("select userid from sql_challenge_users where userid = ?"))
            .thenReturn(checkStatement);
        when(checkStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        
        // Mock: Insert
        when(connection.prepareStatement("INSERT INTO sql_challenge_users VALUES (?, ?, ?)"))
            .thenReturn(insertStatement);

        // Act: Register with empty username
        AttackResult result = challenge.registerNewUser(emptyUsername, email, password);

        // Assert: Should handle gracefully
        assertNotNull(result, "Empty username should be handled");
    }

    @Test
    @DisplayName("Exploit Test: Comment-based injection should FAIL")
    void testCommentInjectionBlocked() throws Exception {
        // Arrange: Comment to bypass query logic
        String commentAttack = "admin'--";
        String email = "test@example.com";
        String password = "password";
        
        // Mock: No existing user (attack blocked)
        when(connection.prepareStatement("select userid from sql_challenge_users where userid = ?"))
            .thenReturn(checkStatement);
        when(checkStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        
        // Mock: Insert
        when(connection.prepareStatement("INSERT INTO sql_challenge_users VALUES (?, ?, ?)"))
            .thenReturn(insertStatement);

        // Act: Attempt comment injection
        AttackResult result = challenge.registerNewUser(commentAttack, email, password);

        // Assert: Comment stored as literal text
        assertTrue(result.getMessage().contains("user.created"), "Comment should be stored as text");
        
        // Verify: Comment was parameterized
        verify(checkStatement).setString(1, commentAttack);
    }

    @Test
    @DisplayName("Edge Case Test: Special characters in all fields should be handled")
    void testSpecialCharactersInAllFieldsHandled() throws Exception {
        // Arrange: Special characters in all fields
        String username = "user'\"<>@#$%";
        String email = "test'@example.com";
        String password = "pass'word\"123";
        
        // Mock: No existing user
        when(connection.prepareStatement("select userid from sql_challenge_users where userid = ?"))
            .thenReturn(checkStatement);
        when(checkStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        
        // Mock: Insert
        when(connection.prepareStatement("INSERT INTO sql_challenge_users VALUES (?, ?, ?)"))
            .thenReturn(insertStatement);

        // Act: Register with special characters
        AttackResult result = challenge.registerNewUser(username, email, password);

        // Assert: Should work correctly
        assertTrue(result.getMessage().contains("user.created"), "Special characters should be handled");
        
        // Verify: All fields were parameterized
        verify(insertStatement).setString(1, username);
        verify(insertStatement).setString(2, email);
        verify(insertStatement).setString(3, password);
    }
}

// Made with Bob
