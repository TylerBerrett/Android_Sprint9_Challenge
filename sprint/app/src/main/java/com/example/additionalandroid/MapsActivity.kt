package com.example.additionalandroid

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var centerLatLong: LatLng

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var mp: MediaPlayer

    private lateinit var audio: Uri

    lateinit var sharedPref: SharedPreferences

    companion object {
        private val defaultAudioLocation = "android.resource://com.example.additionalandroid/raw/nice"

    }




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

        audio = Uri.parse(defaultAudioLocation)

        sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val savedAudio = sharedPref.getString("key1", "")

        mp = if (savedAudio.isNullOrBlank()){
            MediaPlayer.create(this, audio)
        } else {
            MediaPlayer.create(this, Uri.parse(savedAudio))
        }






    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fusedLocationClient.lastLocation.addOnSuccessListener {
            val currentLocation = LatLng(it.latitude, it.longitude)
            centerLatLong = currentLocation
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        }

        mMap.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener{
            override fun onMarkerDragStart(p0: Marker?) {
                p0?.remove()
            }
            override fun onMarkerDragEnd(p0: Marker?) {}
            override fun onMarkerDrag(p0: Marker?) {}
        })

        mMap.setOnMapLongClickListener {
            addMarker(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val center = mMap.cameraPosition.target
        when (item.itemId){
            R.id.drop_pin -> addMarker(center)
            R.id.center_location -> mMap.moveCamera(CameraUpdateFactory.newLatLng(centerLatLong))
            R.id.add_audio -> {
                val getAudio = Intent()
                getAudio.action = Intent.ACTION_GET_CONTENT
                getAudio.type = ("audio/*")
                startActivityForResult(getAudio, 12)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 12){
            data?.let {
                sharedPref.edit().putString("key", "${it.data}").commit()
                val newAudio = it.data ?: Uri.parse(defaultAudioLocation)
                mp = MediaPlayer.create(this, newAudio)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun addMarker(latLng: LatLng){
        val lat = latLng.latitude
        val long = latLng.longitude
        mMap.addMarker(MarkerOptions().position(latLng).title("$lat, $long")).isDraggable = true
        mp.start()
    }

}
