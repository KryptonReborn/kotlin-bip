plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.androidGradlePlugin)
    compileOnly(libs.kotlinGradlePlugin)
}

gradlePlugin {
    plugins {
        register("commonMppLib") {
            id = "common.mpp.lib"
            implementationClass = "plugins.CommonMppLibPlugin"
        }
        register("commonMppPublish") {
            id = "common.mpp.publish"
            implementationClass = "plugins.CommonMppPublish"
        }
    }
}
