package com.duoc.aquachileapp.ui.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun recordarSinInternet(): Boolean {
    val context = LocalContext.current
    val manager = remember(context) {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    fun conectado(): Boolean {
        val red = manager.activeNetwork ?: return false
        val caps = manager.getNetworkCapabilities(red) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    var sinInternet by remember { mutableStateOf(!conectado()) }
    DisposableEffect(manager) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { sinInternet = !conectado() }
            override fun onLost(network: Network) { sinInternet = !conectado() }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                sinInternet = !conectado()
            }
        }
        manager.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
        sinInternet = !conectado()
        onDispose { manager.unregisterNetworkCallback(callback) }
    }
    return sinInternet
}
