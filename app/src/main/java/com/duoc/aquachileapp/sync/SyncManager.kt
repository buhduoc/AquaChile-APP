package com.duoc.aquachileapp.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncManager {
    private const val TRABAJO_UNICO = "aquachile_prechequeos_sync"

    fun programar(context: Context) {
        val solicitud = OneTimeWorkRequestBuilder<PreChequeoSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork(TRABAJO_UNICO, ExistingWorkPolicy.APPEND_OR_REPLACE, solicitud)
    }
}
