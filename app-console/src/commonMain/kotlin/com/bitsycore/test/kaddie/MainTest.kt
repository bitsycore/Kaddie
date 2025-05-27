package com.bitsycore.test.kaddie

import com.bitsycore.lib.kaddie.MutableDiContainer
import com.bitsycore.lib.kaddie.get
import com.bitsycore.lib.kaddie.getDependency
import com.bitsycore.lib.kaddie.register
import com.bitsycore.lib.kaddie.registerDependency

class UserRepository {
    fun findById(id: Int): String = "User$id"
}

class UserServices(private val userRepository: UserRepository) {
    fun getUser(id: Int): String = userRepository.findById(id)
}

class UserServicesAnnex(private val userRepository: UserRepository) {
    fun getUser(id: Int): String = userRepository.findById(id)
}

object ObjectTest {
    fun sayHello() {
        println("Hello ObjectTest! $this")
    }

    class Test50 {
        fun sayHello() {
            println("Hello Test50! $this")
        }

        object Test150 {
            class Test200 {
                object Test300 {
                    class Test400(hello: Test200, val test300: Test300 = Test300) {
                        fun sayHello() {
                            println("Hello Test400! $this")
                        }
                    }
                }
            }
        }
    }

    class Test100 {
        fun sayHello() {
            println("Hello Test100! $this")
        }
    }
}
class NewUserRepository(val userId: UserServices) {
    fun findById(id: Int): String = userId.getUser(id)
}

fun main() {

    println("============================================================")
    println("TRYING WITH GLOBAL CONTAINER")
    println("============================================================")

    registerDependency { UserRepository() }
    registerDependency { UserServices(get()) }
    registerDependency(ObjectTest)
    registerDependency(ObjectTest.Test50())

    getDependency<UserServices>().also {
        println("$it: User: " + it.getUser(66))
    }

    getDependency<UserServicesAnnex>().also {
        println("$it: User: " + it.getUser(66))
    }

    getDependency<ObjectTest>().sayHello()
    getDependency<ObjectTest>().sayHello()
    getDependency<ObjectTest.Test50>().sayHello()
    getDependency<ObjectTest.Test50>().sayHello()
    getDependency<ObjectTest.Test100>().sayHello()
    getDependency<ObjectTest.Test100>().sayHello()
    getDependency<ObjectTest.Test50.Test150.Test200.Test300.Test400>().sayHello()

    getDependency<NewUserRepository>().also {
        println("$it: UserID: " + it.findById(99))
    }

    println("============================================================")
    println("TRYING WITH SOLO CONTAINER")
    println("============================================================")

    val testContainer = MutableDiContainer()

    testContainer.register { UserRepository() }
    testContainer.register { UserServices(get()) }
    testContainer.register(ObjectTest)
    testContainer.register(ObjectTest.Test50())

    testContainer.get<UserServices>().also {
        println("$it: User: " + it.getUser(66))
    }

    testContainer.get<UserServicesAnnex>().also {
        println("$it: User: " + it.getUser(66))
    }

    testContainer.get<ObjectTest>().sayHello()
    testContainer.get<ObjectTest>().sayHello()
    testContainer.get<ObjectTest.Test50>().sayHello()
    testContainer.get<ObjectTest.Test50>().sayHello()
    testContainer.get<ObjectTest.Test100>().sayHello()
    testContainer.get<ObjectTest.Test100>().sayHello()

    testContainer.get<NewUserRepository>().also {
        println("$it: UserID: " + it.findById(99))
    }

    testContainer.get<ObjectTest.Test50.Test150.Test200.Test300.Test400>().sayHello()

    return
}