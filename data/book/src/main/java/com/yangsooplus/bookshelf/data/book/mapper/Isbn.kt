package com.yangsooplus.bookshelf.data.book.mapper

/**
 * 검색 응답의 ISBN을 도서 ID로 사용하기 위해 ISBN13으로 통일한다.
 * Kakao는 ISBN10, ISBN13 또는 두 번호를 공백으로 구분해 제공한다.
 * 같은 책이 검색마다 다른 표기로 내려와도 같은 ID여야 중복 저장을 막고 즐겨찾기 상태를 연결할 수 있다.
 * 예를 들어 "0132350882"와 "0132350882 9780132350884"는 모두 "9780132350884"가 된다.
 *
 * ISBN13은 2007년부터 사용하는 표준이다. 유효한 ISBN10은 모두 ISBN13으로 변환할 수 있지만,
 * 979로 시작하는 ISBN13에는 대응하는 ISBN10이 없으므로 ISBN13으로 통일해야 두 형식을 모두 처리할 수 있다.
 * ISBN은 판·형식별 식별자이므로 같은 제목이어도 개정판이나 전자책은 별개의 도서로 취급한다.
 *
 * ISBN10은 마지막 체크 문자를 제거하고 978을 붙인 뒤 ISBN13 방식으로 체크 숫자를 다시 계산한다.
 * 앞의 12자리에 1과 3을 번갈아 곱한 합으로 (10 - 합 % 10) % 10을 계산한다.
 * ISBN13이 제공되면 검증 후 우선 사용하며, ISBN10만 제공될 때 변환한다.
 * 번호의 형식·체크 숫자가 잘못되면 예외를 전달한다.
 */
internal fun normalizeIsbn13(isbn: String): String {
    val numbers = isbn.trim().split(Regex("\\s+"))
    if (numbers.size !in 1..2) {
        throw IllegalArgumentException("Expected ISBN10, ISBN13, or both")
    }

    if (numbers.any { it.length != 10 && it.length != 13 }) {
        throw IllegalArgumentException("Expected 10 or 13 ISBN characters")
    }

    val isbn13Numbers = numbers.filter { it.length == 13 }
    val normalized = if (isbn13Numbers.isNotEmpty()) {
        isbn13Numbers.map { it.validateIsbn13() }
    } else {
        numbers.map { it.toIsbn13() }
    }.distinct()

    if (normalized.size != 1) {
        throw IllegalArgumentException("Expected a single book identifier")
    }
    return normalized.single()
}

private fun String.toIsbn13(): String {
    val validBody = take(9).all { it in '0'..'9' }
    val validCheckCharacter = last() in '0'..'9' || last() == 'X'
    if (!validBody || !validCheckCharacter) {
        throw IllegalArgumentException("Invalid ISBN10 characters")
    }
    val sum = mapIndexed { index, character ->
        val digit = if (character == 'X') 10 else character.digitToInt()
        digit * (10 - index)
    }.sum()
    if (sum % 11 != 0) {
        throw IllegalArgumentException("Invalid ISBN10 check digit")
    }

    val body = "978" + take(9)
    return body + isbn13CheckDigit(body = body)
}

private fun String.validateIsbn13(): String {
    if (any { it !in '0'..'9' }) {
        throw IllegalArgumentException("ISBN13 must contain only digits")
    }
    if (!startsWith("978") && !startsWith("979")) {
        throw IllegalArgumentException("Invalid ISBN13 prefix")
    }
    if (last().digitToInt() != isbn13CheckDigit(body = take(12))) {
        throw IllegalArgumentException("Invalid ISBN13 check digit")
    }
    return this
}

private fun isbn13CheckDigit(body: String): Int {
    val sum = body.mapIndexed { index, character ->
        character.digitToInt() * if (index % 2 == 0) 1 else 3
    }.sum()
    return (10 - sum % 10) % 10
}
