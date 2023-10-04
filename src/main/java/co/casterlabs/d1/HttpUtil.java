package co.casterlabs.d1;

import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import co.casterlabs.rakurai.json.Rson;
import co.casterlabs.rakurai.json.element.JsonElement;
import co.casterlabs.rakurai.json.element.JsonObject;
import lombok.NonNull;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class HttpUtil {
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType applicationJson = MediaType.parse("application/json");

    static @Nullable JsonElement post(@NonNull String authToken, @NonNull String url, @NonNull JsonObject body) throws D1Exception {
        try (Response response = client.newCall(
            new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + authToken)
                .post(RequestBody.create(body.toString(), applicationJson))
                .build()
        ).execute()) {
            String responseBody = response.body().string();

            if (responseBody.isEmpty()) {
                return null;
            }

            JsonObject json = Rson.DEFAULT.fromJson(responseBody, JsonObject.class);
            D1Exception.checkAndThrow(json);

            return json.get("result");
        } catch (IOException e) {
            throw new D1Exception(e);
        }
    }

    static @Nullable JsonElement get(@NonNull String authToken, @NonNull String url) throws D1Exception {
        try (Response response = client.newCall(
            new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + authToken)
                .build()
        ).execute()) {
            String responseBody = response.body().string();

            if (responseBody.isEmpty()) {
                return null;
            }

            JsonObject json = Rson.DEFAULT.fromJson(responseBody, JsonObject.class);
            D1Exception.checkAndThrow(json);

            return json.get("result");
        } catch (IOException e) {
            throw new D1Exception(e);
        }
    }

}
