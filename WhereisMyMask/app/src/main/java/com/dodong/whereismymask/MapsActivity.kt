package com.dodong.whereismymask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)




    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val name: String = intent.getStringExtra("map_name")

        Log.d("test1","이름" + name)
        val lat: Double = intent.getDoubleExtra("map_lat",0.0)

        Log.d("test1","lat :" + lat)
        val lng: Double = intent.getDoubleExtra("map_lng",0.0)

        Log.d("test1","lng :" + lng)
        val maskStore = LatLng(lat, lng)
        mMap.addMarker(MarkerOptions().position(maskStore).title(name))//지도에 마커 생성
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(maskStore,17f)) //지도  이동, 확대
    }

}
