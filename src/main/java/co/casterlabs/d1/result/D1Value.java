package co.casterlabs.d1.result;

import co.casterlabs.rakurai.json.Rson;
import co.casterlabs.rakurai.json.TypeToken;
import co.casterlabs.rakurai.json.element.JsonArray;
import co.casterlabs.rakurai.json.element.JsonElement;
import co.casterlabs.rakurai.json.serialization.JsonParseException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

@AllArgsConstructor
public class D1Value {
    public final JsonElement raw;

    /**
     * @throws JsonParseException if a JSON deserialization error occurs.
     */
    @SneakyThrows
    public <T> @NonNull T as(@NonNull Class<T> type) {
        return Rson.DEFAULT.fromJson(this.raw, type);
    }

    /**
     * @throws JsonParseException if a JSON deserialization error occurs.
     */
    @SneakyThrows
    public <T> @NonNull T as(@NonNull TypeToken<T> type) {
        return Rson.DEFAULT.fromJson(this.raw, type);
    }

    /**
     * Converts this D1Value into an array of D1Values. The raw value MUST be an
     * array type.
     */
    @SneakyThrows
    public D1Value[] asArray() {
        JsonArray rawResults = this.raw.getAsArray();
        D1Value[] arr = new D1Value[rawResults.size()];
        for (int idx = 0; idx < arr.length; idx++) {
            arr[idx] = new D1Value(rawResults.get(idx));
        }
        return arr;
    }

    @Override
    public String toString() {
        return this.raw.toString();
    }

}
