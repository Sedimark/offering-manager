package eu.sedimark.service.helper;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HttpClientHelper {

    private interface ApiService {
        @GET
        Call<String> sendGetRequest(@Url String url);

        @POST
        Call<String> sendPostRequest(@Url String url, @Body String payload, @HeaderMap Map<String, String> headers);

        @PUT
        Call<String> sendPutRequest(@Url String url, @Body String payload, @HeaderMap Map<String, String> headers);

        @DELETE
        Call<String> sendDeleteRequest(@Url String url);
    }

    private final ApiService apiService;

    private final Logger LOGGER;

    public HttpClientHelper() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://default-url.com/") // Placeholder - An specific one is use per service
                .addConverterFactory(ScalarsConverterFactory.create()) // Returns raw strings
                .build();

        this.apiService = retrofit.create(ApiService.class);
        this.LOGGER = Logger.getLogger(this.getClass().getName());
    }


    private String sendRequest(Call<String> call, String url, String method) throws IOException {
        Response<String> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            LOGGER.severe("Error sending a request: " + response.code() + ". Body: " + response.body());
            throw new IOException(method + " request failed with response code: " + response.code());
        }
    }

    public String sendGetRequest(String url) throws IOException {
        return sendGetRequest(url, Collections.emptyMap());
    }

    public String sendGetRequest(String url, Map<String, String> queryParams) throws IOException {
        String urlWithQueryParams = buildUrlWithParams(url, queryParams);
        Call<String> call = apiService.sendGetRequest(urlWithQueryParams);
        return sendRequest(call, urlWithQueryParams, "GET");
    }

    public String sendPostRequest(String url, String payload) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        return sendPostRequest(url, Collections.emptyMap(), payload, headerMap);
    }

    public String sendPostRequest(String url, Map<String, String> queryParams, String payload, Map<String, String> headerMap) throws IOException {
        String urlWithQueryParams = buildUrlWithParams(url, queryParams);
        Call<String> call = apiService.sendPostRequest(urlWithQueryParams, payload, headerMap);
        return sendRequest(call, urlWithQueryParams, "POST");
    }

    public String sendPutRequest(String url, String payload) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        return sendPutRequest(url, Collections.emptyMap(), payload, headerMap);
    }

    public String sendPutRequest(String url, Map<String, String> queryParams, String payload, Map<String, String> headerMap) throws IOException {
        String urlWithQueryParams = buildUrlWithParams(url, queryParams);
        Call<String> call = apiService.sendPutRequest(urlWithQueryParams, payload, headerMap);
        return sendRequest(call, urlWithQueryParams, "PUT");
    }

    public String sendDeleteRequest(String url) throws IOException {
        return sendDeleteRequest(url, Collections.emptyMap());
    }

    public String sendDeleteRequest(String url, Map<String, String> queryParams) throws IOException {
        String urlWithQueryParams = buildUrlWithParams(url, queryParams);
        Call<String> call = apiService.sendDeleteRequest(urlWithQueryParams);
        return sendRequest(call, urlWithQueryParams, "DELETE");
    }

    private String buildUrlWithParams(String baseUrl, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return baseUrl;
        }
        StringBuilder urlWithParams = new StringBuilder(baseUrl);
        if(!baseUrl.contains("?")) {
            urlWithParams.append("?");
        } else {
            urlWithParams.append("&");
        }
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            urlWithParams.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        // Remove the trailing "&" and return
        return urlWithParams.substring(0, urlWithParams.length() - 1);
    }
}