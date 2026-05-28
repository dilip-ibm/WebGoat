# DevSheriff Security Test Suite

This directory contains comprehensive security regression tests for all vulnerabilities fixed by DevSheriff in the WebGoat application.

## Overview

The test suite validates that security patches have been successfully applied and that the vulnerabilities can no longer be exploited. Each test file includes:

1. **Exploit Tests** - Verify that attack vectors are now blocked
2. **Normal Use Tests** - Ensure legitimate functionality still works
3. **Edge Case Tests** - Test unusual inputs and boundary conditions

## Test Files

### 1. Assignment5-security-test.java
**CVE:** CUSTOM-SQL-007  
**Vulnerability:** SQL Injection in challenge authentication  
**Tests:** 7 test cases

- ✅ SQL injection attacks blocked (OR, UNION, comment-based)
- ✅ Legitimate login works correctly
- ✅ Special characters handled safely
- ✅ Empty and null credentials handled
- ✅ PreparedStatement parameterization verified

### 2. SqlInjectionLesson5a-security-test.java
**CVE:** CUSTOM-SQL-001  
**Vulnerability:** SQL Injection in user data query  
**Tests:** 8 test cases

- ✅ OR-based SQL injection blocked
- ✅ UNION SELECT attacks blocked
- ✅ Comment-based injection blocked
- ✅ Stacked queries blocked
- ✅ Names with apostrophes work correctly
- ✅ Empty strings handled
- ✅ Long inputs handled safely

### 3. SqlInjectionLesson6a-security-test.java
**CVE:** CUSTOM-SQL-003  
**Vulnerability:** SQL Injection in advanced lesson  
**Tests:** 9 test cases

- ✅ UNION-based injection blocked
- ✅ Boolean-based blind injection blocked
- ✅ Time-based blind injection blocked
- ✅ Subquery injection blocked
- ✅ Hex-encoded injection blocked
- ✅ Unicode characters handled
- ✅ Special characters safely parameterized
- ✅ Null input handled gracefully

### 4. SqlInjectionChallenge-security-test.java
**CVE:** CUSTOM-SQL-004  
**Vulnerability:** SQL Injection in user registration  
**Tests:** 8 test cases

- ✅ SQL injection in username check blocked
- ✅ UNION-based injection blocked
- ✅ Comment-based injection blocked
- ✅ Legitimate registration works
- ✅ Duplicate username detection works
- ✅ SQL keywords as text handled
- ✅ Special characters in all fields handled
- ✅ Empty username handled

### 5. pom-dependency-security-test.java
**CVE:** CVE-2015-6420  
**Vulnerability:** commons-collections RCE  
**Tests:** 8 test cases

- ✅ Vulnerable version 3.2.1 not present
- ✅ Fixed version 3.2.2+ present
- ✅ DevSheriff fix comment present
- ✅ pom.xml is valid XML
- ✅ No other vulnerable versions present
- ✅ Dependency still declared
- ✅ Version format valid
- ✅ CVE-2015-6420 mitigated

## Running the Tests

### Quick Start

```bash
# Make the script executable
chmod +x tests/run-all-tests.sh

# Run all security tests
./tests/run-all-tests.sh
```

### Manual Execution

```bash
# Navigate to target-repo
cd target-repo

# Run all security tests
mvn test -Dtest="*SecurityTest"

# Run with coverage
mvn test -Dtest="*SecurityTest" -Pcoverage

# Run specific test
mvn test -Dtest="Assignment5SecurityTest"
```

### Using Maven from project root

```bash
# Run tests
mvn -f target-repo/pom.xml test -Dtest="*SecurityTest"
```

## Test Results

The test runner provides:

- ✅ Pass/Fail status for each test
- 📊 Test statistics (total, passed, failed)
- 📈 Code coverage metrics
- 🔒 List of vulnerabilities tested
- 📝 Links to detailed reports

### Expected Output

```
==========================================
DevSheriff Security Test Suite
==========================================

📋 Test Configuration:
   Project: WebGoat Security Patches
   Test Directory: tests/
   Target Directory: target-repo/

🧪 Running Security Tests...
==========================================

[TEST] Assignment5SecurityTest
  ✓ testSqlInjectionAttackBlocked
  ✓ testLegitimateLoginWorks
  ✓ testSpecialCharactersHandled
  ...

==========================================
📊 Test Results Summary
==========================================
✅ All security tests PASSED

Test Statistics:
   Total Tests: 40
   Passed: 40
   Failed: 0

==========================================
📈 Code Coverage Analysis
==========================================
✓ Coverage report generated
   Instruction Coverage: 85.3%

==========================================
🔒 Security Vulnerabilities Tested
==========================================

✓ CVE-2015-6420: commons-collections RCE
✓ CUSTOM-SQL-007: SQL Injection in Assignment5
✓ CUSTOM-SQL-001: SQL Injection in SqlInjectionLesson5a
✓ CUSTOM-SQL-003: SQL Injection in SqlInjectionLesson6a
✓ CUSTOM-SQL-004: SQL Injection in SqlInjectionChallenge
```

## Test Coverage

### Vulnerabilities Covered

| CVE ID | Type | Status | Tests |
|--------|------|--------|-------|
| CVE-2015-6420 | Dependency | ✅ Fixed | 8 |
| CUSTOM-SQL-007 | SQL Injection | ✅ Fixed | 7 |
| CUSTOM-SQL-001 | SQL Injection | ✅ Fixed | 8 |
| CUSTOM-SQL-003 | SQL Injection | ✅ Fixed | 9 |
| CUSTOM-SQL-004 | SQL Injection | ✅ Fixed | 8 |

**Total:** 5 vulnerabilities, 40 test cases

### Attack Vectors Tested

- ✅ OR-based SQL injection
- ✅ UNION-based SQL injection
- ✅ Comment-based SQL injection
- ✅ Stacked queries
- ✅ Boolean-based blind injection
- ✅ Time-based blind injection
- ✅ Subquery injection
- ✅ Hex-encoded injection
- ✅ Dependency version vulnerabilities

## Continuous Integration

### GitHub Actions Example

```yaml
name: Security Tests

on: [push, pull_request]

jobs:
  security-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run Security Tests
        run: ./tests/run-all-tests.sh
      - name: Upload Coverage
        uses: codecov/codecov-action@v2
        with:
          files: ./target-repo/target/site/jacoco/jacoco.xml
```

## Troubleshooting

### Tests Not Found

If tests are not discovered:
```bash
# Ensure test files are in correct package structure
ls -la target-repo/src/test/java/org/owasp/webgoat/
```

### Maven Build Fails

```bash
# Clean and rebuild
cd target-repo
mvn clean install -DskipTests
mvn test -Dtest="*SecurityTest"
```

### Coverage Report Not Generated

```bash
# Run with coverage profile
mvn test -Pcoverage -Dtest="*SecurityTest"
```

## Contributing

When adding new security fixes:

1. Create a new test file: `tests/[ClassName]-security-test.java`
2. Include exploit, normal use, and edge case tests
3. Add DevSheriff comment header with CVE ID
4. Update this README with test details
5. Run `./tests/run-all-tests.sh` to verify

## Test Maintenance

- Tests should be run before every deployment
- Update tests when security patches are modified
- Add new tests for newly discovered vulnerabilities
- Keep test data realistic but safe

## References

- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [CVE-2015-6420 Details](https://nvd.nist.gov/vuln/detail/CVE-2015-6420)

## License

These tests are part of the DevSheriff security audit project.