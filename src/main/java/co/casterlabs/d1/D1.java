package co.casterlabs.d1;

import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import co.casterlabs.d1.result.D1Result;
import co.casterlabs.rakurai.json.Rson;
import co.casterlabs.rakurai.json.element.JsonArray;
import co.casterlabs.rakurai.json.element.JsonObject;
import co.casterlabs.rakurai.json.serialization.JsonParseException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.With;
import xyz.e3ndr.fastloggingframework.logging.FastLogger;
import xyz.e3ndr.fastloggingframework.logging.LogLevel;

@ToString
public class D1 {
    private @ToString.Exclude FastLogger logger;
    private @ToString.Exclude Builder config;

    static {
        D1.class.getClassLoader().setPackageAssertionStatus("co.casterlabs.d1", true);;
    }

    private @Getter String databaseRegion;
    private @Getter String databaseName;
    private @Getter String databaseId;
    private @Getter String accountId;

    private D1(Builder config) throws D1Exception {
        this.config = config;

        JsonObject about = HttpUtil.get(
            this.config.apiToken,
            String.format(
                "%s/client/v4/accounts/%s/d1/database/%s",
                this.config.apiUrl, this.config.accountId, this.config.databaseId
            )
        ).getAsObject();

        this.databaseRegion = about.getString("running_in_region");
        this.databaseName = about.getString("name");
        this.databaseId = this.config.databaseId;
        this.accountId = this.config.accountId;

        this.logger = new FastLogger(String.format("D1 (%s|%s@%s)", this.databaseId, this.databaseName, this.databaseRegion));
        this.logger.setCurrentLevel(this.config.logLevel);
    }

    /* ---------------- */
    /* API              */
    /* ---------------- */

    public D1Result query(@NonNull String sql, @Nullable Object... params) throws D1Exception {
        JsonObject body = new JsonObject().put("sql", sql);
        if (params != null && params.length > 0) {
            body.put("params", Rson.DEFAULT.toJson(params));
        }

        this.logger.trace("Executing: %s", body);

        String url = String.format(
            "%s/client/v4/accounts/%s/d1/database/%s/query",
            this.config.apiUrl, this.config.accountId, this.config.databaseId
        );
        this.logger.trace("Query url: %s", url);

        JsonArray result = HttpUtil.post(this.config.apiToken, url, body).getAsArray();
        this.logger.trace("Raw result: %s", result);

        if (result.size() > 1) {
            throw new IllegalStateException("Use batchQuery() to execute multiple SQL statements.");
        }

        try {
            return Rson.DEFAULT.fromJson(result.get(0), D1Result.class);
        } catch (JsonParseException e) {
            throw new D1Exception(e);
        }
    }

    public D1Result[] batchQuery(@NonNull String sql) throws D1Exception {
        JsonObject body = JsonObject.singleton("sql", sql);
        this.logger.trace("Executing: %s", body);

        String url = String.format(
            "%s/client/v4/accounts/%s/d1/database/%s/query",
            this.config.apiUrl, this.config.accountId, this.config.databaseId
        );
        this.logger.trace("Query url: %s", url);

        JsonArray result = HttpUtil.post(this.config.apiToken, url, body).getAsArray();
        this.logger.trace("Raw result: %s", result);

        try {
            return Rson.DEFAULT.fromJson(result, D1Result[].class);
        } catch (JsonParseException e) {
            throw new D1Exception(e);
        }
    }

    /* ---------------- */
    /* Builder          */
    /* ---------------- */

    /**
     * Creates a new chainable builder for creating a new D1 instance.
     *
     * @param  projectId the project id
     * 
     * @return           a builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        /** The level to log at, you should probably leave this at WARNING. */
        private final @With LogLevel logLevel;

        /**
         * The API URL to use, if for some reason you have access to some private API.
         */
        private final @With String apiUrl;

        /**
         * Your Cloudflare account id. You can find this in the dashboard url or on the
         * right-hand panel.
         */
        private final @With String accountId;

        /**
         * The D1 database id. You can find this on the database page.
         */
        private final @With String databaseId;

        /**
         * Your api token. Create this at https://dash.cloudflare.com/profile/api-tokens
         * with the `Account.D1:edit` permission.
         */
        private final @With String apiToken;

        private Builder() {
            this.logLevel = LogLevel.WARNING;
            this.apiUrl = "https://api.cloudflare.com";
            this.accountId = null;
            this.databaseId = null;
            this.apiToken = null;
        }

        /**
         * Builds a new D1 instance based off the settings you've set.
         *
         * @return             a new D1 instance.
         * 
         * @throws IOException if an I/O error occurs whilst constructing the database.
         * @throws D1Exception
         */
        public D1 build() throws D1Exception {
            assert this.apiUrl != null && !this.apiUrl.isEmpty() : "API URL cannot be empty or null.";
            assert this.accountId != null && !this.accountId.isEmpty() : "Account ID cannot be empty or null.";
            assert this.databaseId != null && !this.databaseId.isEmpty() : "Database ID cannot be empty or null.";
            assert this.apiToken != null && !this.apiToken.isEmpty() : "API Token cannot be empty or null.";
            return new D1(this);
        }

    }

}
