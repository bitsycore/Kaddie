package com.bitsycore.lib.kaddie

import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaConstructor

class JvmReflectionDependencyProvider : DependencyProvider {

	val dependencies: MutableMap<KClass<*>, Any> = mutableMapOf()

	override fun <T : Any> getDependency(parentDependencies: DiContainer, klass: KClass<T>, vararg extraParam: Any): T? {
		try {
			@Suppress("UNCHECKED_CAST")
			dependencies[klass]?.let { return it as? T }
			buildFromReflection(parentDependencies, klass, extraParam).let { return it }
		} catch (_: Exception) {}
		return null
	}

	private fun <T : Any> buildFromReflection(parentDependencies: DiContainer, klass: KClass<T>, extraParam: Array<out Any>) : T {
		klass.objectInstance?.let { return it }
		val constructor = klass.getConstructorForReflection() ?: error("Cannot find constructor for class: $klass")

		// Shortcut for empty constructor
		if (constructor.parameters.isEmpty()) {
			val instance = constructor.call()
			dependencies[klass] = instance
			return instance
		}

		// Get required dependencies
		val dependenciesMap = constructor.getDependenciesAsMap(parentDependencies, extraParam)

		val instance = if (!klass.isInner || constructor.haveOptionalParam) {
			constructor.callBy(dependenciesMap)
		} else {
			// FIXME Java Inner Class doesn't include kotlin inner class parameter identifier
			// but it seem to still be required for a callBy ... So we need to use the constructor in java directly
			constructor.javaConstructor?.newInstance(*dependenciesMap.values.toTypedArray())
				?: error("Cannot create instance of class: $klass")
		}

		dependencies[klass] = instance

		return instance
	}
}