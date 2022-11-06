package p06_query_openweather;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;

public class AllowedParameters {

    private HashMap<String, String> paramValue;

    public AllowedParameters(String[] allowedParams) {
        this.paramValue = new HashMap<>();
        for (String param : allowedParams) {
            this.paramValue.put(param, "");
        }
    }

    public void setParam(String param, String value) throws Exception {
        if (!this.paramValue.containsKey(param)) {
            throw new RuntimeException(MessageFormat.format(
                    "Unexpected parameter: \"{0}\"! Please check API docs again. ", param));
        }
        String oldValue = this.paramValue.get(param);
        if (!oldValue.isEmpty()) {
            System.out.println(MessageFormat.format(
                    "Changing value of parameter \"{0}\", from {1} to {2}", param, oldValue, value));
        }
        this.paramValue.put(param, value);
    }

    public String getValue(String param) throws Exception {
        if (!this.paramValue.containsKey(param)) {
            throw new RuntimeException(MessageFormat.format(
                    "Unexpected parameter: \"{0}\"! Please check API docs again. ", param));
        }
        return this.paramValue.get(param);
    }

    public String makeQuery() {
        StringBuilder query = new StringBuilder(Main.INIT_CAPACITY);
        boolean firstParam = true;

        // Iterate over all parameters and concatenate ?par=val or &par=val
        for (String param : this.paramValue.keySet()) {
            String value = this.paramValue.get(param);
            if (value.isEmpty()) {
                continue;
            }
            query.append(firstParam ? '?' : '&');
            query.append(URLEncoder.encode(param, StandardCharsets.UTF_8));
            query.append('=');
            query.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            firstParam = false;
        }
        return query.toString();
    }
}

