import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

// 1. Прерываемая функция (suspend)
suspend fun suspendableFunction() {
    delay(1000L)
    println("Suspendable function executed on thread: ${Thread.currentThread().name}")
}

// 2. Launch задача
fun launchTaskExample() = runBlocking {
    launch {
        println("Launch task started on thread: ${Thread.currentThread().name}")
        delay(500L)
        println("Launch task completed")
    }
}

// 3. Async задача с возвратом значения
fun asyncTaskExample() = runBlocking {
    val result = async {
        println("Async task started on thread: ${Thread.currentThread().name}")
        delay(1000L)
        42
    }
    println("Async task result: ${result.await()}")
}

// 4. Массив задач с использованием launch
fun launchMultipleTasks() = runBlocking {
    val jobs = List(5) { index ->
        launch {
            println("Task $index started")
            delay((index + 1) * 500L)
            println("Task $index completed")
        }
    }
    jobs.forEach { it.join() }
}

// 5. Массив задач с использованием async
fun asyncMultipleTasks() = runBlocking {
    val results = List(5) { index ->
        async {
            println("Async task $index started")
            delay((index + 1) * 500L)
            index * 10
        }
    }
    results.forEach { println("Result: ${it.await()}") }
}

// 6. Отмена задачи и всех дочерних задач
fun cancelTaskExample() = runBlocking {
    val job = launch {
        launch {
            try {
                repeat(1000) { i ->
                    println("Job $i running")
                    delay(500L)
                }
            } catch (e: CancellationException) {
                println("Job cancelled!")
            }
        }
    }
    delay(1500L)
    println("Cancelling parent job")
    job.cancelAndJoin()
    println("Parent job cancelled")
}

// 7. Перехват исключения при отмене задачи
fun exceptionHandlingExample() = runBlocking {
    val job = launch {
        try {
            repeat(1000) { i ->
                println("Task $i running")
                delay(300L)
            }
        } catch (e: CancellationException) {
            println("Task cancelled, exception caught: ${e.message}")
        } finally {
            println("Task cleaned up")
        }
    }
    delay(1000L)
    println("Cancelling task")
    job.cancelAndJoin()
}

// 8. Разные контексты выполнения задач
fun differentContextsExample() = runBlocking {
    launch(Dispatchers.Default) {
        println("Running on Default context: ${Thread.currentThread().name}")
    }
    launch(Dispatchers.IO) {
        println("Running on IO context: ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Unconfined) {
        println("Running on Unconfined context: ${Thread.currentThread().name}")
    }
}

// 9. Использование yield
fun yieldExample() = runBlocking {
    launch {
        repeat(5) { i ->
            println("Task before yield $i")
            yield()
            println("Task after yield $i")
        }
    }
}

// 10. Шаблон "Производитель-потребитель"
fun producerConsumerExample() = runBlocking {
    val channel = Channel<Int>()

    // Producer coroutine
    val producer = launch {
        for (i in 1..5) {
            println("Produced: $i")
            channel.send(i)
            delay(300L)
        }
        channel.close()
    }

    // Consumer coroutine
    val consumer = launch {
        for (value in channel) {
            println("Consumed: $value")
            delay(500L)
        }
    }

    producer.join()
    consumer.join()
}

fun main() = runBlocking {
    println("1. Suspendable function:")
    suspendableFunction()

    println("\n2. Launch task example:")
    launchTaskExample()

    println("\n3. Async task example:")
    asyncTaskExample()

    println("\n4. Launch multiple tasks:")
    launchMultipleTasks()

    println("\n5. Async multiple tasks:")
    asyncMultipleTasks()

    println("\n6. Cancel task example:")
    cancelTaskExample()

    println("\n7. Exception handling example:")
    exceptionHandlingExample()

    println("\n8. Different contexts example:")
    differentContextsExample()

    println("\n9. Yield example:")
    yieldExample()

    println("\n10. Producer-Consumer example:")
    producerConsumerExample()
}
