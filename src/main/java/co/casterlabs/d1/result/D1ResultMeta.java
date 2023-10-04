package co.casterlabs.d1.result;

import co.casterlabs.rakurai.json.annotating.JsonClass;
import co.casterlabs.rakurai.json.annotating.JsonField;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@JsonClass(exposeAll = true)
public class D1ResultMeta {
    @JsonField("served_by")
    private String servedBy;

    private double duration;

    private int changes;

    @JsonField("last_row_id")
    private int lastRowId;

    @JsonField("changed_db")
    @Accessors(fluent = true)
    private boolean didChangedDb;

    @JsonField("size_after")
    private long sizeAfter;

    @JsonField("rows_read")
    private int rowsRead;

    @JsonField("rows_written")
    private int rowsWritten;
}