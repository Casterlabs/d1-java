package co.casterlabs.d1.result;

import org.jetbrains.annotations.Nullable;

import co.casterlabs.rakurai.json.annotating.JsonClass;
import co.casterlabs.rakurai.json.annotating.JsonField;
import co.casterlabs.rakurai.json.element.JsonArray;
import co.casterlabs.rakurai.json.element.JsonObject;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;

@ToString
@JsonClass(exposeAll = true)
public class D1Result {
    public final D1ResultMeta meta = null;

    @JsonField("results")
    public final JsonArray rawResults = null;

    /**
     * Retrieves the first result from the query.
     * 
     * @return null, if there are no results.
     */
    public <T> @Nullable D1Value first() {
        if (this.rawResults.isEmpty()) return null;
        return new D1Value(this.rawResults.get(0));
    }

    /**
     * Retrieves the specified field of the first result from the query.
     * 
     * @param  field                    The field to retrieve
     * 
     * @return                          null, if there are no results.
     * 
     * @throws IllegalArgumentException if the specified field could not be found in
     *                                  the result.
     */
    public D1Value first(@NonNull String field) {
        if (this.rawResults.isEmpty()) return null;

        JsonObject row = this.rawResults
            .getObject(0);

        assert row.containsKey(field) : new IllegalArgumentException("Could not find field \"" + field + "\" in result: " + row);
        return new D1Value(row.get(field));
    }

    /**
     * Retrieves all rows from the query as a single value (for use with
     * D1Value#as()).
     */
    @SneakyThrows
    public D1Value all() {
        return new D1Value(this.rawResults);
    }

}
