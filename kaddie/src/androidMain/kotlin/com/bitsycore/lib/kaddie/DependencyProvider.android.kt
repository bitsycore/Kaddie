package com.bitsycore.lib.kaddie

actual fun getDefaultDependencyProviders(): List<DependencyProvider> = listOf(
	JvmReflectionDependencyProvider()
)
