plugins {
    kotlin("jvm").version("1.4.31")
}

repositories {
    mavenCentral()
}

dependencies {
    kotlin("stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.3")

    testApi("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
