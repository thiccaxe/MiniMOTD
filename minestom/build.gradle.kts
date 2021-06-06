plugins {
  id("minimotd.shadow-platform")
}

dependencies {
  implementation(projects.minimotdCommon)
  compileOnly(libs.minestomApi)
}

tasks {
  shadowJar {
    commonRelocation("io.leangen.geantyref")
  }
}
