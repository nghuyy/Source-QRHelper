var buildtime = java.text.SimpleDateFormat("hh:mm aa dd/MM/yyyy").format(java.util.Date())
task("Clean") {
    doLast {
        //Clean()
    }
}

task("Develop") {
    doLast {
        //Clean()
    }
}

task("Release") {
    doLast {
        DoBuildAAR()
        DoRelease()
    }
}

task("Upload") {
    doLast {
        DoRelease()
    }
}

fun DoBuildAAR(){
    exec {
        commandLine(
            "gradle.bat", "assembleRelease"
        )
        workingDir = project.projectDir
    }
}

fun DoRelease(){
    val props = java.util.Properties()
    props.load(java.io.FileReader(file("app.properties")))
    val VERSION_CODE = (props.getProperty("build").toInt())
    val VERSION = props.getProperty("version")
    val APPID = props.getProperty("package")
    if(!File("build/outputs/aar/${APPID}-${VERSION}-release.aar").exists()){
       throw kotlin.Exception("Build failed! cant find aar!")
    }else {
        delete("dist")
        exec {
            commandLine = listOf("git", "clone", "git@github.com:nghuyy/qrhelper.git", "dist")
        }
        delete(fileTree("dist") {
            exclude(".git","*.md")
        })
        copy {
            from("build/outputs/aar/${APPID}-${VERSION}-release.aar")
            into("dist/")
        }
        exec {
            workingDir = File("./dist")
            commandLine = listOf("git", "add", ".")
        }
        exec {
            workingDir = File("./dist")
            commandLine = listOf("git", "commit", "-m", "\"${VERSION}(${buildtime})\"")
        }
        exec {
            workingDir = File("./dist")
            commandLine = listOf("git", "tag", "-a", "${VERSION}", "-m", "\"Build: ${VERSION} (${buildtime})\"")
        }
        exec {
            workingDir = File("./dist")
            commandLine = listOf("git", "push", "-f", "origin", "main","--tags")
        }
    }
}