# Converting Java Tests to Kotlin

## Recommended Approach

The best way to convert 46 Java test files to Kotlin is to use IntelliJ IDEA's built-in converter:

### Steps in IntelliJ IDEA:

1. **Select all Java test files:**
   - Open `src/test/java` in Project view
   - Select all `.java` files (Ctrl+A or Cmd+A)

2. **Convert to Kotlin:**
   - Right-click → "Convert Java File to Kotlin File"
   - Or use: Code → Convert Java File to Kotlin File
   - Or shortcut: Ctrl+Alt+Shift+K (Windows/Linux) or Cmd+Option+Shift+K (Mac)

3. **Review and fix:**
   - IntelliJ will convert all files automatically
   - Review the converted files for any issues
   - Fix any compilation errors (usually minimal)

4. **Delete original Java files:**
   - After verifying Kotlin files work, delete the Java versions

### Manual Conversion Challenges:

Converting 46 files manually would require handling:
- Mock annotations (@Mock) → lateinit var
- Static imports → companion object references  
- ImmutableList.of() → listOf()
- when() → `when`() (backticks for Kotlin keywords)
- Exception handling differences
- Nullable types
- And more...

### Alternative: Gradle Task

You could also create a Gradle task that uses the Kotlin compiler's Java interop,
but IntelliJ's converter is the most reliable and widely used approach.

## Files to Convert (46 total):

$(find src/test/java -name "*.java" -type f | sort)

## Estimated Time:
- IntelliJ auto-convert: 5-10 minutes + review time
- Manual conversion: 4-6 hours

## Recommendation:
Use IntelliJ IDEA's converter for best results and time efficiency.
