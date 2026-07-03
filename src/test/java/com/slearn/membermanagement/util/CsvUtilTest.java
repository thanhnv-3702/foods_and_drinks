package com.slearn.membermanagement.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvUtilTest {

    @Test
    void build_includesUtf8BomAndHeaders() {
        String csv = CsvUtil.build(List.of("ID", "Name"), List.of(List.of("1", "Alice")));

        assertThat(csv).startsWith("\uFEFF");
        assertThat(csv).contains("ID,Name");
        assertThat(csv).contains("1,Alice");
        assertThat(csv).endsWith("\r\n");
    }

    @Test
    void build_escapesCommaQuoteAndNewline() {
        String csv = CsvUtil.build(
                List.of("Value"),
                List.of(List.of("a,b"), List.of("say \"hi\""), List.of("line1\nline2")));

        assertThat(csv).contains("\"a,b\"");
        assertThat(csv).contains("\"say \"\"hi\"\"\"");
        assertThat(csv).contains("\"line1\nline2\"");
    }

    @Test
    void build_nullFieldBecomesEmpty() {
        var row = new java.util.ArrayList<String>();
        row.add(null);
        String csv = CsvUtil.build(List.of("Col"), List.of(row));

        assertThat(csv).contains("Col\r\n\r\n");
    }
}
