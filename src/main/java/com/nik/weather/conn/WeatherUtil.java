package com.nik.weather.conn;

import com.nik.weather.data.Constants;
import com.nik.weather.data.payload.Forecasts;
import com.nik.weather.data.vo.Weather;
import com.nik.weather.exception.YahooWeatherServiceException;
import com.nik.weather.util.JsonUtil;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class WeatherUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherUtil.class);

    public static Weather callYahooWeatherService(String city, String region, boolean isStartup) throws YahooWeatherServiceException {

        String cityRegion = city + "," + region;
        //final String appId = "O0Rv5P7i";
        final String consumerKey = "dj0yJmk9UUs0UUNMQUN1SkIyJmQ9WVdrOVR6QlNkalZRTjJrbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PWE2";
        final String consumerSecret = "b4e285777ef0c86d43b8038c1d3adab23b0f78dc";
        final String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss";
        Forecasts forecasts = new Forecasts();
        try {
            long timestamp = new Date().getTime() / 1000;
            byte[] nonce = new byte[32];
            Random rand = new Random();
            rand.nextBytes(nonce);
            String oauthNonce = new String(nonce).replaceAll("\\W", "");

            List<String> parameters = new ArrayList<>();
            parameters.add("oauth_consumer_key=" + consumerKey);
            parameters.add("oauth_nonce=" + oauthNonce);
            parameters.add("oauth_signature_method=HMAC-SHA1");
            parameters.add("oauth_timestamp=" + timestamp);
            parameters.add("oauth_version=1.0");
            // Make sure value is encoded
            parameters.add("location=" + URLEncoder.encode(cityRegion, "UTF-8"));
            parameters.add("format=json");
            Collections.sort(parameters);

            StringBuilder parametersList = new StringBuilder();
            for (int i = 0; i < parameters.size(); i++) {
                parametersList.append((i > 0) ? "&" : "").append(parameters.get(i));
            }

            String signatureString = null;
            try {
                signatureString = "GET&" +
                        URLEncoder.encode(url, "UTF-8") + "&" +
                        URLEncoder.encode(parametersList.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String signature = null;
            try {
                SecretKeySpec signingKey = new SecretKeySpec((consumerSecret + "&").getBytes(), "HmacSHA1");
                if (signatureString != null) {
                    Mac mac = Mac.getInstance("HmacSHA1");
                    mac.init(signingKey);
                    byte[] rawHMAC = mac.doFinal(signatureString.getBytes());
                    Base64.Encoder encoder = Base64.getEncoder();
                    signature = encoder.encodeToString(rawHMAC);
                }
            } catch (Exception e) {
                System.err.println("Unable to append signature");
                System.exit(0);
            }

            String authorizationLine = "?" +
                    "oauth_consumer_key=" + consumerKey + "&" +
                    "oauth_signature_method=HMAC-SHA1&" +
                    "oauth_timestamp=" + timestamp + "&" +
                    "oauth_nonce=" + oauthNonce + "&" +
                    "oauth_version=1.0&" +
                    "oauth_signature=" + signature + "&";


            Map<String, String> headers = new HashMap<>();

            headers.put("postman-token", "992af996-963c-394b-c4f0-465796a30069");
            headers.put("cache-control", "no-cache");
            headers.put("Content-Type", "application/x-www-form-urlencoded");


            HttpRequestBase req = HttpUtil.buildHttpRequest("GET", url + authorizationLine + "location=" + cityRegion + "&format=json", null, headers, null);
            String json = HttpUtil.fetchRawResponse(req, 20);
            ArrayList arrayList = (ArrayList) JsonUtil.deserialize(json, Map.class).get("forecasts");
            Map map = (Map) arrayList.get(0);

            forecasts.setCode((Double) map.get("code"));
            forecasts.setDate((Double) map.get("date"));
            forecasts.setHigh((Double) map.get("high"));
            forecasts.setLow((Double) map.get("low"));
            forecasts.setText((String) map.get("text"));

            if (forecasts.text != null && forecasts.low != null && forecasts.high != null) {
                Weather w = new Weather();
                w.setCity(city);
                w.setStatus(forecasts.text);
                w.setMinTemp(forecasts.low.intValue());
                w.setMaxTemp(forecasts.high.intValue());
                return w;
            }
        } catch (Exception e) {
            LOGGER.error(e.getCause() + e.getMessage());
            if (!isStartup) {
                throw new YahooWeatherServiceException("Yahoo Service problem, please try again",
                        Constants.Weather.YAHOO_SERVICE_ERROR);
            }
        }
        return null;
    }
}
