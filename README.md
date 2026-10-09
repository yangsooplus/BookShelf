# BookShelf

Kakao 도서 검색과 로컬 즐겨찾기를 위한 Android 프로젝트.

## 빌드

- JDK 17
- Gradle 9.4.1 / AGP 9.2.1 / Kotlin 2.4.0
- compileSdk 37 / minSdk 26 / targetSdk 37
- KSP 2.3.10 / Hilt 2.60.1 / Room 2.8.4

루트 `local.properties`에 Android SDK 경로와 Kakao REST API 키를 설정한다. 이 파일은 Git에서 제외한다.

```properties
sdk.dir=/path/to/Android/sdk
KAKAO_REST_API_KEY=your-rest-api-key
```

```sh
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## 모듈 구조

| 모듈 | 책임 |
| --- | --- |
| `:app` | 앱 진입점, 최상위 내비게이션, Hilt 조립 |
| `:feature:book` | 도서 화면, ViewModel, MVI 상태 |
| `:domain:book` | 순수 Kotlin 모델, Repository 계약, UseCase |
| `:data:book` | Repository 구현, Kakao API, 데이터 변환 |
| `:data:datasource:network` | 공통 HTTP 클라이언트와 통신 설정 |
| `:data:datasource:database` | Room DB, Entity·DAO, 마이그레이션 |
| `:core:mvi` | 공통 MVI 기반 계층 |
| `:core:designsystem` | 테마와 공통 UI 컴포넌트 |

```text
app → feature:book, data:book, core:designsystem
feature:book → domain:book, core:mvi, core:designsystem
data:book → domain:book, data:datasource:network, data:datasource:database
```

feature는 외부 진입점만 공개하고 내부 구현은 `internal`로 제한한다. domain은 Android에 의존하지 않으며, 데이터 구현은 app에서 Hilt로 연결한다.

## Convention plugins

공통 빌드 설정은 별도 included build인 `build-logic`에서 관리한다. 의존성 버전은 `gradle/libs.versions.toml`을 공유한다.

| 플러그인 | 역할 |
| --- | --- |
| `bookshelf.android.application` | Android Application 설정 |
| `bookshelf.android.library` | Android Library 설정 |
| `bookshelf.kotlin.library` | Kotlin/JVM 설정 |
| `bookshelf.android.compose` | Compose 설정과 공통 의존성 |
| `bookshelf.android.hilt` | Hilt·KSP 설정 |
| `bookshelf.android.feature` | Library·Compose·Hilt 설정과 MVI·designsystem 의존성 |
