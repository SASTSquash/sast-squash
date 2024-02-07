# Generate CodeQL Sarif Results

Example usage:

```bash
codeql database create java-database --overwrite --language=java
```

```bash
codeql database analyze java-database --format='sarifv2.1.0' --output='./out.sarif' codeql/java-queries:Security/CWE/CWE-209/StackTraceExposure.ql
```
