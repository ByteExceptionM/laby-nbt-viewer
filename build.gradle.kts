plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")

group = "io.masel"
version = providers.environmentVariable("VERSION").getOrElse("2.5.0")

labyMod {
    defaultPackageName = "io.masel.nbtviewer"

    addonInfo {
        namespace = "nbt-viewer"
        displayName = "NBT Viewer"
        author = "ByteException_"
        description =
            "LabyMod addon to view item nbt data in minecraft. Enable Advanced Tooltips (F3+H), hover over an item and press SHIFT. Magic starts..."
        minecraftVersion = "1.17.1<1.21.10"
        version = rootProject.version.toString()
    }

    minecraft {
        registerVersion(versions.toTypedArray()) {
            runs {
                getByName("client") {
                    devLogin = true
                }
            }
        }
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")

    group = rootProject.group
    version = rootProject.version
}
