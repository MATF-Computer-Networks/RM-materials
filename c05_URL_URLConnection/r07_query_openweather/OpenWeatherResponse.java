package r07_query_openweather;

// Mock-up class used for deserialization of response from:
// https://api.openweathermap.org/data/2.5/weather

final class OpenWeatherResponse {

    private static class Coord {
        public double lon;
        public double lat;
    }
    private static class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }
    private static class Main {
        public float temp;
        public float feels_like;
        public float temp_min;
        public float temp_max;
        public int pressure;
        public int humidity;
        public int sea_level;
        public int grnd_level;
    }
    private static class Wind {
        public float speed;
        public int deg;
        public float gust;
    }
    private static class Rain {
        public float one_h;
        public float three_h;
    }
    private static class Snow {
        public float one_h;
        public float three_h;
    }
    private static class Clouds {
        public int all;
    }
    private static class Sys {
        public int type;
        public int id;
        public String country;
        public long sunrise;
        public long sunset;
    }

    public Coord coord;
    public Weather[] weather;
    public String base;
    public Main main;
    public int visibility;
    public Wind wind;
    public Rain rain;
    public Clouds clouds;
    public long dt;
    public Sys sys;
    public int timezone;
    public int id;
    public String name;
    public int cod;
}