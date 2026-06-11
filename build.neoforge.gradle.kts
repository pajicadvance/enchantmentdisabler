plugins {
	id("mod-platform")
	id("net.neoforged.moddev")
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") {
			forgeVersionRange = "[${prop("deps.minecraft")},)"
		}
		required("neoforge") {
			forgeVersionRange = "[1,)"
		}
		required("fzzy_config") {
			slug("fzzy-config")
			forgeVersionRange = "[0,)"
		}
		required("mixson") {
			slug("mixson")
			forgeVersionRange = "[0,)"
		}
	}
}

neoForge {
	version = property("deps.neoforge") as String

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "NeoForge Client (${stonecutter.active?.version})"
			programArgument("--username=Dev")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "NeoForge Server (${stonecutter.active?.version})"
		}
	}

	mods {
		register(property("mod.id") as String) {
			sourceSet(sourceSets["main"])
		}
	}
}

repositories {
	mavenCentral()
	strictMaven("https://maven.fzzyhmstrs.me/", "me.fzzyhmstrs") { name = "Fzzy Config" }
	strictMaven("https://thedarkcolour.github.io/KotlinForForge/") { name = "KotlinForForge" }
	strictMaven("https://jitpack.io") { name = "Jitpack" }
	strictMaven("https://maven.blamejared.com/") { name = "BlameJared" }
	strictMaven("https://maven.cassian.cc") { name = "Cassian" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
}

dependencies {
	implementation("me.fzzyhmstrs:fzzy_config:${prop("deps.fzzy_config")}+neoforge")
	implementation("maven.modrinth:mixson:${prop("deps.mixson")}")
	compileOnly("mezz.jei:jei-${prop("deps.jei")}")
	compileOnly("cc.cassian.rrv:reliable-recipe-viewer-neoforge:${prop("deps.rrv")}")
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}
