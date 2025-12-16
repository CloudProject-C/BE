# CampEat
## BE


## Git 규칙 (Issue · Branch · Commit)

### Issue 규칙

> 모든 작업은 **이슈 생성 → 브랜치 생성 → 커밋 → PR** 순으로 진행합니다.

**📌 형식**

```
[타입]: 작업 요약
```

**💡 예시**

```
feat: 프로젝트 기초 세팅
refactor: 엔티티 구조 리팩토링
docs: README 업데이트
```

**🎯 주요 타입**

| 타입       | 설명               |
| -------- | ---------------- |
| feat     | 새로운 기능 추가        |
| fix      | 버그 수정            |
| refactor | 코드 리팩토링          |
| chore    | 빌드, 설정, 의존성 변경 등 |
| docs     | 문서 수정            |
| test     | 테스트 코드 추가/수정     |
| style    | 코드 스타일, 포맷 변경    |

---

### Branch 규칙

> 이슈 번호를 기반으로 브랜치를 생성합니다.

**📌 형식**

```
[타입]/#[이슈번호]-[작업명-소문자-하이픈]
```

**💡 예시**

```
feat/#1-project-setting
refactor/#12-review-service
docs/#15-readme-update
```

---

### Commit 규칙

> 커밋 메시지에는 반드시 **이슈 번호**를 포함시킵니다.

**📌 형식**

```
#[이슈번호] [타입]: 작업 내용
```

**💡 예시**

```
#1 feat: 의존성 추가
#1 feat: BaseEntity 생성
#12 refactor: Service 책임 분리
```

---

### ✅ 정리

| 구분     | 형식                        |
| ------ | ------------------------- |
| Issue  | `feat: 프로젝트 기초 세팅`        |
| Branch | `feat/#1-project-setting` |
| Commit | `#1 feat: 의존성 추가`         |
| PR     | `feat: 프로젝트 기초 세팅 완료`      |

---
