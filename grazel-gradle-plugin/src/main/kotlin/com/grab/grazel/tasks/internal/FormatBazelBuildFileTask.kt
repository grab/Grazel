/*
 * Copyright 2021 Grabtaxi Holdings PTE LTD (GRAB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grab.grazel.tasks.internal

import com.grab.grazel.util.BUILD_BAZEL
import com.grab.grazel.util.WORKSPACE
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.process.ExecOperations
import java.io.File

private const val FORMAT_BAZEL_FILE_TASK = "formatBazelScripts"
private const val FORMAT_BUILD_BAZEL_FILE_TASK = "formatBuildBazel"
private const val FORMAT_WORK_SPACE_FILE_TASK = "formatWorkSpace"

abstract class FormatBazelFileTask : DefaultTask() {

    @get:OutputFile
    var bazelFile: File = File(project.projectDir, BUILD_BAZEL)

    @get:InputFile
    abstract val buildifierScript: RegularFileProperty

    init {
        outputs.upToDateWhen { false } // This task is supposed to run always until we figure out up-to-date checks
    }

    private val execOperations: ExecOperations = project.serviceOf()

    @TaskAction
    fun action() {
        if (bazelFile.exists()) {
            execOperations.exec {
                commandLine = listOf(
                    "sh",
                    "-c",
                    buildifierScript.get().asFile.absolutePath,
                    bazelFile.absolutePath
                )
            }
        }
    }

    companion object {
        private const val TASK_DESCRIPTION = "Format Bazel build files"

        private fun Project.register(
            taskName: String,
            configureAction: FormatBazelFileTask.() -> Unit
        ): TaskProvider<out Task> {
            return tasks.register<FormatBazelFileTask>(name = taskName).apply {
                configure {
                    group = GRAZEL_TASK_GROUP
                    configureAction(this)
                }
            }
        }

        /**
         * Register formatting task on the given project and provide callbacks to configure it.
         *
         * Based on the project type i.e whether root or subproject, register the correct formatting task and provide
         * callbacks to configure it.
         *
         * @param project The project instance to register for
         * @param configureAction Callback to configure the registered task. Can be called multiple times for all registered
         *                        tasks
         * @return The created provider for this task.
         */
        fun register(
            project: Project,
            buildifierScript: Provider<RegularFile>,
            configureAction: FormatBazelFileTask.() -> Unit
        ): TaskProvider<out Task> {
            val rootProject = project.rootProject
            if (project == rootProject) {
                // Format work space
                val formatWorkspace = rootProject.register(FORMAT_WORK_SPACE_FILE_TASK) {
                    bazelFile = File(rootProject.projectDir, WORKSPACE)
                    description = "Format $WORKSPACE file"

                    configureAction(this)
                    this.buildifierScript.set(buildifierScript)
                }
                // Format build.bazel
                val formatBuildBazel = rootProject.register(FORMAT_BUILD_BAZEL_FILE_TASK) {
                    description = "Format $BUILD_BAZEL file"

                    configureAction(this)
                    this.buildifierScript.set(buildifierScript)
                }
                // Aggregating task to depend on above
                return rootProject.register(FORMAT_BAZEL_FILE_TASK) {
                    description = TASK_DESCRIPTION
                    dependsOn(formatWorkspace, formatBuildBazel)
                    this.buildifierScript.set(buildifierScript)
                }
            } else {
                return project.register(FORMAT_BAZEL_FILE_TASK) {
                    description = TASK_DESCRIPTION
                    configureAction(this)
                    this.buildifierScript.set(buildifierScript)
                }
            }
        }
    }
}