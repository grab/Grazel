/*
 * Copyright 2022 Grabtaxi Holdings PTE LTD (GRAB)
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

import com.grab.grazel.di.qualifiers.RootProject
import com.grab.grazel.hybrid.bazelCommand
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.process.ExecOperations

abstract class GenerateBuildifierScriptTask : DefaultTask() {

    @get:OutputFile
    abstract val buildifierScript: RegularFileProperty

    init {
        outputs.upToDateWhen { false } // This task is supposed to run always until we figure out up-to-date checks
    }

    @TaskAction
    fun action() {
        project.bazelCommand(
            "run",
            "@grab_bazel_common//:buildifier",
            "--script_path=${buildifierScript.get().asFile.path}"
        )
    }

    companion object {
        private const val TASK_NAME = "generateBuildifierScript"

        fun register(
            @RootProject project: Project,
            configureAction: GenerateBuildifierScriptTask.() -> Unit = {},
        ) = project.tasks.register<GenerateBuildifierScriptTask>(
            TASK_NAME,
        ) {
            description = "Generates buildifier executable script"
            group = GRAZEL_TASK_GROUP
            buildifierScript.set(project.layout.buildDirectory.file("buildifier"))

            configureAction(this)
        }
    }
}
