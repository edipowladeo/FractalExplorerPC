package com.example.fractal

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ObjectPool<T>(val createObject:()->T) {
    var pool: ConcurrentLinkedQueue<T> = ConcurrentLinkedQueue()
    private var executorService: ScheduledExecutorService? = null

    /**
     * Creates the pool.
     *
     * @param minIdle minimum number of objects residing in the pool
     */
    constructor(minIdle: Int,construtor:()->T):this(construtor) {

        // initialize pool
        initialize(minIdle)
    }

    /**
     * Creates the pool.
     *
     * @param minIdle            minimum number of objects residing in the pool
     * @param maxIdle            maximum number of objects residing in the pool
     * @param validationInterval time in seconds for periodical checking of minIdle / maxIdle conditions in a separate thread.
     * When the number of objects is less than minIdle, missing instances will be created.
     * When the number of objects is greater than maxIdle, too many instances will be removed.
     */
    constructor(
            minIdle: Int, maxIdle: Int, validationInterval: SegundosI,
            construtor: () -> T
    ):this(construtor) {
        // initialize pool
        initialize(minIdle)

        // check pool conditions in a separate thread
        executorService = Executors.newSingleThreadScheduledExecutor().apply {
            scheduleWithFixedDelay(Runnable {
                val size: Int = pool.size
                if (size < minIdle) {
                    val sizeToBeAdded = minIdle - size
                    for (i in 0 until sizeToBeAdded) {
                        pool.add(createObject())
                    }
                } else if (size > maxIdle) {
                    val sizeToBeRemoved = size - maxIdle
                    for (i in 0 until sizeToBeRemoved) {
                        pool.poll()
                    }
                }

//todo: create interface or class for logs vc prinln
            //    Log.i("ObjectPool","${size} -> ${pool.size}")
                println("ObjectPool: ${size} -> ${pool.size}")
            }, validationInterval, validationInterval, TimeUnit.SECONDS)
        }
    }

    constructor(
            minIdle: Int, maxIdle: Int, validationInterval: SegundosI,
            construtor: () -> T,
            destrutor:(T)->Unit
    ):this(construtor) {
        // initialize pool
        initialize(minIdle)

        // check pool conditions in a separate thread
        executorService = Executors.newSingleThreadScheduledExecutor().apply {
            scheduleWithFixedDelay(Runnable {
                val size: Int = pool.size
                if (size < minIdle) {
                    val sizeToBeAdded = minIdle - size
                    for (i in 0 until sizeToBeAdded) {
                        pool.add(createObject())
                    }
                } else if (size > maxIdle) {
                    val sizeToBeRemoved = size - maxIdle
                    for (i in 0 until sizeToBeRemoved) {
                        pool.poll()
                    }
                }


    //            Log.i("ObjectPool","${size} -> ${pool.size}")
                println("ObjectPool: ${size} -> ${pool.size}")
            }, validationInterval, validationInterval, TimeUnit.SECONDS)
        }
    }



    /**
     * Gets the next free object from the pool. If the pool doesn't contain any objects,
     * a new object will be created and given to the caller of this method back.
     *
     * @return T borrowed object
     */
    fun borrowObject(): T {
        var objeto: T
        if (pool.poll().also { objeto = it } == null) {
            objeto = createObject()
        }
        return objeto
    }

    /**
     * Returns object back to the pool.
     *
     * @param `object` object to be returned
     */
    fun returnObject(objeto: T?) {
        if (objeto == null) {
            return
        }
        pool.offer(objeto)
    }

    /**
     * Shutdown this pool.
     */
    fun shutdown() {
        executorService?.let { shutdown() }
    }

    private fun initialize(minIdle: Int) {
        pool = ConcurrentLinkedQueue<T>()
        for (i in 0 until minIdle) {
            pool.add(createObject())
        }
    }
}