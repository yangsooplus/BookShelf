# core:mvi

화면의 Intent, State, Reducer, Effect를 관리하는 MVI 기반 모듈.

- `Intent`: 화면에서 전달하는 사용자 행동과 이벤트.
- `State`: 화면의 현재 상태. 불변 데이터 클래스로 구현한다.
- `Reducer<S>`: 현재 상태를 다음 상태로 변환하는 순수 함수.
- `Effect`: 내비게이션, 스낵바 등 일회성 UI 이벤트.
- `BaseViewModel<I, S, E, R>`: 상태와 Effect 노출, Reducer 적용, 코루틴 실행과 예외 처리.

## Reducer 정의

화면별 `sealed interface`가 `Reducer<S>`를 상속하고, 각 타입이 `reduce()`를 직접 구현한다.
클래스 이름은 `ShowLoading`, `UpdateBooks`처럼 변경 의도를 드러낸다.

```kotlin
internal data class BookState(val isLoading: Boolean = false) : State

internal sealed interface BookReducer : Reducer<BookState> {
    data object ShowLoading : BookReducer {
        override fun reduce(state: BookState): BookState =
            state.copy(isLoading = true)
    }
}
```
