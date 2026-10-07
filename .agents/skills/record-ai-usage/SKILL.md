---
name: record-ai-usage
description: Create a feature-level AI usage record from the current Codex conversation and available Git evidence when the user finishes a work unit and asks to document it.
---

# Record AI Usage

Create a concise, factual record of how AI contributed to one completed feature or work unit. The user invokes this skill manually at a milestone, so treat a direct request to record the work as authorization to append the entry to `docs/AI_USAGE.md`.

## Establish the work boundary

- Use the feature name and work boundary supplied in the invocation. Examples: `$record-ai-usage 도서 검색, 검색 화면 설계부터 현재까지` or `$record-ai-usage 즐겨찾기 저장`.
- Use the relevant context available in the current Codex conversation, beginning at that boundary. Do not include unrelated work from earlier in the thread.
- If the user provides a feature name but no explicit start point, infer the smallest coherent feature task in the available conversation. If the context does not establish a reliable boundary, ask one concise question before writing.
- Do not imply access to conversation history that is not present in the current context. If compaction or missing history leaves material gaps, mark those details as unknown or ask the user.

## Gather evidence

- Read the relevant conversation turns for goals, AI-assisted work, user decisions, review feedback, and verification results.
- Inspect Git status, relevant diffs, and recent commits when this directory is a Git repository. Use them to corroborate what changed and identify the branch or commit when available.
- Do not treat a code diff as evidence that a test ran. Record a test, build, or manual check only when its execution and result are visible in the conversation or an available command result. If no verification is evidenced, say it was not recorded or remains unverified.
- Do not read or reproduce secrets, API keys, or unrelated private data. Avoid copying long prompts or raw conversation transcripts; summarize the work.
- Distinguish AI-generated suggestions or code from decisions and edits the user made. Do not claim that the user personally changed something unless the context supports it.

## Draft and record

Use the feature name alone as the section title. Do not put a date in the title or add a date field. Use this structure, omitting a field only when it truly does not apply:

```md
## 기능명

- 변경 내용:
- AI 활용:
- 검증 방법 및 결과:
- 직접 판단해 수정한 부분:
- 고려했지만 구현하지 않은 내용:
- 관련 브랜치/커밋:
```

- Write in Korean, concise enough for a teammate to scan later.
- Keep the decision rationale and verification evidence concrete. State what was checked and the observed result; avoid generic claims such as “verified thoroughly.”
- Mark unknowns explicitly instead of filling gaps with plausible details. If the user calls for a draft only, show it without writing the file.
- When asked to record or append, preserve all existing content in `docs/AI_USAGE.md` and append the new section. Create the file with a short top-level heading if it does not exist.
- Before writing, inspect the target file and avoid overwriting prior records. If the same feature heading already exists, append another section with the same feature-only title so the new work unit remains separately traceable.
- After writing, reread the added section and report its path. Do not change `.gitignore` or other project files as part of this task.

## Invocation examples

- `$record-ai-usage 검색 화면 설계부터 현재까지의 도서 검색 작업을 기록해줘`
- `$record-ai-usage 즐겨찾기 저장 기능의 초안만 만들어줘`
