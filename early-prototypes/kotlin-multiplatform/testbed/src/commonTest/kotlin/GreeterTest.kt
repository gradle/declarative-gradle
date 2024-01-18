import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GreeterTest {
    @Test
    fun testGreeting() {
        val range = openEndedRange()
        assertTrue(range.contains(5))
        assertFalse(range.contains(10))
    }
}