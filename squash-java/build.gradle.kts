plugins {
    id("org.openrewrite.build.language-library")
}

val rewriteVersion = "latest.release"

dependencies {
    compileOnly("org.projectlombok:lombok:latest.release")
    annotationProcessor("org.projectlombok:lombok:latest.release")

    implementation(platform("org.openrewrite:rewrite-bom:$rewriteVersion"))
    implementation("org.openrewrite:rewrite-java")
    implementation("org.openrewrite:rewrite-maven")
    implementation("org.openrewrite.recipe:rewrite-static-analysis:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-yaml")
    implementation("org.openrewrite:rewrite-xml")
    // TODO: Switch back to using release version
    implementation("org.openrewrite.meta:rewrite-analysis:latest.integration")

    runtimeOnly("org.openrewrite:rewrite-java-17")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:latest.release")

    testImplementation("org.openrewrite:rewrite-test:${rewriteVersion}")
    testImplementation("org.openrewrite:rewrite-java-tck:${rewriteVersion}")

    testImplementation("org.assertj:assertj-core:latest.release")
    testImplementation("javax:javaee-api:7.+")

    testRuntimeOnly("org.openrewrite:rewrite-java-17:${rewriteVersion}")

    testRuntimeOnly("javax.servlet:javax.servlet-api:4.0.1") {
        because("CWE-209-StackTraceExposure")
    }
    testRuntimeOnly("org.apache.ant:ant:latest.release") {
        because("RelativePathCommandTest")
    }
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.10.0")
        }
    }
}
