package com.duoc.aquachileapp.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.duoc.aquachileapp.data.Remote.toDto
import com.duoc.aquachileapp.data.Remote.RetrofitClient
import com.duoc.aquachileapp.data.local.toDomain
import com.duoc.aquachileapp.repository.EvidenciaEquipoRepository
import android.net.Uri
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.NonCancellable
import com.duoc.aquachileapp.data.local.AppDatabase
import com.duoc.aquachileapp.data.local.EstadoSincronizacion
import com.duoc.aquachileapp.repository.PreChequeoRepository
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

class PreChequeoSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private suspend fun subirFoto(valor: String): String {
        if (valor.matches(Regex("/api/fotos/[a-f0-9]{64}\\.(jpg|png)"))) return valor
        val datos = withContext(Dispatchers.IO) {
            val uri = Uri.parse(valor)
            require(uri.scheme == "content" || uri.scheme == "file") { "Referencia de foto no compatible" }
            val entrada = try { applicationContext.contentResolver.openInputStream(uri) }
                catch (e: SecurityException) { throw IllegalArgumentException("Foto sin permiso: volver a seleccionar", e) }
                catch (e: java.io.FileNotFoundException) { throw IllegalArgumentException("Foto no disponible", e) }
            requireNotNull(entrada) { "Foto no disponible" }.use { stream ->
                val salida = java.io.ByteArrayOutputStream()
                val buffer = ByteArray(8192)
                while (true) {
                    val n = stream.read(buffer)
                    if (n < 0) break
                    require(salida.size() + n <= 10 * 1024 * 1024) { "Foto supera 10 MB" }
                    salida.write(buffer, 0, n)
                }
                salida.toByteArray()
            }
        }
        val png = datos.size >= 8 && datos.take(8).map { it.toInt() and 255 } == listOf(137,80,78,71,13,10,26,10)
        val jpg = datos.size >= 3 && datos.take(3).map { it.toInt() and 255 } == listOf(255,216,255)
        require(png || jpg) { "Solo se admiten fotos JPEG o PNG" }
        val tipo = if (png) "image/png" else "image/jpeg"
        val nombre = if (png) "foto.png" else "foto.jpg"
        val parte = MultipartBody.Part.createFormData("archivo", nombre, datos.toRequestBody(tipo.toMediaType()))
        val ruta = RetrofitClient.api.subirFoto(parte)["url"]
        require(ruta != null && ruta.matches(Regex("/api/fotos/[a-f0-9]{64}\\.(jpg|png)"))) { "Ruta de foto inválida" }
        return ruta
    }

    override suspend fun doWork(): Result {
        com.duoc.aquachileapp.data.Remote.SesionSegura.iniciar(applicationContext)
        if (com.duoc.aquachileapp.data.Remote.SesionSegura.token == null) return Result.success()
        val dao = AppDatabase.obtenerInstancia(applicationContext).preChequeoDao()
        val repository = PreChequeoRepository()
        var hayErroresTemporales = false

        for (registro in dao.obtenerPendientes()) {
            if (isStopped) return Result.retry()

            try {
                dao.actualizarEstado(registro.id, EstadoSincronizacion.SINCRONIZANDO)
                val dominio = registro.toDomain()
                val cache = mutableMapOf<String, String>()
                suspend fun foto(uri: String): String = cache[uri] ?: subirFoto(uri).also { cache[uri] = it }
                val items = dominio.items.map { item -> item.copy(fotos = item.fotos.map { foto(it) }) }
                val fotos = (dominio.fotos.map { foto(it) } + items.flatMap { it.fotos }).distinct()
                val evidencias = dominio.evidenciasEquipos.filter { !it.uriFotoActual.isNullOrBlank() }.map { e ->
                    e.copy(uriFotoActual = foto(requireNotNull(e.uriFotoActual)))
                }
                val dto = dominio.copy(items = items, fotos = fotos).toDto().copy(id = null, uuid = registro.uuid)
                val respuesta = repository.crear(dto)
                val idRemoto = respuesta.id
                    ?: throw IOException("El servidor no devolvió el ID del prechequeo")
                val repoEvidencias = EvidenciaEquipoRepository()
                val existentes = repoEvidencias.listarPorFaena(idRemoto.toString())
                for (e in evidencias) {
                    val dato = e.toDto(idRemoto.toString(), dominio.supervisor).copy(id = null)
                    val anterior = existentes.firstOrNull { it.idEquipo == e.idEquipo }?.id
                    if (anterior == null) repoEvidencias.crear(dato)
                    else repoEvidencias.actualizar(anterior, dato)
                }
                dao.marcarSincronizado(registro.id, idRemoto)
                Log.d("OFFLINE_SYNC", "Sincronizado UUID=${registro.uuid}, servidor=$idRemoto")
            } catch (e: CancellationException) {
                withContext(NonCancellable) { dao.actualizarEstado(registro.id, EstadoSincronizacion.PENDIENTE) }
                throw e
            } catch (e: HttpException) {
                if (e.code() == 401) { dao.actualizarEstado(registro.id, EstadoSincronizacion.PENDIENTE, "Inicia sesión nuevamente"); return Result.success() }
                val temporal = e.code() == 408 || e.code() == 429 || e.code() >= 500
                dao.actualizarEstado(registro.id, EstadoSincronizacion.ERROR, "HTTP ${e.code()}")
                hayErroresTemporales = hayErroresTemporales || temporal
                Log.e("OFFLINE_SYNC", "HTTP ${e.code()} UUID=${registro.uuid}", e)
            } catch (e: IOException) {
                dao.actualizarEstado(registro.id, EstadoSincronizacion.ERROR, e.message)
                hayErroresTemporales = true
                Log.e("OFFLINE_SYNC", "Sin conexión con el backend UUID=${registro.uuid}", e)
            } catch (e: Exception) {
                dao.actualizarEstado(registro.id, EstadoSincronizacion.ERROR, e.message)
                Log.e("OFFLINE_SYNC", "Error de sincronización UUID=${registro.uuid}", e)
            }
        }
        return if (hayErroresTemporales) Result.retry() else Result.success()
    }
}
