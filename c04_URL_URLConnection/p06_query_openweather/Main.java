package p06_query_openweather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLConnection;

// For this example, you could use our shared API key.
// Please be aware that there are limitations, e.g. no more than 60 calls/min.
// You are encouraged to create your own account at https://openweathermap.org/.
// That way you could obtain your own API key.
final class Main {

    static final String endpoint = "https://api.openweathermap.org/data/2.5/weather";
    static final String[] params = {"lat", "lon", "units", "lang"/*, "appid"*/};
    static final int INIT_CAPACITY = 256;

    public static void main(String[] args) {

        try {
            QueryBuilder qb = new QueryBuilder(endpoint, params);
            URLConnection uc = qb.toUrl().openConnection();

            // Note; You should not hardcode the API key in the code (we are doing it for simplicity's sake),
            // the best practice is to either load it from the configuration files or environment variables.
            // We could treat the API key like any other parameter, by adding "appid" to this.params array
            // Instead of that, we'll send the API key through the request headers:
            final String headerFieldKeyForAPIKey = "x-api-key";
            final String headerFieldForAPIKey = "<insert_api_key_here>";
            uc.setRequestProperty(headerFieldKeyForAPIKey, headerFieldForAPIKey);

            System.out.println();
            System.out.println("Sending GET request: " + qb);

            StringBuilder jsonString = new StringBuilder(INIT_CAPACITY);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()))) {
                String jsonLine;
                while ((jsonLine = in.readLine()) != null) {
                    jsonString.append(jsonLine);
                }
            }
            System.out.println("Response: " + jsonString);
            System.out.println();

            System.out.println("Deserializing JSON response....");

            // We are using Jackson library: https://github.com/FasterXML/jackson
            ObjectMapper mapper = new ObjectMapper();
            OpenWeatherResponse owr = mapper.readValue(
                    jsonString.toString(), OpenWeatherResponse.class);
            System.out.println("Task is finished! " +
                    "You can now process the data directly from API-specialized Java classes!");

            System.out.println();
            System.out.println("Pretty print JSON:");
            System.out.println(mapper.valueToTree(owr).toPrettyString());

        } catch (MalformedURLException e) {
            System.err.println("Please double-check URL!");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            System.err.println("An error occurred during JSON deserialization.");
            e.printStackTrace();
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
        }
    }
}
