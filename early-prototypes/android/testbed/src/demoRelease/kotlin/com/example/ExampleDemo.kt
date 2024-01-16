package com.example

// This build succeeds since we declared a retrofit dependency onthe demoRelease target

import retrofit2.Retrofit

class ExampleDemo(example: Example) {
    init {
        Retrofit.Builder()
    }
}