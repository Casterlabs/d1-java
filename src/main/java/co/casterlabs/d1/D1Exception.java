package co.casterlabs.d1;

import java.util.LinkedList;
import java.util.List;

import co.casterlabs.rakurai.json.element.JsonArray;
import co.casterlabs.rakurai.json.element.JsonObject;
import lombok.Getter;
import lombok.NonNull;

/**
 * Note that we internally use a status code of -1 for exceptions caused by
 * other exceptions.
 */
public class D1Exception extends Exception {
    private static final long serialVersionUID = 7203201210813293559L;

    private final @Getter int[] codes;
    private final @Getter String[] messages;

    public D1Exception(@NonNull String reason, @NonNull int[] codes, @NonNull String[] messages) {
        super(reason);
        this.codes = codes;
        this.messages = messages;
    }

    public D1Exception(Throwable cause) {
        super(cause.getMessage(), cause);
        this.codes = new int[] {
                -1
        };
        this.messages = new String[] {
                cause.getMessage()
        };
    }

    static void checkAndThrow(JsonObject response) throws D1Exception {
        JsonArray errors = response.getArray("errors");
        if (errors.isEmpty()) return;

        int[] codes = new int[errors.size()];
        for (int idx = 0; idx < codes.length; idx++) {
            codes[idx] = errors.getObject(idx).getNumber("code").intValue();
        }

        String[] messages = new String[errors.size()];
        for (int idx = 0; idx < messages.length; idx++) {
            messages[idx] = errors.getObject(idx).getString("message");
        }

        List<String> reasons = new LinkedList<>();
        for (int idx = 0; idx < messages.length; idx++) {
            reasons.add(String.format("%d: %s", codes[idx], messages[idx]));
        }

        throw new D1Exception(String.join("\n", reasons), codes, messages);
    }

}
