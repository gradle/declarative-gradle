To run (on the JVM):
```shell
  ./gradlew jvmRun -DmainClass=GreeterKt --quiet
```

To publish the project to `build/repo` (Can confirm presence of sources jars in the directory):
```shell
  ./gradlew publishAllPublicationsToTestRepository
```

To run all tests (JVM and Browser-based JS tests):
```shell
  ./gradlew allTests
```
