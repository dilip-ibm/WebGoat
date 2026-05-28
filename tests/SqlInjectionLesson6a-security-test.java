// DevSheriff auto-generated regression test for CUSTOM-SQL-003
// Tests for SQL injection fix in SqlInjectionLesson6a.java

package org.owasp.webgoat.lessons.sqlinjection.advanced;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.owasp.webgoat.container.LessonDataSource;
import org.owasp.webgoat.container.assignments.AttackResult;

class SqlInjectionLesson6aSecurityTest {

    @Mock
    private LessonDataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData metaData;

    private SqlInjectionLesson6a lesson;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        lesson = new SqlInjectionLesson6a(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(3);
        when(metaData.getColumnName(anyInt())).thenReturn("column");
    }

    @Test
    @DisplayName("Exploit Test: UNION-based SQL injection should FAIL")
    void testUnionBasedSqlInjectionBlocked() throws Exception {
        // Arrange: UNION attack to extract data from other tables
        String unionAttack = "Smith' UNION SELECT userid, user_name, password FROM user_system_data--";
        
        // Mock: No results (attack blocked by parameterization)
        when(resultSet.first()).thenReturn(false);

        // Act: Attempt UNION injection
        AttackResult result = lesson.injectableQuery(unionAttack);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "UNION-based SQL injection should be blocked");
        
        // Verify: PreparedStatement was used with parameter
        verify(preparedStatement).setString(1, unionAttack);
        verify(preparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Normal Use Test: Legitimate user data query should work")
    void testLegitimateQueryWorks() throws Exception {
        // Arrange: Valid last name
        String validLastName = "Smith";
        
        // Mock: Valid results returned
        when(resultSet.first()).thenReturn(true);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString(anyInt())).thenReturn("John", "Smith", "john@example.com");
        when(resultSet.getRow()).thenReturn(1);

        // Act: Legitimate query
        AttackResult result = lesson.injectableQuery(validLastName);

        // Assert: Query should succeed
        assertTrue(result.isSuccess(), "Legitimate query should work");
        
        // Verify: Parameter was set correctly
        verify(preparedStatement).setString(1, validLastName);
    }

    @Test
    @DisplayName("Edge Case Test: Special characters should be safely handled")
    void testSpecialCharactersSafelyHandled() throws Exception {
        // Arrange: Input with various SQL special characters
        String specialChars = "O'Brien-Smith; DROP TABLE--/**/";
        
        // Mock: No results (not found, but no SQL error)
        when(resultSet.first()).thenReturn(false);

        // Act: Query with special characters
        AttackResult result = lesson.injectableQuery(specialChars);

        // Assert: Should handle without SQL errors
        assertFalse(result.isSuccess(), "Special characters should be safely parameterized");
        
        // Verify: Special characters were parameterized
        verify(preparedStatement).setString(1, specialChars);
    }

    @Test
    @DisplayName("Exploit Test: Boolean-based blind SQL injection should FAIL")
    void testBooleanBlindSqlInjectionBlocked() throws Exception {
        // Arrange: Boolean-based blind injection
        String blindInjection = "Smith' AND '1'='1";
        
        // Mock: No results (attack blocked)
        when(resultSet.first()).thenReturn(false);

        // Act: Attempt blind injection
        AttackResult result = lesson.injectableQuery(blindInjection);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "Boolean-based blind injection should be blocked");
        
        // Verify: Attack payload was parameterized
        verify(preparedStatement).setString(1, blindInjection);
    }

    @Test
    @DisplayName("Edge Case Test: Unicode characters should be handled")
    void testUnicodeCharactersHandled() throws Exception {
        // Arrange: Name with unicode characters
        String unicodeName = "Müller";
        
        // Mock: Valid results
        when(resultSet.first()).thenReturn(true);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString(anyInt())).thenReturn("Hans", "Müller", "hans@example.com");
        when(resultSet.getRow()).thenReturn(1);

        // Act: Query with unicode
        AttackResult result = lesson.injectableQuery(unicodeName);

        // Assert: Should work correctly
        assertTrue(result.isSuccess(), "Unicode characters should be handled");
        
        // Verify: Unicode was parameterized
        verify(preparedStatement).setString(1, unicodeName);
    }

    @Test
    @DisplayName("Exploit Test: Time-based blind SQL injection should FAIL")
    void testTimeBasedBlindSqlInjectionBlocked() throws Exception {
        // Arrange: Time-based blind injection attempt
        String timeBasedAttack = "Smith' AND SLEEP(5)--";
        
        // Mock: No results (attack blocked)
        when(resultSet.first()).thenReturn(false);

        // Act: Attempt time-based injection
        AttackResult result = lesson.injectableQuery(timeBasedAttack);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "Time-based blind injection should be blocked");
        
        // Verify: Attack was parameterized (SLEEP won't execute)
        verify(preparedStatement).setString(1, timeBasedAttack);
    }

    @Test
    @DisplayName("Exploit Test: Subquery injection should FAIL")
    void testSubqueryInjectionBlocked() throws Exception {
        // Arrange: Subquery injection attempt
        String subqueryAttack = "Smith' AND (SELECT COUNT(*) FROM user_system_data) > 0--";
        
        // Mock: No results (attack blocked)
        when(resultSet.first()).thenReturn(false);

        // Act: Attempt subquery injection
        AttackResult result = lesson.injectableQuery(subqueryAttack);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "Subquery injection should be blocked");
        
        // Verify: Subquery was parameterized
        verify(preparedStatement).setString(1, subqueryAttack);
    }

    @Test
    @DisplayName("Edge Case Test: Null input should be handled gracefully")
    void testNullInputHandled() throws Exception {
        // Arrange: Null input
        String nullInput = null;

        // Act & Assert: Should not throw exception
        assertDoesNotThrow(() -> {
            lesson.injectableQuery(nullInput);
        }, "Null input should be handled gracefully");
    }

    @Test
    @DisplayName("Exploit Test: Hex-encoded injection should FAIL")
    void testHexEncodedInjectionBlocked() throws Exception {
        // Arrange: Hex-encoded SQL injection attempt
        String hexAttack = "Smith' OR 0x31=0x31--";
        
        // Mock: No results (attack blocked)
        when(resultSet.first()).thenReturn(false);

        // Act: Attempt hex-encoded injection
        AttackResult result = lesson.injectableQuery(hexAttack);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "Hex-encoded injection should be blocked");
        
        // Verify: Hex payload was parameterized
        verify(preparedStatement).setString(1, hexAttack);
    }
}

// Made with Bob
