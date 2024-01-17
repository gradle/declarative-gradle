import kotlinx.datetime.*

expect fun environment(): String

fun main() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    println("Hello, ${environment()}, it's $today!")
}
