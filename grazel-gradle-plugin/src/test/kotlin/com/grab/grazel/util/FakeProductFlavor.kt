/*
 * Copyright 2021 Grabtaxi Holdings Pte Ltd (GRAB), All rights reserved.
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 */

package com.grab.grazel.util

import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.ApiVersion
import com.android.builder.model.ClassField
import com.android.builder.model.ProductFlavor
import com.android.builder.model.SigningConfig
import com.android.builder.model.VectorDrawablesOptions
import com.grab.grazel.configuration.VariantFilter
import com.grab.grazel.gradle.AndroidBuildVariantDataSource
import org.gradle.api.Action
import org.gradle.api.Project
import java.io.File

class FakeAndroidBuildVariantDataSource(
    var ignoreFlavorsName: List<String> = emptyList(),
    var ignoreVariantName: List<Pair<String, String?>> = emptyList(),
    override val variantFilter: Action<VariantFilter>? = null
) : AndroidBuildVariantDataSource {
    override fun getIgnoredFlavors(project: Project): List<ProductFlavor> =
        ignoreFlavorsName.map { FakeProductFlavor(it) }

    override fun getIgnoredVariants(project: Project): List<BaseVariant> =
        ignoreVariantName.map { FakeVariant(it.first, it.second) }

    override fun getMigratableVariants(project: Project): List<BaseVariant> {
        return emptyList()
    }
}

class FakeProductFlavor(private val name: String) : ProductFlavor {
    override fun getName(): String = name

    override fun getTestApplicationId(): String {
        TODO("Not yet implemented")
    }

    override fun getMultiDexEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getManifestPlaceholders(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun getTestProguardFiles(): MutableCollection<File> {
        TODO("Not yet implemented")
    }

    override fun getMultiDexKeepProguard(): File {
        TODO("Not yet implemented")
    }

    override fun getRenderscriptTargetApi(): Int {
        TODO("Not yet implemented")
    }

    override fun getWearAppUnbundled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getTestHandleProfiling(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getVersionName(): String {
        TODO("Not yet implemented")
    }

    override fun getSigningConfig(): SigningConfig {
        TODO("Not yet implemented")
    }

    override fun getApplicationId(): String {
        TODO("Not yet implemented")
    }

    override fun getMinSdkVersion(): ApiVersion {
        TODO("Not yet implemented")
    }

    override fun getVersionNameSuffix(): String {
        TODO("Not yet implemented")
    }

    override fun getTestInstrumentationRunner(): String {
        TODO("Not yet implemented")
    }

    override fun getApplicationIdSuffix(): String {
        TODO("Not yet implemented")
    }

    override fun getRenderscriptSupportModeBlasEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMaxSdkVersion(): Int {
        TODO("Not yet implemented")
    }

    override fun getDimension(): String {
        TODO("Not yet implemented")
    }

    override fun getBuildConfigFields(): MutableMap<String, ClassField> {
        TODO("Not yet implemented")
    }

    override fun getConsumerProguardFiles(): MutableCollection<File> {
        TODO("Not yet implemented")
    }

    override fun getMultiDexKeepFile(): File {
        TODO("Not yet implemented")
    }

    override fun getProguardFiles(): MutableCollection<File> {
        TODO("Not yet implemented")
    }

    override fun getResourceConfigurations(): MutableCollection<String> {
        TODO("Not yet implemented")
    }

    override fun getResValues(): MutableMap<String, ClassField> {
        TODO("Not yet implemented")
    }

    override fun getTargetSdkVersion(): ApiVersion {
        TODO("Not yet implemented")
    }

    override fun getRenderscriptNdkModeEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getVersionCode(): Int {
        TODO("Not yet implemented")
    }

    override fun getRenderscriptSupportModeEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getTestFunctionalTest(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getVectorDrawables(): VectorDrawablesOptions {
        TODO("Not yet implemented")
    }

    override fun getTestInstrumentationRunnerArguments(): MutableMap<String, String> {
        TODO("Not yet implemented")
    }
}