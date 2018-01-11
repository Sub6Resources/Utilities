package com.sub6resources.utilities

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

/**
 * Created by whitaker on 1/11/18.
 */
abstract class BaseService: Service() {

    open val thisService: BaseService? = null
    open val serviceStartMethod = Service.START_STICKY
    open val commandMap: Map<String, () -> Unit>? = null

    open fun unbind() {}
    open fun bind() {}

    val baseBinder by lazy {BaseBinder()}

    inner class BaseBinder : Binder() {
        internal val service
            get() = thisService ?: this@BaseService
    }

    override fun onBind(intent: Intent): IBinder? {
        bind()
        return baseBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        unbind()
        return false
    }

    override fun onStartCommand(startIntent: Intent?, flags: Int, startId: Int): Int {
        startIntent?.let {
            val action = it.action
            val command = it.getStringExtra(CMD_NAME)
            if(action == ACTION_CMD) {
                commandMap?.get(command)?.invoke()
            }
        }
        return serviceStartMethod
    }

    companion object {
        // The action of the incoming Intent indicating that it contains a command
        // to be executed (see {@link #onStartCommand})
        val ACTION_CMD = "com.sub6resources.musicstreamer.ACTION_CMD"
        // The key in the extras of the incoming Intent indicating the command that
        // should be executed (see {@link #onStartCommand})
        val CMD_NAME = "CMD_NAME"
    }

}