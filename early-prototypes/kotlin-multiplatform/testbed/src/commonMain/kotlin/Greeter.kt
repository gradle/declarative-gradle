import kotlinx.datetime.*

expect fun environment(): String

// This method is for testing the languageVersion property
fun openEndedRange(): OpenEndRange<Int> {
    return 4..<10 // Open-ended ranges added in Kotlin 1.9
}

fun main() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    println("Hello, ${environment()}, it's $today!")
    println("Here's an open-ended range available since Kotlin 1.9: ${openEndedRange()}.")
}
