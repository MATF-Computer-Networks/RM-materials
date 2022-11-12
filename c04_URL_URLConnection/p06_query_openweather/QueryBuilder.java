package p06_query_openweather;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Scanner;

public class QueryBuilder {

    private final AllowedParameters allowedParams;
    private final String endpoint;

    public QueryBuilder(String endpoint, String[] params) throws RuntimeException {
        this.allowedParams = new AllowedParameters(params);
        this.endpoint = endpoint;

        System.out.println("Please enter values for the following parameters:");
        // Example input values:
        //        "lat": 44.78
        //        "lon": 20.45
        //        "units": metric
        //        "lang": sr
        try (Scanner sc = new Scanner(System.in)) {
            for (String param : params) {
                System.out.print(MessageFormat.format("\"{0}\": ", param));
                allowedParams.setParam(param, sc.nextLine());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setParamValue(String param, String value) {
        try {
            this.allowedParams.setParam(param, value);
        } catch (Exception e) {
            System.err.println("Unsupported parameter!");
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.endpoint + this.allowedParams.makeQuery();
    }

    public URL toUrl() throws MalformedURLException {
        return new URL(this.toString());
    }
}
