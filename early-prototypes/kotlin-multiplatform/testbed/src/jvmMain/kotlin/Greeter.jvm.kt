import kotlinx.coroutines.runBlocking

actual fun environment(): String {
    return runBlocking {
        computeEnvironment()
    }
}

suspend fun computeEnvironment(): String {
    return "JVM"
}
