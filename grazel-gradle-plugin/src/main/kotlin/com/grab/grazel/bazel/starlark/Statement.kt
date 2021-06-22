/*
 * Copyright 2021 Grabtaxi Holdings Pte Ltd (GRAB), All rights reserved.
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 */

package com.grab.grazel.bazel.starlark

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

internal const val INDENT = 2

interface Statement {

    fun write(level: Int, writer: PrintWriter)

    fun indent(
        level: Int,
        writer: PrintWriter
    ): PrintWriter {
        writer.print(" ".repeat(level * INDENT))
        return writer
    }
}

interface IsEmpty {
    fun isEmpty(): Boolean
}

private fun String.isQuoted() = startsWith("\"") && endsWith("\"")

fun Any.quote(): String {
    val stringValue = toString()
    return if (!stringValue.isQuoted()) "\"" + toString() + "\"" else stringValue
}

/**
 * Quotes each [Iterable]'s items with `quote`.
 */
val <T : Any> Collection<T>.quote: Collection<String> get() = map { it.quote() }

class StatementsBuilder : AssignmentBuilder {
    private val mutableStatements = mutableListOf<Statement>()
    val statements get() = mutableStatements.toList()

    fun add(statement: Statement) {
        mutableStatements += statement
        addNewLine()
    }

    fun add(statements: List<Statement>) {
        this.mutableStatements.addAll(statements)
        addNewLine()
    }

    fun add(builder: StatementsBuilder.() -> Unit) {
        add(statements(builder))
    }

    fun newLine() {
        addNewLine()
    }

    private fun addNewLine() {
        mutableStatements += NewLineStatement
    }

    fun add(statement: String) {
        add(StringStatement(statement))
    }

    override fun String.eq(value: String) {
        val key = this
        add(assignments { key eq value })
    }

    override fun String.eq(assignee: Assignee) {
        val key = this
        add(assignments { key eq assignee })
    }

    override fun String.eq(strings: List<String>) {
        val key = this
        add(assignments { key eq strings })
    }
}

fun statements(builder: StatementsBuilder.() -> Unit): List<Statement> {
    return StatementsBuilder().apply(builder).statements
}

fun List<Statement>.asString(): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    forEach { it.write(0, pw) }
    return sw.toString()
}

fun Statement.asString(): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    write(0, pw)
    return sw.toString()
}

fun List<Statement>.writeToFile(file: File) {
    PrintWriter(file).use { printWriter ->
        forEach { statement -> statement.write(0, printWriter) }
        printWriter.flush()
    }
}