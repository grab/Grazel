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

package com.grab.grazel.migrate.android

import com.grab.grazel.util.commonPath
import org.gradle.api.Project
import java.io.File

private const val JAVA_PATTERN = "**/*.java"
private const val KOTLIN_PATTERN = "**/*.kt"
private const val ALL_PATTERN = "**"

enum class SourceSetType(val patterns: Sequence<String>) {
    JAVA(patterns = sequenceOf(JAVA_PATTERN)),
    JAVA_KOTLIN(patterns = sequenceOf(JAVA_PATTERN, KOTLIN_PATTERN)),
    KOTLIN(patterns = sequenceOf(KOTLIN_PATTERN)),
    RESOURCES(patterns = sequenceOf(ALL_PATTERN)),
    RESOURCES_CUSTOM(patterns = sequenceOf(ALL_PATTERN)),
    ASSETS(patterns = sequenceOf(ALL_PATTERN))
}

/**
 * Given a list of directories specified by `dirs` and list of file patterns specified by `patterns` will return
 * list of `dir/pattern` where `dir`s has at least one file matching the pattern.
 */
internal fun Project.filterSourceSetPaths(
    dirs: Sequence<File>,
    patterns: Sequence<String>
): Sequence<String> = dirs.filter(File::exists)
    .map(::relativePath)
    .flatMap { dir ->
        patterns.flatMap { pattern ->
            val matchedFiles = fileTree(dir).matching { include(pattern) }.files
            when {
                matchedFiles.isEmpty() -> sequenceOf()
                else -> {
                    val commonPath = commonPath(*matchedFiles.map { it.path }.toTypedArray())
                    val relativePath = relativePath(commonPath)
                    if (matchedFiles.size == 1) {
                        sequenceOf(relativePath)
                    } else {
                        sequenceOf("$relativePath/$pattern")
                    }
                }
            }
        }
    }.distinct()