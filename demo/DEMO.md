# Demo: Claude Git Hooks - AI for your workflow

## What is it

Pre-commit code analyzer that mimics static analysis tools like Sonar with a non-deterministic approach.

- **Pre-commit code analysis**: Detects critical issues before they reach the repo
- **Automatic commit messages**: Write "auto" and Claude generates the message
- **PR generation**: Title, description and suggested tests with a single command
- **Smart skip**: Excludes code with SKIP-ANALYSIS comments
- **Parallelization**: Asynchronous file analysis with sub-agents
- **Auto-update**: Keeps itself updated automatically

## Business Value

**Claude Git Hooks leverages a sunk cost (the Claude license you're already paying for) to save costs and extract value:**

- **Reduces time in PR reviews**: Detects issues before code review, saving hours of work in code reviews.
- **Prevents production incidents**: Identifies vulnerabilities, race conditions and critical errors before merge. A bug in production is far more expensive than one in development.
- **Maintains consistent code quality**: Applies standards automatically, reducing technical debt accumulation and its associated rework costs.

---

## Case 1: Endless list of changes in a giant commit

```bash
# You write the commit quickly
git commit -m "auto"

# Claude analyzes your changes and generates:
# "feat(auth): implement JWT token refresh with Redis caching
#
#  - Add token refresh endpoint with 15min expiry
#  - Integrate Redis for session management
#  - Update middleware to validate refresh tokens"
```
- Claude understands the semantic context of changes, identifies architecture patterns (JWT, Redis) and generates messages following Conventional Commits automatically.
- Commit normalization makes it easier to search for bugs identified "on the fly", with no clear date of origin.
- An eventual audit becomes friendlier with clear commits.

## Case 2: Complex commits that put out one fire but light two

```bash
git commit -m "fix: resolver problema de concurrencia"

# Claude blocks the commit:
#
# QUALITY GATE: FAILED
#
# Critical Issues (2):
# - UserService.java:45 - Race condition in updateBalance() method
# - PaymentController.java:78 - SQL Injection in dynamic query
#
# Generating claude_resolution_prompt.md with solutions...
```
- AST-aware analysis that detects security, concurrency and quality problems.
- Time is essential in these cases, claude-hooks generates a structured prompt that serves as context for Claude to fix the critical issues.

## Case 3: PR with hundreds of changes from a month ago that I don't remember

```bash
claude-hooks analyze-diff develop

# Claude generates:
#
# PR Title: "feat: Add real-time notifications with WebSocket support"
#
# Branch Name: feature/realtime-notifications
#
# Description:
# ## What changed
# - Implemented WebSocket server using Socket.io
# - Added notification queue with Redis
# - Created React hooks for real-time updates
#
# ## Testing checklist
# Unit tests for notification service
# Integration tests for WebSocket connections
# Load test with 1000+ concurrent connections
#
# Breaking Changes: API endpoint /notifications deprecated
```

- Compares branches intelligently, identifies the type of change, suggests specific tests based on modified code and detects breaking changes automatically.
- Prevents PRs where 95% of the information is lost, compromising quick detection of specific changes in the future.
- If the user forgot to create a new branch, recommends a name and gives instructions to create PR.

## Case 4: Hacks that are a necessary evil

```java
// Non-horrible option ---------------------

// SKIP-ANALYSIS_BLOCK
@Deprecated
public void legacyPaymentMethod() {
    // Code that works but is horrible
    System.out.println("Processing payment...");
    Thread.sleep(9999999); // Don't judge me
}
// SKIP-ANALYSIS_BLOCK

// New code that WILL be analyzed
public CompletableFuture<Payment> processPaymentAsync() {
    // Your good code here
}
```

```bash
// Really bad option ---------------------
git commit -m "feat: new stuff" --no-verify
```

- Claude-hooks respects analysis boundaries, allowing pragmatic coexistence between technical debt and good practices without noise in the reports.
- Git respects them even more, allowing you to cancel the entire pre-commit.

---

## Configuration Options

### CLI Flags
- `--force`: Reinstalls hooks even if they already exist
- `--skip-auth`: Skips Claude authentication verification (useful for CI/CD)

### Environment Variables
- `CLAUDE_ANALYSIS_MODE=sonarqube`: Activates SonarQube analysis mode
- `CLAUDE_DEBUG=true`: Enables detailed debug logging
- `CLAUDE_USE_SUBAGENTS=true`: Activates parallel analysis with subagents
- `CLAUDE_SUBAGENT_MODEL=haiku|sonnet|opus`: Model for subagents (haiku: fast, sonnet: balanced, opus: thorough)
- `CLAUDE_SUBAGENT_BATCH_SIZE=3`: Number of files analyzed in parallel per batch

### Customizable Prompt Files
- `.claude/CLAUDE_PRE_COMMIT_SONAR.md`: Evaluation criteria for SonarQube analysis
- `.claude/CLAUDE_ANALYSIS_PROMPT_SONAR.md`: Analysis prompt template
- `.claude/CLAUDE_RESOLUTION_PROMPT.md`: Template for AI resolution prompt generation

---

## AI under the hood

- **Contextual analysis**: Claude doesn't just see syntax, it understands architectural patterns and best practices
- **Project learning**: Adapts to your codebase conventions through `.claude/` files
- **Semantic generation**: Messages and PRs reflect the "why" of the change, not just the "what"
- **Proactive detection**: Identifies code smells, vulnerabilities and anti-patterns before CI/CD
- **Optimized prompt engineering**: Customizable templates that maximize Claude's precision

**Setup in 30 seconds**: `npm install -g claude-git-hooks && claude-hooks install`


# Subagents in Claude Code

## What is a Subagent?

A **subagent** is an autonomous instance of Claude that executes specialized tasks independently. Think of it as delegating work to a focused assistant that:

- Runs in its own context (doesn't pollute your main conversation)
- Has access to specific tools based on its type
- Can run in parallel with other subagents

**Key difference from regular Claude usage:**
- Regular: You interact back-and-forth with Claude in a conversation
- Subagent: You give detailed instructions once, it executes autonomously, returns result

## Available Subagents in Claude Code

| Type | Tools | Use Cases | Speed/Cost |
|------|-------|-----------|------------|
| **general-purpose** | All (Read, Write, Edit, Bash, Glob, Grep, etc.) | Complex multi-step tasks, modify multiple files, deep research | Slow/Expensive |
| **Explore** | Glob, Grep, Read, Bash | Search files by pattern, keyword search, understand codebase structure | Fast/Cheap |
| **Plan** | Glob, Grep, Read, Bash | Create implementation plans, break down complex features, understand dependencies | Fast/Cheap |

## Subagents in claude-hooks document

### Efficiency Advantages

Using subagents can improve speed and lower cost of code analysis in these scenarios:

#### **Parallel analysis of multiple files**

**Problem:** Analysing N files sequentially has N times the impact on analysis time.

**Solution:** Use general-purpose subagents in parallel for deep analysis. Results from testing with 4 controller files:

| Metric | BATCH_SIZE=1 (Sequential) | BATCH_SIZE=4 (Parallel) | Improvement |
|--------|---------------------------|-------------------------|-------------|
| **Total Time** | 367.9s (~6.1 min) | 150.4s (~2.5 min) | **2.4x faster** |
| **Quality Gate** | FAILED | FAILED | Consistent |
| **Total Issues** | 46 | 53 | High consistency |
| **Blocker** | 12 | 18 | Detects more critical |
| **Critical** | 13 | 15 | High consistency |
| **Reliability** | E | E | Consistent |
| **Security** | E | E | Consistent |
| **Maintainability** | E | E | Consistent |
| **Duplications** | 26% | 30% | High consistency |
| **Complexity** | 20 | 20 | Consistent |

**Detection consistency**: High - Both modes detect the same types of critical issues (SQL injection, hardcoded credentials, race conditions, resource leaks)

**Added value:** 2.4x faster with consistent detection of critical issues.

## When NOT to Use Subagents

Subagents add overhead. Avoid them when:

1. **Single simple task:** Direct `claude < prompt.txt` is faster
2. **Already have full context:** No need to explore
3. **Sequential dependencies:** Results depend on previous steps
4. **Real-time interaction needed:** Subagents are autonomous, can't ask questions

## Best Practices

### 1. Be Specific in Prompts
Subagents don't allow back-and-forth interaction, so prompts must be detailed and complete from the start. Specify expected output format, conventions to follow, and what the subagent should return.

### 2. Use Appropriate Model
- **haiku**: Fast and simple tasks (Explore, searches)
- **sonnet**: Complex tasks with better quality (general-purpose, deep analysis)
- **opus**: Maximum quality when cost is not a limiting factor

### 3. Batch Parallel Work
Limit concurrent subagents to avoid overload. Use `CLAUDE_SUBAGENT_BATCH_SIZE` to process files in controlled batches (default: 3 files at a time).

## Summary

**Subagents = Parallel execution + Specialized tools + Autonomous work**

For `claude-hooks`:
- Use **Explore** for fast context gathering
- Use **general-purpose** for parallel deep analysis
- Keep it simple: only use subagents when clear efficiency gain

**Rule of thumb:** If analysing 3+ files, use subagents. If not, direct invocation is fine.
