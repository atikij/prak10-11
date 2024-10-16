import java.util.concurrent.Future
import java.util.concurrent.Callable
import java.util.concurrent.Executors

//1.Создать поток при помощи наследования от интерфейса Runnable
class MyRunnable : Runnable {
    override fun run() {
        println("Runnable is running on thread: ${Thread.currentThread().name}")
    }
}

fun runnableExample() {
    val thread = Thread(MyRunnable())
    thread.start()
    thread.join()
}
//2.Создать поток при помощи наследования от Thread
class MyThread : Thread() {
    override fun run() {
        println("Thread is running on thread: ${Thread.currentThread().name}")
    }
}

fun threadExample() {
    val thread = MyThread()
    thread.start()
    thread.join()
}
//3. Создать поток при помощи Callable и Future


fun callableExample() {
    val executor = Executors.newSingleThreadExecutor()
    val future = executor.submit(Callable {
        println("Callable is running on thread: ${Thread.currentThread().name}")
        42
    })

    println("Result from Callable: ${future.get()}")
    executor.shutdown()
}
//4. Создать пул потоков для первых трёх заданий
fun threadPoolExample() {
    val executor = Executors.newFixedThreadPool(3)

    val runnableTask = MyRunnable()
    val threadTask = MyThread()
    val callableTask = Callable {
        println("Callable task running in thread pool")
        42
    }

    executor.submit(runnableTask)
    executor.submit(threadTask)
    val future = executor.submit(callableTask)

    println("Callable result: ${future.get()}")

    executor.shutdown()
}
//5. Создать массив потоков, как только будет готов результат хотя бы в одном из них - отменить остальные

fun cancelOtherThreadsOnFirstResult() {
    val executor = Executors.newFixedThreadPool(5)
    val tasks = List(5) { index ->
        Callable {
            Thread.sleep((index + 1) * 1000L)
            println("Task $index completed")
            index
        }
    }

    val futures: List<Future<Int>> = tasks.map { executor.submit(it) }

    val firstResult = futures.first { it.isDone || it.get() != null }
    println("First task completed: ${firstResult.get()}")

    futures.forEach { it.cancel(true) } // Отменяем оставшиеся задачи
    executor.shutdown()
}
//6. Создать массив потоков, как только будут готовы результаты первых трёх потоков - отменить остальные
fun cancelAfterThreeResults() {
    val executor = Executors.newFixedThreadPool(5)
    val tasks = List(5) { index ->
        Callable {
            Thread.sleep((index + 1) * 1000L)
            println("Task $index completed")
            index
        }
    }

    val futures: List<Future<Int>> = tasks.map { executor.submit(it) }
    var completedCount = 0

    futures.forEach {
        try {
            if (it.get() != null) {
                completedCount++
                if (completedCount >= 3) {
                    println("Three tasks completed. Cancelling the rest.")
                    futures.forEach { future ->
                        future.cancel(true)
                    }
                    return@forEach
                }
            }
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }

    executor.shutdown()
}
//7. Создать массив потоков, выводить результат выполнения каждого потока сразу как только он будет готов
fun printResultsAsReady() {
    val executor = Executors.newFixedThreadPool(5)
    val tasks = List(5) { index ->
        Callable {
            Thread.sleep((index + 1) * 1000L)
            index * 2
        }
    }

    val futures = tasks.map { executor.submit(it) }

    futures.forEach {
        println("Task result: ${it.get()}")
    }

    executor.shutdown()
}
//8. Создать массив потоков, как только очередной поток вернет результат - ставить на паузу на 1 секунду все оставшиеся потоки
fun pauseOthersOnResult() {
    val executor = Executors.newFixedThreadPool(5)
    val tasks = List(5) { index ->
        Callable {
            Thread.sleep((index + 1) * 1000L)
            index
        }
    }

    val futures = tasks.map { executor.submit(it) }

    futures.forEach {
        val result = it.get()
        println("Task result: $result")
        Thread.sleep(1000L)
    }

    executor.shutdown()
}

fun main() {
    println("1. Runnable Example:")
    runnableExample()

    println("\n2. Thread Example:")
    threadExample()

    println("\n3. Callable Example:")
    callableExample()

    println("\n4. Thread Pool Example:")
    threadPoolExample()

    println("\n5. Cancel other threads on first result:")
    cancelOtherThreadsOnFirstResult()

    println("\n6. Cancel after three results:")
    cancelAfterThreeResults()

    println("\n7. Print results as ready:")
    printResultsAsReady()

    println("\n8. Pause others on result:")
    pauseOthersOnResult()
}
