package com.example.mausam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.example.mausam.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//886705b4c1182eb1c69f28eb8c520e20

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Delhi")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!= null) {
                    fetchWeatherData(query)
                    return true
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(
            cityName,
            "886705b4c1182eb1c69f28eb8c520e20",
            "metric"
        )
        response.enqueue(
            object : retrofit2.Callback<MausamApp?> {
                override fun onResponse(
                    call: Call<MausamApp?>,
                    response: Response<MausamApp?>
                ) {
                    val responseBody = response.body()
                    if (response.isSuccessful() && responseBody != null) {
                        val temperature = responseBody.main.temp.toString()
                        val humidity = responseBody.main.humidity.toString()
                        val windSpeed = responseBody.wind.speed.toString()
                        val sunrise = responseBody.sys.sunrise.toLong()
                        val sunset = responseBody.sys.sunset.toLong()
                        val seaLevel = responseBody.main.sea_level.toString()
                        val condition = responseBody.weather[0].main
                        val minTemp = responseBody.main.temp_min.toString()
                        val maxTemp = responseBody.main.temp_max.toString()
                        val cityName = responseBody.name
                        val countryName = responseBody.sys.country


                        binding.temperature.text = "$temperature °C"
                        binding.humidity1.text = "$humidity %"
                        binding.wind1.text = "$windSpeed km/h"
                        binding.sunrise1.text ="${time(sunrise)}"
                        binding.sunset1.text = "${time(sunset)}"
                        binding.weather.text = "$condition"
                        binding.mintemp.text = "Min Temp: $minTemp °C"
                        binding.maxtemp.text = "Max Temp: $maxTemp °C"
                        binding.cityName.text = "$cityName"
                        binding.sea1.text = "$seaLevel hPa"
                        binding.conditions1.text = "$condition"
                        binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text=date()

                        changeImagesAccordingToWeatherCondition(condition)
                        
                    }
                }

                override fun onFailure(call: retrofit2.Call<MausamApp?>, t: Throwable) {
                    println("Error in fetching data")
                }
            }
        )
    }

    private fun changeImagesAccordingToWeatherCondition(condition: String) {
        when(condition){
            "Clear Sky","Sunny", "Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy" ,"Haze", "Smoke" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle", "Moderate Rain", "Shower", "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow" , "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    private fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
    private fun date():String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())

    }
    private fun time(timestamp: Long):String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
}