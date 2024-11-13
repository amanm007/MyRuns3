
package com.example.aman_singh_myruns2

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapHandler(
    private val context: Context,
    private val mapFragment: SupportMapFragment
) : OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val polylineOptions = PolylineOptions().width(5f).color(android.graphics.Color.BLUE)

    init {
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    fun addMarker(location: LatLng) {
        googleMap.addMarker(MarkerOptions().position(location))
    }

    fun updatePolyline(location: LatLng) {
        polylineOptions.add(location)
        googleMap.addPolyline(polylineOptions)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}
