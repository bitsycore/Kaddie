package com.bitsycore.lib.kaddie

inline fun <reified T : Any> MutableDiContainer.register(instance: T) =
    register(T::class, instance)

inline fun <reified T : Any> MutableDiContainer.register(noinline constructor: DependencyFactory<T>) =
    register(T::class, constructor)

inline fun <reified T : Any> MutableDiContainer.unregister() = unregister(T::class)

inline fun <reified T : Any> DiContainer.get(vararg extraParam: Any): T = get(T::class, extraParam)