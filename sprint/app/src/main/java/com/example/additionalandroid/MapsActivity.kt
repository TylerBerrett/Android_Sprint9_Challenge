package com.example.additionalandroid

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var centerLatLong: LatLng

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val permission = android.Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(permission), 12)
        } else{ fusedLocationClient = LocationServices.getFusedLocationProviderClient(this) }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fusedLocationClient.lastLocation.addOnSuccessListener {
            val currentLocation = LatLng(it.latitude, it.longitude)
            centerLatLong = currentLocation
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val audio = Uri.parse("android.resource://com.example.additionalandroid/raw/nice")
        val mp = MediaPlayer.create(this, audio)
        when (item.itemId){
            R.id.drop_pin -> {
                mMap.addMarker(MarkerOptions().position(mMap.cameraPosition.target).title("My Marker"))
                mp.start()

            }
            R.id.center_location -> mMap.moveCamera(CameraUpdateFactory.newLatLng(centerLatLong))
        }
        return super.onOptionsItemSelected(item)
    }

}
