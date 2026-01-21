plugins {
	id("mod-platform")
	id("net.neoforged.moddev")
	id("dev.kikugie.fletching-table") version "0.1.0-alpha.22"
	kotlin("jvm") version "2.2.10"
	id("com.google.devtools.ksp") version "2.2.10-2.0.2"
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") {
			forgeVersionRange = "[${prop("deps.minecraft")}]"
		}
		required("neoforge") {
			forgeVersionRange = "[1,)"
		}
		required("fzzy_config") {
			slug("fzzy-config")
			forgeVersionRange = "[0,)"
		}
	}
}

stonecutter {
	filters.exclude("**/*.accesswidener", "**/*.cfg")
	val dir = eval(current.version, ">1.21.10")
	replacements.string {
		direction = dir
		replace("ValidatedIdentifier", "ValidatedIdentifier")
	}
	replacements.string {
		direction = dir
		replace("ResourceLocation", "Identifier")
	}
	replacements.string {
		direction = dir
		replace("net.minecraft.world.entity.npc.VillagerTrades", "net.minecraft.world.entity.npc.villager.VillagerTrades")
	}
}

fletchingTable {
	mixins.create("main") {
		mixin("default", "${prop("mod.id")}.mixins.json")
	}
}

neoForge {
	version = property("deps.neoforge") as String
	validateAccessTransformers = true

	if (hasProperty("deps.parchment")) parchment {
		val (mc, ver) = (property("deps.parchment") as String).split(':')
		mappingsVersion = ver
		minecraftVersion = mc
	}

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
	maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
	maven("https://maven.fzzyhmstrs.me/") { name = "Fzzy Config" }
	maven("https://maven.terraformersmc.com/" ) { name = "TerraformersMC" }
	maven("https://maven.blamejared.com/") { name = "BlameJared" }
	maven("https://thedarkcolour.github.io/KotlinForForge/") { name = "KotlinForForge" }
	maven("https://maven.shedaniel.me/") { name = "Shedaniel" }
	maven("https://maven.cassian.cc") { name = "Cassian" }
	maven("https://jitpack.io") { name = "Jitpack" }
	exclusiveContent {
		forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
		filter { includeGroup("maven.modrinth") }
	}
}

dependencies {
	implementation( "me.fzzyhmstrs:fzzy_config:${prop("deps.fzzy_config")}+neoforge")
	implementation("com.github.ramixin:mixson-neoforge:${prop("deps.mixson")}")
	jarJar("com.github.ramixin:mixson-neoforge:${prop("deps.mixson")}")

	compileOnly("dev.emi:emi-neoforge:${prop("deps.emi")}")
	compileOnly("mezz.jei:jei-${prop("deps.jei")}")
	compileOnly("me.shedaniel:RoughlyEnoughItems-neoforge:${prop("deps.rei")}")
	if (sc.current.parsed.matches("> 1.21.10"))
		compileOnly("cc.cassian.rrv:reliable-recipe-viewer-neoforge:${prop("deps.rrv")}+${stonecutter.current.version}")
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}
