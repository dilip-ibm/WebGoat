// DevSheriff auto-generated regression test for CUSTOM-SQL-001
// Tests for SQL injection fix in SqlInjectionLesson5a.java

package org.owasp.webgoat.lessons.sqlinjection.introduction;

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

class SqlInjectionLesson5aSecurityTest {

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

    private SqlInjectionLesson5a lesson;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        lesson = new SqlInjectionLesson5a(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(3);
        when(metaData.getColumnName(anyInt())).thenReturn("column");
    }

    @Test
    @DisplayName("Exploit Test: SQL injection with OR condition should FAIL")
    void testSqlInjectionOrAttackBlocked() throws Exception {
        // Arrange: Classic SQL injection payload
        String maliciousInput = "Smith' OR '1'='1";
        
        // Mock: No results (attack blocked)
        when(resultSet.first()).thenReturn(false);

        // Act: Attempt SQL injection
        AttackResult result = lesson.injectableQuery(maliciousInput);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "SQL injection OR attack should be blocked");
        
        // Verify: PreparedStatement was used with parameter
        verify(preparedStatement).setString(1, maliciousInput);
        verify(preparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Normal Use Test: Legitimate last name query should work")
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
    @DisplayName("Edge Case Test: Last name with apostrophe should be handled")
    void testApostropheInNameHandled() throws Exception {
        // Arrange: Name with apostrophe (common in names like O'Brien)
        String nameWithApostrophe = "O'Brien";
        
        // Mock: Valid results
        when(resultSet.first()).thenReturn(true);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString(anyInt())).thenReturn("Patrick", "O'Brien", "patrick@example.com");
        when(resultSet.getRow()).thenReturn(1);

        // Act: Query with apostrophe
        AttackResult result = lesson.injectableQuery(nameWithApostrophe);

        // Assert: Should work correctly
        assertTrue(result.isSuccess(), "Names with apostrophes should work");
        
        // Verify: Apostrophe was safely parameterized
        verify(preparedStatement).setString(1, nameWithApostrophe);
    }

    @Test
    @DisplayName("Exploit Test: UNION SELECT attack should FAIL")
    void testUnionSelectAttackBlocked() throws Exception {
        // Arrange: UNION-based SQL injection
        String unionAttack = "Smith' UNION SELECT userid, user_name, password FROM user_system_data--";
        
        // Mock: No results (attack blocked)
        when(resultSet.first()).thenReturn(false);

        // Act: Attempt UNION injection
        AttackResult result = lesson.injectableQuery(unionAttack);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "UNION SELECT attack should be blocked");
        
        // Verify: Attack payload was parameterized
        verify(preparedStatement).setString(1, unionAttack);
    }

    @Test
    @DisplayName("Edge Case Test: Empty string should be handled")
    void testEmptyStringHandled() throws Exception {
        // Arrange: Empty last name
        String emptyName = "";
        
        // Mock: No results
        when(resultSet.first()).thenReturn(false);

        // Act: Query with empty string
        AttackResult result = lesson.injectableQuery(emptyName);

        // Assert: Should fail gracefully
        assertFalse(result.isSuccess(), "Empty string should return no results");
        
        // Verify: Empty string was parameterized
        verify(preparedStatement).setString(1, emptyName);
    }

    @Test
    @DisplayName("Exploit Test: Comment-based injection should FAIL")
    void testCommentInjectionBlocked() throws Exception {
        // Arrange: Comment to bypass rest of query
        String commentAttack = "Smith'--";
        
        // Mock: No results (attack blocked)
        when(resultSet.first()).thenReturn(false);

        // Act: Attempt comment injection
        AttackResult result = lesson.injectableQuery(commentAttack);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "Comment-based injection should be blocked");
    }

    @Test
    @DisplayName("Edge Case Test: Very long input should be handled")
    void testLongInputHandled() throws Exception {
        // Arrange: Extremely long input
        String longInput = "A".repeat(1000) + "' OR '1'='1";
        
        // Mock: No results
        when(resultSet.first()).thenReturn(false);

        // Act: Query with long input
        AttackResult result = lesson.injectableQuery(longInput);

        // Assert: Should handle without errors
        assertFalse(result.isSuccess(), "Long input should be handled safely");
        
        // Verify: Long input was parameterized
        verify(preparedStatement).setString(1, longInput);
    }

    @Test
    @DisplayName("Exploit Test: Stacked queries attack should FAIL")
    void testStackedQueriesBlocked() throws Exception {
        // Arrange: Attempt to execute multiple statements
        String stackedQueries = "Smith'; DROP TABLE user_data; --";
        
        // Mock: No results (attack blocked)
        when(resultSet.first()).thenReturn(false);

        // Act: Attempt stacked queries
        AttackResult result = lesson.injectableQuery(stackedQueries);

        // Assert: Attack should fail
        assertFalse(result.isSuccess(), "Stacked queries attack should be blocked");
        
        // Verify: Dangerous payload was safely parameterized
        verify(preparedStatement).setString(1, stackedQueries);
    }
}

// Made with Bob
