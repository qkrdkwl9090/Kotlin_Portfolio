package com.dodong.todayweather

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {
    var TO_GRID = 0
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private var gpsTracker: GpsTracker? = null

    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val shortWeathers = ArrayList<ShortWeather>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tmp = convertGRID_GPS(TO_GRID, 37.579871128849334, 126.98935225645432)
        val tmp2 = convertGRID_GPS(TO_GRID, 35.101148844565955, 129.02478725562108)
        val tmp3 = convertGRID_GPS(TO_GRID, 33.500946412305076, 126.54663058817043)

        Log.e("test1", "x = " + tmp!!.x + ", y = " + tmp!!.y)
        Log.e("test1", "x = " + tmp2!!.x + ", y = " + tmp2!!.y)
        Log.e("test1", "x = " + tmp3!!.x + ", y = " + tmp3!!.y)
        getData()
        Stetho.initializeWithDefaults(this)
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            checkRunTimePermission()
        }
        val textview_address: TextView = findViewById(R.id.address_text)
        val ShowLocationButton: TextView = findViewById(R.id.re_address)
        ShowLocationButton.setOnClickListener {

            gpsTracker = GpsTracker(this@MainActivity)
            latitude = gpsTracker!!.latitude
            longitude = gpsTracker!!.longitude
            val address: String? = getCurrentAddress(latitude, longitude)

            var simpleAddress: List<String> = address!!.split(" ")
            textview_address.text =
                simpleAddress[0] + " " + simpleAddress[1] + " " + simpleAddress[2]
            Log.d(
                "test",
                "address : " + simpleAddress[0] + " " + simpleAddress[1] + " " + simpleAddress[2]
            )
            Toast.makeText(
                this@MainActivity,
                "현재위치 \n위도 $latitude\n경도 $longitude",
                Toast.LENGTH_LONG
            ).show()

        }
    }


    fun getData() {
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=1159068000")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(client)
            .build()
        val service = retrofit.create(RetrofitService::class.java)
        val rssService: Call<List<ShortWeather>> = service.getWeatherData()
        rssService.enqueue(object : Callback<List<ShortWeather>> {
            override fun onFailure(call: Call<List<ShortWeather>>, t: Throwable) {
                if (call.isCanceled()) {
                    println("Call was cancelled forcefully")
                } else {
                    println("Network Error :: " + t.localizedMessage)
                }
            }

            override fun onResponse(call: Call<List<ShortWeather>>, response: Response<List<ShortWeather>>) {
                if (response.isSuccessful()) {
                    val apiResponse = response.body()
                    // API response
                    val adapter = shortAdapter(
                        apiResponse!!,
                       LayoutInflater.from(this@MainActivity)
                    )
                    shortWeahter_recyclerview.adapter = adapter
                    shortWeahter_recyclerview.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

                } else {
                    println("Request Error :: " + response.errorBody())
                }
            }
        })


    }


    private fun convertGRID_GPS(
        mode: Int,
        lat_X: Double,
        lng_Y: Double
    ): LatXLngY? {
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //
        val DEGRAD = Math.PI / 180.0
        val RADDEG = 180.0 / Math.PI
        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD
        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)
        val rs = LatXLngY()
        if (mode == TO_GRID) {
            rs.lat = lat_X
            rs.lng = lng_Y
            var ra =
                Math.tan(Math.PI * 0.25 + lat_X * DEGRAD * 0.5)
            ra = re * sf / Math.pow(ra, sn)
            var theta = lng_Y * DEGRAD - olon
            if (theta > Math.PI) theta -= 2.0 * Math.PI
            if (theta < -Math.PI) theta += 2.0 * Math.PI
            theta *= sn
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5)
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5)
        } else {
            rs.x = lat_X
            rs.y = lng_Y
            val xn = lat_X - XO
            val yn = ro - lng_Y + YO
            var ra = Math.sqrt(xn * xn + yn * yn)
            if (sn < 0.0) {
                ra = -ra
            }
            var alat = Math.pow(re * sf / ra, 1.0 / sn)
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5
            var theta = 0.0
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5
                    if (xn < 0.0) {
                        theta = -theta
                    }
                } else theta = Math.atan2(xn, yn)
            }
            val alon = theta / sn + olon
            rs.lat = alat * RADDEG
            rs.lng = alon * RADDEG
        }
        return rs
    }


    internal class LatXLngY {
        var lat = 0.0
        var lng = 0.0
        var x = 0.0
        var y = 0.0
    }

    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String?>,
        grandResults: IntArray
    ) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true


            // 모든 퍼미션을 허용했는지 체크합니다.
            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {

                //위치 값을 가져올 수 있음
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {
                    Toast.makeText(
                        this@MainActivity,
                        "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(
                    this@MainActivity,
                    "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Toast.LENGTH_LONG
                )
                    .show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@MainActivity, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@MainActivity, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }


    fun getCurrentAddress(latitude: Double, longitude: Double): String? {

        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        addresses = try {
            geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        if (addresses == null || addresses.size == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show()
            return "주소 미발견"
        }
        val address: Address = addresses[0]
        return address.getAddressLine(0).toString().toString() + "\n"
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
                앱을 사용하기 위해서는 위치 서비스가 필요합니다.
                위치 설정을 수정하실래요?
                """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val callGPSSettingIntent =
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        })
        builder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create().show()
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음")
                        checkRunTimePermission()
                        return
                    }
                }
        }
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }


}

class shortAdapter(
    var postList: List<ShortWeather>,
    val inflater: LayoutInflater
) : RecyclerView.Adapter<shortAdapter.viewHolder>() {
    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView
        val rainper: TextView
        val temp: TextView

        init {
            time = itemView.findViewById(R.id.timeitem_item)
            rainper = itemView.findViewById(R.id.timeitem_rainper)
            temp = itemView.findViewById(R.id.timeitem_temp)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = inflater.inflate(R.layout.timeitemview, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.time.setText(postList.get(position).hour)
        holder.rainper.setText(postList.get(position).pop)
        holder.temp.setText(postList.get(position).temp)
    }
}

//
//class ReceiveShortWeather : AsyncTask<URL, Integer, Long> {
//
//    val shortWeathers = ArrayList<ShortWeather>()
//
//    fun doInBackground(vararg urls: URL?): Long? {
//        val url = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=1159068000"
//        val client = OkHttpClient()
//        val request: Request = Request.Builder()
//            .url(url)
//            .build()
//        var response: Response? = null
//        try {
//            response = client.newCall(request).execute()
//            parseXML(response.body().string())
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return null
//    }
//
//
//    fun parseXML(xml: String?) {
//        try {
//            var tagName = ""
//            var onHour = false
//            var onDay = false
//            var onTem = false
//            var onWfKor = false
//            var onPop = false
//            var onEnd = false
//            var isItemTag1 = false
//            var i = 0
//            val factory =
//                XmlPullParserFactory.newInstance()
//            val parser = factory.newPullParser()
//            parser.setInput(StringReader(xml))
//            var eventType = parser.eventType
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                if (eventType == XmlPullParser.START_TAG) {
//                    tagName = parser.name
//                    if (tagName == "data") {
//                        shortWeathers.add(ShortWeather())
//                        onEnd = false
//                        isItemTag1 = true
//                    }
//                } else if (eventType == XmlPullParser.TEXT && isItemTag1) {
//                    if (tagName == "hour" && !onHour) {
//                        shortWeathers[i].hour = (parser.text)
//                        onHour = true
//                    }
//                    if (tagName == "day" && !onDay) {
//                        shortWeathers[i].day = (parser.text)
//                        onDay = true
//                    }
//                    if (tagName == "temp" && !onTem) {
//                        shortWeathers[i].temp = (parser.text)
//                        onTem = true
//                    }
//                    if (tagName == "wfKor" && !onWfKor) {
//                        shortWeathers[i].wfKor = (parser.text)
//                        onWfKor = true
//                    }
//                    if (tagName == "pop" && !onPop) {
//                        shortWeathers[i].pop = (parser.text)
//                        onPop = true
//                    }
//                } else if (eventType == XmlPullParser.END_TAG) {
//                    if (tagName == "s06" && onEnd == false) {
//                        i++
//                        onHour = false
//                        onDay = false
//                        onTem = false
//                        onWfKor = false
//                        onPop = false
//                        isItemTag1 = false
//                        onEnd = true
//                    }
//                }
//                eventType = parser.next()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}
//
//class LongWeatherActivity : AppCompatActivity() {
//    var textView_longWeather: TextView? = null
//    ReceiveLongWeather().execute()
//
//
//    inner class ReceiveLongWeather : AsyncTask<URL?, Int?, Long?>() {
//        var longWeathers = ArrayList<LongWeather>()
//        protected override fun doInBackground(vararg urls: URL): Long? {
//            val url =
//                "http://www.kma.go.kr/weather/forecast/mid-term-rss3.jsp?stnId=109"
//            val client = OkHttpClient()
//            val request: Request = Request.Builder()
//                .url(url)
//                .build()
//            var response: Response? = null
//            try {
//                response = client.newCall(request).execute()
//                parseWeekXML(response.body().string())
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }
//            return null
//        }
//
//
//        fun parseWeekXML(xml: String?) {
//            try {
//                var tagName = ""
//                var onCity = false
//                var onTmEf = false
//                var onWf = false
//                var onTmn = false
//                var onTmx = false
//                var onEnd = false
//                var isItemTag1 = false
//                var i = 0
//                val factory =
//                    XmlPullParserFactory.newInstance()
//                val parser = factory.newPullParser()
//                parser.setInput(StringReader(xml))
//                var eventType = parser.eventType
//                while (eventType != XmlPullParser.END_DOCUMENT) {
//                    if (eventType == XmlPullParser.START_TAG) {
//                        tagName = parser.name
//                        if (tagName == "city") {
//                            eventType = parser.next()
//                            onCity = if (parser.text == "서울") {    // 파싱하고 싶은 지역 이름을 쓴다
//                                true
//                            } else {
//                                if (onCity) { // 이미 parsing을 끝냈을 경우
//                                    break
//                                } else {        // 아직 parsing을 못했을 경우
//                                    false
//                                }
//                            }
//                        }
//                        if (tagName == "data" && onCity) {
//                            longWeathers.add(LongWeather())
//                            onEnd = false
//                            isItemTag1 = true
//                        }
//                    } else if (eventType == XmlPullParser.TEXT && isItemTag1 && onCity) {
//                        if (tagName == "tmEf" && !onTmEf) {
//                            longWeathers[i].tmEf = (parser.text)
//                            onTmEf = true
//                        }
//                        if (tagName == "wf" && !onWf) {
//                            longWeathers[i].wf = (parser.text)
//                            onWf = true
//                        }
//                        if (tagName == "tmn" && !onTmn) {
//                            longWeathers[i].tmn = (parser.text)
//                            onTmn = true
//                        }
//                        if (tagName == "tmx" && !onTmx) {
//                            longWeathers[i].tmx = (parser.text)
//                            onTmx = true
//                        }
//                    } else if (eventType == XmlPullParser.END_TAG) {
//                        if (tagName == "reliability" && onEnd == false) {
//                            i++
//                            onTmEf = false
//                            onWf = false
//                            onTmn = false
//                            onTmx = false
//                            isItemTag1 = false
//                            onEnd = true
//                        }
//                    }
//                    eventType = parser.next()
//                }
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//}
