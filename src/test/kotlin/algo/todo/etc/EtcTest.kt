package algo.todo.etc

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.ZoneId

/**
 * etc 패키지에는 기타 테스트 코드를 작성한다.
 */
class EtcTest {
    @Test
    fun `유효하지 않은 시간대 문자열을 사용하면 ZoneId 객체를 생성하면 예외가 발생한다`() {
        val timeZoneString = "invalid-time-zone"

        shouldThrow<Exception> {
            ZoneId.of(timeZoneString)
        }
    }

    @Test
    fun `유효한 시간대 문자열을 사용하면 ZoneId 객체를 생성할 수 있다`() {
        val timeZoneString = "Asia/Seoul"

        val zoneId = ZoneId.of(timeZoneString)

        zoneId.id shouldBe timeZoneString
    }
}
