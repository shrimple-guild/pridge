plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") apply false
}

stonecutter active "1.21.11"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("mod_version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    constants["release"] = property("mod_id") != "pridge"
    dependencies["fapi"] = node.project.property("fapi_version") as String
}
