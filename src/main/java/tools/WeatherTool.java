package tools;

import dev.langchain4j.agent.tool.Tool;

import java.util.HashMap;
import java.util.Map;

import static java.lang.IO.println;

public class WeatherTool {

    @Tool("Get weather forecast for a specified city.")
    public String get_forecast(String city) {
        Map<String, String> forecasts = new HashMap<>();
        forecasts.put("Paris", "Temperature: 28°C, Conditions: Sunny, Wind: 10 km/h");
        forecasts.put("Stockholm", "Temperature: 12°C, Conditions: Rainy, Wind: 15 km/h");
        forecasts.put("London", "Temperature: 15°C, Conditions: Rain, Wind: 8 km/h");
        forecasts.put("Berlin", "Temperature: 16°C, Conditions: Partly cloudy, Wind: 12 km/h");
        forecasts.put("Madrid", "Temperature: 24°C, Conditions: Clear skies, Wind: 5 km/h");

        var forecast = forecasts.getOrDefault(city, "Sorry, no forecast available for " + city);
        printUsage(city, forecast);
        return forecast;
    }

    private void printUsage(String city, String forecast) {
        println("-----------------------");
        println("*** get_forecast called with city: " + city);
        println("*** return forecast: " + forecast);
        println("-----------------------");
    }
}