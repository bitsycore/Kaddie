import com.bitsycore.lib.kloggy.AnsiEscapeUtil
import com.bitsycore.lib.kloggy.LogLevels
import com.bitsycore.lib.kloggy.log
import com.bitsycore.lib.kstacktrace.Stacktrace
import com.bitsycore.test.Hello
import com.bitsycore.test.secondShow

@Suppress("NOTHING_TO_INLINE")
fun logFrame() {
    println("============================================================")
    print(Stacktrace(1))
    println("============================================================\n")
}

fun main() {
    logFrame()
    secondShow()
    Hello().show()
    val thro = Hello().getAThrowable()
    println("============================================================")
    print(Stacktrace(thro))
    println("============================================================")
    TestDI.testDI(546687754, 1, 1, 1, 1, 1, 1, 1, 1, LogLevels.Info, LogLevels.Info, LogLevels.Info)
    log(Stacktrace().currentFrame.toString(), AnsiEscapeUtil.colorize("Hello", AnsiEscapeUtil.CYAN, bold = true) + AnsiEscapeUtil.colorize("Hello", AnsiEscapeUtil.CYAN, bold = false))
    testLog()
    return
}

fun testLog() {
    //language=JSON
    val dummyJson : String = """
        {
          "posts": [
            {
              "content": "This is the content of the first post.",
              "createdAt": "2024-07-26T10:00:00Z",
              "id": 101,
              "title": "First Post"
            },
            {
              "content": "This is the content of the second post.",
              "createdAt": "2024-07-27T11:00:00Z",
              "id": 102,
              "title": "Second Post"
            }
          ],
          "settings": {
            "notifications": {
              "email": true,
              "sms": false
            },
            "theme": "dark"
          },
          "user": {
            "email": "john.doe@example.com",
            "id": 1,
            "isActive": true,
            "name": "John Doe"
          }
        }
    """.trimIndent()
    log("Main", "Hello, world!", LogLevels.Warning)
    log("Core", "Hello, world!", LogLevels.Error)
    log("MemoryHandler", "Hello, world!", LogLevels.Info)
    log("Main", "Hello, world!", LogLevels.Info)
    log("Main", "Hello, world!", LogLevels.Verbose)
    log("Main", "Hello, world!", LogLevels.Debug)
    log("Main", "Server Answer : ${dummyJson.replace("\n", "").replace("\t", "").replace(" ", "")}", LogLevels.Debug)
    log("Main", "Hello, world!", LogLevels.Debug)
    log("Main", "Hello, world!", LogLevels.Verbose)
    log("UserManager", "Hello, world!", LogLevels.Warning)
    log("Main", "Hello, world!", LogLevels.Info)
    log("MemoryCleaner", "Hello, world!", LogLevels.Error)
    log("User", "Hello, world!", LogLevels.Warning)

    print("${Stacktrace()}")
}