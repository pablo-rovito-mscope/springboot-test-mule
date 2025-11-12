# Demo: Claude Git Hooks - AI for your workflow

## What is it

Pre-commit code analyzer that mimics static analysis tools like Sonar with a non-deterministic approach.

- **Pre-commit code analysis**: Detects critical issues before they reach the repo
- **Automatic commit messages**: Write "auto" and Claude generates the message
- **PR generation**: Title, description and suggested tests with a single command
- **Parallelization**: Asynchronous file analysis with sub-agents
- **Auto-update**: Keeps itself updated automatically

## Business Value

**Claude Git Hooks leverages a sunk cost (the Claude license you're already paying for) to save costs and extract value:**

- **Reduces time in PR reviews**: Detects issues before code review, saving hours of work in code reviews.
- **Prevents production incidents**: Identifies vulnerabilities, race conditions and critical errors before merge. A bug in production is far more expensive than one in development.
- **Maintains consistent code quality**: Applies standards automatically, reducing technical debt accumulation and its associated rework costs.

---

## Demo

### Instructions

1. Clone repository and checkout branch `claude-hook-demo`
2. Install claude-hooks: `npm install -g claude-git-hooks && claude-hooks install`
3. Run this prompt in Claude CLI to introduce critical issues:
```
Add critical security and concurrency issues to all Java controllers in src/main/java/com/example/crud/controller/: SQL injection via unsanitized parameters, race conditions in balance updates, hardcoded credentials, and missing input validation. Make issues realistic but detectable.
```
4. Stage files with `git add .` and attempt commit to trigger analysis
5. Test the demo cases below
6. Experiment further using [claude-hooks README](https://github.com/mscope-S-L/git-hooks/blob/DEV/README.md)

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

## Case 2: Complex commits that put out one fire but light two more

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

---

## Configuration Options (v2.2.0+)

### CLI Flags
- `--force`: Reinstalls hooks even if they already exist
- `--skip-auth`: Skips Claude authentication verification (useful for CI/CD)
- `--set-preset <name>`: Sets active preset (backend, frontend, fullstack, database, ai, default)

### Configuration File (`.claude/config.json`)

```json
{
  "preset": "backend",
  "analysis": {
    "maxFileSize": 200000,
    "maxFiles": 30,
    "timeout": 180000
  },
  "commitMessage": {
    "autoKeyword": "auto",
    "timeout": 180000
  },
  "subagents": {
    "enabled": true,
    "model": "haiku",
    "batchSize": 2
  },
  "templates": {
    "baseDir": ".claude",
    "analysis": "CLAUDE_ANALYSIS_PROMPT_SONAR.md",
    "guidelines": "CLAUDE_PRE_COMMIT_SONAR.md",
    "commitMessage": "COMMIT_MESSAGE.md",
    "analyzeDiff": "ANALYZE_DIFF.md",
    "resolution": "CLAUDE_RESOLUTION_PROMPT.md"
  },
  "system": {
    "debug": true
  }
}
```

### Parallel Analysis
- **enabled**: `true` activates parallel execution with multiple Claude CLI processes
- **model**: `haiku` (fast), `sonnet` (balanced), `opus` (thorough)
- **batchSize**: Number of files per batch (1=fastest, higher=fewer API calls)

### Customizable Prompt Files
- `.claude/CLAUDE_PRE_COMMIT_SONAR.md`: Evaluation criteria for SonarQube analysis
- `.claude/CLAUDE_ANALYSIS_PROMPT_SONAR.md`: Analysis prompt template
- `.claude/CLAUDE_RESOLUTION_PROMPT.md`: Template for AI resolution prompt generation
- `.claude/COMMIT_MESSAGE.md`: Commit message generation template
- `.claude/ANALYZE_DIFF.md`: PR analysis template

---

## AI under the hood

- **Contextual analysis**: Claude doesn't just see syntax, it understands architectural patterns and best practices
- **Project learning**: Adapts to your codebase conventions through `.claude/` files
- **Semantic generation**: Messages and PRs reflect the "why" of the change, not just the "what"
- **Proactive detection**: Identifies code smells, vulnerabilities and anti-patterns before CI/CD
- **Optimized prompt engineering**: Customizable templates that maximize Claude's precision

---

## Next steps

- **Improve UX**: UX has been so far the main driver. Use laziness as a creative source.
- **Add boilerplate and clear boundaries**: Evolve into a CLI that puts clear, safe boundaries for architecting, safety and best practices.
- **Customization**: Add/improve templates and specific configs.
- **Make use of new Claude Cli features**: Claude CLI frequently releases tools that can be used for new functions.
- **Explore use of Claude API**: Claude Code and Claude API have more powerful features, such as Tasks.