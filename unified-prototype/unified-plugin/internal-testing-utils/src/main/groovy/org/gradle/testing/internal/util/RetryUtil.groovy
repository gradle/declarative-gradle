package org.gradle.testing.internal.util

final class RetryUtil {
    private RetryUtil() {}

    static int retry(int retries = 3, int waitMsBetweenRetries = 0, Closure closure) {
        int retryCount = 0
        Throwable lastException = null

        while (retryCount++ < retries) {
            try {
                closure.call()
                return retryCount
            } catch (Throwable e) {
                lastException = e
                Thread.sleep(waitMsBetweenRetries)
            }
        }

        // Retry count exceeded, throwing last exception
        throw lastException
    }
}