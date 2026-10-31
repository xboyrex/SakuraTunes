# Security Policy

## Supported versions

Security fixes are generally prepared against the latest maintained source on the main branch. Older releases may not receive fixes.

## Reporting a vulnerability

Do not publish credentials, private data, proof-of-concept payloads, or exploitable details in a public issue.

Use GitHub's private vulnerability reporting feature for this repository when it is enabled. Otherwise, contact the maintainer at xboyrex496@gmail.com with:
- A concise description and potential impact.
- Affected version, build variant, Android version, and device model where relevant.
- Reproduction steps and a minimal proof of concept.
- Any suggested mitigation.

Allow reasonable time for investigation and a fix before public disclosure. Do not access, modify, or retain data that does not belong to you while investigating.

## Secrets in CI

Never place signing keys, API credentials, personal tokens, or session cookies in source files or workflow logs. Use GitHub Actions secrets and least-privilege workflow permissions.
