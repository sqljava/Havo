package com.example.havo.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.havo.adapter.DayAdapter


import com.example.havo.databinding.FragmentWeather2Binding
import com.example.havo.adapter.HourAdapter
import com.example.havo.data_class.Day
import com.example.havo.data_class.Hour


import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Weather2Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentWeather2Binding
    lateinit var hours: MutableList<Hour>
    lateinit var days: MutableList<Day>
    var url = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = FragmentWeather2Binding.inflate(inflater, container, false)
        var location = "Tashkent"
         url =
            "http://api.weatherapi.com/v1/forecast.json?key=9bcfb053b7d247fda8c53154230810&q=Tashkent&days=5&aqi=yes&alerts=yes"

        val requestque = Volley.newRequestQueue(requireContext())
        hours = mutableListOf()
        days = mutableListOf()

        var currentTime: String? = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))


        binding.addLocation.setOnClickListener {
            binding.addLocation.visibility = View.GONE
            binding.location.visibility = View.INVISIBLE
            binding.searchText.visibility = View.VISIBLE
            binding.searchText.findFocus()
            binding.send.isEnabled = true

            binding.send.visibility = View.VISIBLE
        }








        var request = JsonObjectRequest(url, object : Response.Listener<JSONObject> {
            override fun onResponse(response: JSONObject?) {

                var forecast = response?.getJSONObject("forecast")
                var location = response?.getJSONObject("location")
                var current = response?.getJSONObject("current")

                var condition = current?.getJSONObject("condition")

                var forecastday = forecast?.getJSONArray("forecastday")


                var state = condition?.getString("icon")
                var conditio = condition?.getString("text")

                var name = location?.getString("name")

                var temp_c = current?.getString("temp_c")
                var date = current?.getString("last_updated")
                var wind_kph = current?.getString("wind_kph")
                var wind_dir = current?.getString("wind_dir")
                var hum = current?.getString("humidity")



                binding.location.setText(name)
                binding.date.setText("Today , "+date)
                binding.temprature.setText(temp_c+" °C")

                binding.state.load("http:"+state)
                binding.wind.setText(wind_kph+" kph ,"+wind_dir)
                binding.humidity.setText(hum+"%")



                val resobj = forecastday!!.getJSONObject(0)

                val day = resobj.getJSONObject("day")

                val astro = resobj.getJSONObject("astro")

                val sunrise = astro.getString("sunrise")
                val sunset = astro.getString("sunset")

                val uv = current?.getString("uv")
//                    val max_wind = day.getString("maxwind_kph")
                val pressure = current?.getString("pressure_mb")
                val vision = current?.getString("vis_km")
                val chance_of_rain = day.getString("daily_chance_of_rain")
                val chance_of_snow = day.getString("daily_chance_of_snow")

                val maxtemp_c = day.getString("maxtemp_c")
                val mintemp_c = day.getString("mintemp_c")

                val date_day = resobj.getString("date")


                for (i in 0 until forecastday.length()){
                    val resobj = forecastday!!.getJSONObject(i)
                    val day = resobj!!.getJSONObject("day")
                    val text = resobj.getString("date")
                    val condition = day.getJSONObject("condition")

                    val state = condition.getString("text")
                    val img = condition.getString("icon")

                    val max_temp = day.getString("maxtemp_c")
                    val min_temp = day.getString("mintemp_c")
                    val date = text.substring(text.length-5,text.length)

                    val day_object = Day(date,state,max_temp,min_temp,img)
                    days.add(day_object)
                    var manager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                    binding.dayRec.layoutManager = manager
                    binding.dayRec.adapter = DayAdapter(days)



                }

                val hour = resobj.getJSONArray("hour")
                if (date_day == currentTime){
                    for (i in 0 until hour.length()){
                        val hour_obj = hour.getJSONObject(i)
                        val time_date = hour_obj.getString("time")
                        val time = time_date.substring(time_date.length-5,time_date.length)
                        val temp_hour = hour_obj.getString("temp_c")
                        val wind_hour = hour_obj.getString("wind_mph")
                        val condition_hour = hour_obj.getJSONObject("condition")
                        val img_hour = condition_hour.getString("icon")
                        val hour = Hour(time,temp_hour,wind_hour,img_hour)

                        hours.add(hour)
                        var manager =
                            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                        binding.dailyHourRv.layoutManager = manager
                        binding.dailyHourRv.adapter = HourAdapter(hours)



                    }
                }


//                    Log.d("TAG", "onResponse: $hour")
            }


        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("TAG", "onErrorResponse: $error")
            }

        })

        binding.send.setOnClickListener {
            hours.clear()
            days.clear()

            if (binding.searchText.text.length > 0 && binding.searchText.text.isNotEmpty()) {
                location = binding.searchText.text.toString()
                binding.addLocation.visibility = View.VISIBLE
                binding.searchText.visibility = View.GONE
                binding.location.visibility = View.VISIBLE
                binding.send.visibility = View.GONE


                url =
                    "http://api.weatherapi.com/v1/forecast.json?key=9bcfb053b7d247fda8c53154230810&q=" + location + "&days=5&aqi=yes&alerts=yes"
                Log.d("TAG", "onCreateView: $url")



                request = JsonObjectRequest(url, object : Response.Listener<JSONObject> {
                    override fun onResponse(response: JSONObject?) {

                        var forecast = response?.getJSONObject("forecast")
                        var location = response?.getJSONObject("location")
                        var current = response?.getJSONObject("current")

                        var condition = current?.getJSONObject("condition")

                        var forecastday = forecast?.getJSONArray("forecastday")


                        var state = condition?.getString("icon")
                        var conditio = condition?.getString("text")

                        var name = location?.getString("name")

                        var temp_c = current?.getString("temp_c")
                        var date = current?.getString("last_updated")
                        var wind_kph = current?.getString("wind_kph")
                        var wind_dir = current?.getString("wind_dir")
                        var hum = current?.getString("humidity")



                        binding.location.setText(name)
                        binding.date.setText("Today , "+date)
                        binding.temprature.setText(temp_c+" °C")

                        binding.state.load("http:"+state)
                        binding.wind.setText(wind_kph+" kph ,"+wind_dir)
                        binding.humidity.setText(hum+"%")



                        val resobj = forecastday!!.getJSONObject(0)

                        val day = resobj.getJSONObject("day")

                        val astro = resobj.getJSONObject("astro")

                        val sunrise = astro.getString("sunrise")
                        val sunset = astro.getString("sunset")

                        val uv = current?.getString("uv")
//                    val max_wind = day.getString("maxwind_kph")
                        val pressure = current?.getString("pressure_mb")
                        val vision = current?.getString("vis_km")
                        val chance_of_rain = day.getString("daily_chance_of_rain")
                        val chance_of_snow = day.getString("daily_chance_of_snow")

                        val maxtemp_c = day.getString("maxtemp_c")
                        val mintemp_c = day.getString("mintemp_c")

                        val date_day = resobj.getString("date")



                        for (i in 0 until forecastday.length()){
                            val resobj = forecastday!!.getJSONObject(i)
                            val day = resobj!!.getJSONObject("day")
                            val text = resobj.getString("date")
                            val condition = day.getJSONObject("condition")

                            val state = condition.getString("text")
                            val img = condition.getString("icon")

                            val max_temp = day.getString("maxtemp_c")
                            val min_temp = day.getString("mintemp_c")
                            val date = text.substring(text.length-5,text.length)

                            val day_object = Day(date,state,max_temp,min_temp,img)
                            days.add(day_object)
                            var manager =
                                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                            binding.dayRec.layoutManager = manager
                            binding.dayRec.adapter = DayAdapter(days)



                        }

                        val hour = resobj.getJSONArray("hour")
                        if (date_day == currentTime){
                            for (i in 0 until hour.length()){
                                val hour_obj = hour.getJSONObject(i)
                                val time_date = hour_obj.getString("time")
                                val time = time_date.substring(time_date.length-5,time_date.length)
                                val temp_hour = hour_obj.getString("temp_c")
                                val wind_hour = hour_obj.getString("wind_mph")
                                val condition_hour = hour_obj.getJSONObject("condition")
                                val img_hour = condition_hour.getString("icon")
                                val hour = Hour(time,temp_hour,wind_hour,img_hour)

                                hours.add(hour)
                                var manager =
                                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                                binding.dailyHourRv.layoutManager = manager
                                binding.dailyHourRv.adapter = HourAdapter(hours)



                            }
                        }


//                    Log.d("TAG", "onResponse: $hour")
                    }


                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        Log.d("TAG", "onErrorResponse: $error")
                    }

                })
                requestque.add(request)


            } else Toast.makeText(requireContext(), "you have not filled gap", Toast.LENGTH_SHORT)
                .show()

        }
        requestque.add(request)




        return binding.root
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Weather2Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Weather2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}