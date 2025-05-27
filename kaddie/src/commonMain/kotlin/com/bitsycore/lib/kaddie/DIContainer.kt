package com.bitsycore.lib.kaddie

import kotlin.reflect.KClass

fun MutableDiContainer() : MutableDiContainer = DiContainerImpl().also {
    val providers = getDefaultDependencyProviders()
    providers.forEach { provider -> it.addProvider(provider) }
}

interface DiContainer {
    fun <T : Any> get(klass: KClass<T>, vararg extraParam: Any = emptyArray()): T
    fun <T : Any> has(klass: KClass<T>): Boolean
    fun getKeys(): Set<KClass<*>>
}

interface MutableDiContainer : DiContainer {
    fun <T : Any> register(klass: KClass<T>, instance: T)
    fun <T : Any> register(klass: KClass<T>, factory: DependencyFactory<T>)
    fun unregisterAll()
    fun unregister(klass: KClass<*>)
    fun addProvider(dependencyProvider: DependencyProvider)
    fun removeProvider(dependencyProvider: DependencyProvider)
}

internal class DiContainerImpl : MutableDiContainer {
    private val dependenciesList = mutableMapOf<KClass<*>, Holder<*>>()
    private val dependencyProviders = mutableListOf<DependencyProvider>()

    override fun <T : Any> register(klass: KClass<T>, instance: T) {
        dependenciesList[klass] = InstanceHolder(instance)
    }

    override fun <T : Any> register(klass: KClass<T>, factory: DependencyFactory<T>) {
        dependenciesList[klass] = FactoryHolder(this, factory)
    }

    override fun <T : Any> has(klass: KClass<T>): Boolean = dependenciesList.containsKey(klass)

    override fun getKeys(): Set<KClass<*>> = dependenciesList.keys

    override fun unregisterAll() = dependenciesList.clear()

    override fun unregister(klass: KClass<*>) {
        dependenciesList.remove(klass)
    }

    override fun addProvider(dependencyProvider: DependencyProvider) {
        dependencyProviders.add(dependencyProvider)
    }

    override fun removeProvider(dependencyProvider: DependencyProvider) {
        dependencyProviders.remove(dependencyProvider)
    }

    override fun <T : Any> get(klass: KClass<T>, vararg extraParam: Any): T {
        val dependency = dependenciesList[klass]
        if (dependency == null) {
            return getFromDependencyProvider(this, klass, extraParam)
        } else {
            @Suppress("UNCHECKED_CAST")
            return dependency.get() as T
        }
    }

    private fun <T : Any> getFromDependencyProvider(diContainer: DiContainer, klass: KClass<T>, extraParam: Array<out Any>): T {
        for (provider in dependencyProviders.reversed()) {
            try {
                val instance = provider.getDependency(diContainer, klass, *extraParam)
                if (instance != null) {
                    return instance
                }
            } catch (e: Exception) {
                throw DependencyInstancingException(e.message ?: "Unknown error")
            }
        }
        throw DependencyNotFoundException(klass.toString())
    }
}

class DependencyInstancingException(message: String) : Exception("Dependency instancing error: $message")
class DependencyNotFoundException(message: String) : Exception("Dependency not found: $message")