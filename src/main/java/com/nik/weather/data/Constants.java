package com.nik.weather.data;

public class Constants {
    public static class System {
        public static final Integer OKAY = 0;
        public static final Integer GENERAL_ERROR = 1;
        public static final String GENERAL_ERROR_MSG = "A general error occurred.";
    }

    public static class Weather{
        public static final Integer INVALID_NAME = 1000;
        public static final Integer INVALID_CITY = 1001;

        public static final String APP_ID = "O0Rv5P7i";
        public static final String CUSTOMER_KEY = "dj0yJmk9UUs0UUNMQUN1SkIyJmQ9WVdrOVR6QlNkalZRTjJrbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PWE2";
        public static final String CUSTOMER_SECRET = "b4e285777ef0c86d43b8038c1d3adab23b0f78dc";
        public static final String BASE_URL = "https://weather-ydn-yql.media.yahoo.com/forecastrss";
    }
}
