plugins {
    id("java-library")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("com.squareup.moshi:moshi:1.15.2")
    implementation("com.jsoniter:jsoniter:0.9.23")
    implementation("com.alibaba:fastjson:1.2.83")
    implementation("com.bluelinelabs:logansquare:1.3.7")
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}