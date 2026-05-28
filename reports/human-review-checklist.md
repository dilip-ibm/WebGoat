# Human Review Checklist for Security Fixes

This checklist must be completed for all vulnerabilities requiring human review or marked as "do not auto-fix" before deployment.

---

## CUSTOM-SQL-006 — UserService.java
**Priority:** 2 | **Risk:** HIGH | **Approval:** needs_human_review

Estimated review time: **4 hours**

- [ ] Confirmed the fix does not break the files listed in impact-analysis.json:
  - [ ] target-repo/src/main/java/org/owasp/webgoat/container/WebSecurityConfig.java
  - [ ] target-repo/src/main/java/org/owasp/webgoat/container/users/RegistrationController.java
  - [ ] target-repo/src/test/java/org/owasp/webgoat/container/users/UserServiceTest.java
  - [ ] target-repo/src/main/java/org/owasp/webgoat/container/lessons/LessonConnectionInvocationHandler.java
- [ ] A second engineer has reviewed the code change
- [ ] Tested in a staging environment with both valid and malicious usernames
- [ ] Compliance team has been notified (OWASP Top 10 A03:2021, PCI-DSS 6.5.1, HIPAA 164.312(a)(1))
- [ ] Rollback steps have been documented
- [ ] User registration flow tested end-to-end
- [ ] Verified username validation doesn't break existing user accounts
- [ ] Integration tests pass for user registration and lesson initialization

**Additional Notes:**
- This affects core authentication and user management
- SQL injection in schema creation could allow privilege escalation
- Test with usernames containing: quotes, semicolons, dashes, SQL keywords

---

## CUSTOM-SQL-005 — JWTHeaderKIDEndpoint.java
**Priority:** 3 | **Risk:** HIGH | **Approval:** needs_human_review

Estimated review time: **3 hours**

- [ ] Confirmed the fix does not break the files listed in impact-analysis.json:
  - [ ] target-repo/src/test/java/org/owasp/webgoat/lessons/jwt/claimmisuse/JWTHeaderKIDEndpointTest.java
- [ ] A second engineer has reviewed the code change
- [ ] Tested in a staging environment with malicious JWT tokens
- [ ] Compliance team has been notified (OWASP Top 10 A03:2021, CWE-89, PCI-DSS 6.5.1)
- [ ] Rollback steps have been documented
- [ ] JWT authentication flow tested with valid tokens
- [ ] Verified fix prevents SQL injection in 'kid' header parameter
- [ ] Test cases updated to verify both valid and malicious inputs

**Additional Notes:**
- This affects JWT token validation and authentication
- SQL injection could allow token forgery and authentication bypass
- Test with 'kid' values containing: SQL injection payloads, UNION attacks, boolean-based injections

---

## CUSTOM-SQL-002 — SqlInjectionLesson8.java
**Priority:** 6 | **Risk:** MEDIUM | **Approval:** needs_human_review

Estimated review time: **2 hours**

- [ ] Confirmed the fix does not break the files listed in impact-analysis.json:
  - [ ] target-repo/src/main/java/org/owasp/webgoat/lessons/sqlinjection/introduction/SqlInjectionLesson9.java
- [ ] A second engineer has reviewed the code change
- [ ] Tested in a staging environment
- [ ] Compliance team has been notified (OWASP Top 10 A03:2021, PCI-DSS 6.5.1)
- [ ] Rollback steps have been documented
- [ ] Verified generateTable() method still works correctly in SqlInjectionLesson9
- [ ] Lesson objectives still achievable after fix (if educational vulnerability)
- [ ] Employee data queries tested with injection payloads

**Additional Notes:**
- This is a lesson file - verify educational goals are maintained
- SqlInjectionLesson9 depends on generateTable() method
- Test with SQL injection in both 'name' and 'auth_tan' parameters

---

## CUSTOM-XSS-002 — StoredXssComments.java
**Priority:** 9 | **Risk:** HIGH | **Approval:** needs_human_review

Estimated review time: **3 hours**

- [ ] Confirmed the fix does not break the files listed in impact-analysis.json:
  - [ ] target-repo/src/test/java/org/owasp/webgoat/lessons/xss/StoredXssCommentsTest.java
  - [ ] target-repo/src/main/java/org/owasp/webgoat/lessons/xss/stored/StoredCrossSiteScriptingVerifier.java
- [ ] A second engineer has reviewed the code change
- [ ] Tested in a staging environment with XSS payloads
- [ ] Compliance team has been notified (OWASP Top 10 A03:2021, PCI-DSS 6.5.7, GDPR Article 32)
- [ ] Rollback steps have been documented
- [ ] Verified HTML encoding doesn't break legitimate comment content
- [ ] StoredCrossSiteScriptingVerifier updated to match new behavior
- [ ] All stored comments display correctly after sanitization

**Additional Notes:**
- Stored XSS affects all users viewing comments
- Could lead to session hijacking and account takeover
- Test with: `<script>` tags, event handlers, encoded payloads, polyglot XSS
- Verify legitimate HTML entities in comments are handled properly

---

## CUSTOM-XSS-001 — CrossSiteScriptingLesson5a.java
**Priority:** 10 | **Risk:** HIGH | **Approval:** needs_human_review

Estimated review time: **2 hours**

- [ ] Confirmed the fix does not break the files listed in impact-analysis.json:
  - [ ] target-repo/src/it/java/org/owasp/webgoat/integration/XSSIntegrationTest.java
- [ ] A second engineer has reviewed the code change
- [ ] Tested in a staging environment
- [ ] Compliance team has been notified (OWASP Top 10 A03:2021, PCI-DSS 6.5.7, SOC 2 CC6.1)
- [ ] Rollback steps have been documented
- [ ] XSSIntegrationTest updated to verify sanitization instead of XSS execution
- [ ] Payment flow tested with malicious input in field1 parameter
- [ ] Lesson can still demonstrate XSS concepts if that's the educational goal

**Additional Notes:**
- Reflected XSS in payment context - high risk for credential theft
- Could be used for phishing attacks targeting credit card information
- Test with: `<script>alert('XSS')</script>`, event handlers, encoded payloads
- Verify credit card display still works correctly after encoding

---

## CVE-2021-29505 — pom.xml (XStream)
**Priority:** 11 | **Risk:** HIGH | **Approval:** do_not_auto_fix

Estimated review time: **6 hours**

- [ ] Confirmed the fix does not break the files listed in impact-analysis.json:
  - [ ] target-repo/src/main/java/org/owasp/webgoat/lessons/vulnerablecomponents/VulnerableComponentsLesson.java
  - [ ] target-repo/src/test/java/org/owasp/webgoat/lessons/vulnerablecomponents/VulnerableComponentsLessonTest.java
- [ ] A second engineer has reviewed the code change
- [ ] Tested in a staging environment
- [ ] Compliance team has been notified (OWASP Top 10 A08:2021, CWE-502, PCI-DSS 6.2)
- [ ] Rollback steps have been documented
- [ ] VulnerableComponentsLesson still demonstrates deserialization vulnerability
- [ ] Alternative approach documented if lesson breaks (use different vulnerable component or add XStream security config)
- [ ] VulnerableComponentsLessonTest passes with updated version

**Additional Notes:**
- **INTENTIONALLY VULNERABLE** - This is for educational purposes
- Consider: Keep vulnerable version but isolate it, or update lesson to use different vulnerable component
- If upgrading: Document how lesson will demonstrate deserialization attacks with newer XStream
- Security team approval required before any changes to this educational vulnerability
- Consider adding runtime security configuration to XStream instead of version upgrade

---

## Review Sign-off

**Reviewer Name:** ___________________________

**Date:** ___________________________

**Signature:** ___________________________

**Security Team Approval:** ___________________________

**Deployment Authorization:** ___________________________

---

## Notes for Reviewers

1. **Testing Requirements:**
   - All fixes must be tested in a staging environment that mirrors production
   - Both positive (legitimate use) and negative (attack) test cases required
   - Integration tests must pass before deployment

2. **Rollback Plan:**
   - Document exact steps to revert changes
   - Keep previous version tagged in version control
   - Have database backup if schema changes are involved

3. **Compliance Notification:**
   - Email compliance team with CVE details and fix timeline
   - Include affected regulations in notification
   - Document compliance team acknowledgment

4. **Educational Context:**
   - WebGoat is a training application - some vulnerabilities may be intentional
   - Verify with product owner before fixing lesson-related vulnerabilities
   - Ensure fixes don't break the educational value of the application

5. **Deployment Window:**
   - Schedule fixes during low-traffic periods
   - Have monitoring in place to detect issues
   - Plan for immediate rollback if problems occur