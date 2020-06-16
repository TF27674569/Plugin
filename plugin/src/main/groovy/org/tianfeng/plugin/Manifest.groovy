package org.tianfeng.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project


public class Manifest implements Plugin<Project> {

    static final String EXTENSION_NAME = 'manifest'

    @Override
    void apply(Project project) {
        println "-------------------------------------------"
        project.extensions.create(EXTENSION_NAME, PkgNameExtension)

        project.afterEvaluate {
            PkgNameExtension extension = project[EXTENSION_NAME]
            String className = extension.className

            println("manifest  {\n   classsName:${className} \n}")

            project.tasks.getByName("preReleaseBuild") {
                it.doFirst {
                    project.android.applicationVariants.all { variant ->
                        variant.outputs.each { output ->
                            output.processManifest.doLast {
                                // 获取AndroidManifest 文件
                                def manifestPath = "${project.getProjectDir().absolutePath}/build/intermediates/merged_manifests/${variant.dirName}/AndroidManifest.xml"
                                def manifestFile = new File(manifestPath)
                                if (!manifestFile.exists()) {
                                    manifestPath = "${project.getProjectDir().absolutePath}/build/intermediates/manifests/full/${variant.dirName}/AndroidManifest.xml"
                                    manifestFile = new File(manifestPath)
                                }
                                println("path:${manifestPath}")
                                // manifest 文件内容
                                def manifestContent = manifestFile.getText('UTF-8')

                                // 包含过滤的文本
                                if (manifestContent.contains(className)) {
                                    StringReader sr = new StringReader(manifestContent)
                                    String line
                                    int startIndex = -1, endIndex = -1
                                    int currentIndex = 0
                                    boolean target = false

                                    while ((line = sr.readLine()) != null) {
                                        if (line.contains("<activity")) {
                                            startIndex = currentIndex
                                        }

                                        if (line.contains(className)) {
                                            target = true
                                        }

                                        if (line.contains("</activity>")) {
                                            if (target) {
                                                endIndex = currentIndex
                                                break
                                            } else {
                                                startIndex = -1
                                            }
                                        }

                                        currentIndex += 1
                                    }

                                    sr.close()

                                    println("start:${startIndex}  end:${endIndex}")

                                    sr = new StringReader(manifestContent)
                                    StringWriter sw = new StringWriter()
                                    currentIndex = 0
                                    while ((line = sr.readLine()) != null) {
                                        if (currentIndex == startIndex) {
                                            sw.write("<!--\n")
                                        }
                                        sw.write(line)
                                        sw.write("\n")
                                        if (currentIndex == endIndex) {
                                            sw.write("-->\n")
                                        }

                                        currentIndex++
                                    }
                                    sr.close()
                                    sw.close()

                                    println("value:${sw.toString()}")

                                    new File(manifestPath).write(sw.toString(), 'UTF-8')
                                }


                                println "-------------------------------------------"
                            }
                        }
                    }
                }
            }
        }
    }
}

