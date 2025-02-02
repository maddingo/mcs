package it.mulders.mcs.search;

import org.apache.commons.io.output.NullOutputStream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DelegatingOutputPrinterTest implements WithAssertions {
    private final OutputPrinter noOutput = mock(OutputPrinter.class);
    private final OutputPrinter pomXmlOutput = mock(OutputPrinter.class);
    private final OutputPrinter tabularSearchOutput = mock(OutputPrinter.class);

    private final DelegatingOutputPrinter printer = new DelegatingOutputPrinter(noOutput, pomXmlOutput, tabularSearchOutput);

    private final SearchQuery query = new CoordinateQuery("org.codehaus.plexus", "plexus-utils");
    private final PrintStream outputStream = new PrintStream(NullOutputStream.nullOutputStream());

    @Test
    void no_results_delegate() {
        printer.print(query, responseWithResult(0), outputStream);
        verify(noOutput).print(eq(query), any(), eq(outputStream));
        verify(pomXmlOutput, never()).print(any(), any(), any());
        verify(tabularSearchOutput, never()).print(any(), any(), any());
    }

    @Test
    void one_result_delegate() {
        printer.print(query, responseWithResult(1), outputStream);
        verify(noOutput, never()).print(any(), any(), any());
        verify(pomXmlOutput).print(eq(query), any(), eq(outputStream));
        verify(tabularSearchOutput, never()).print(any(), any(), any());
    }

    @Test
    void multiple_results_delegate() {
        printer.print(query, responseWithResult(2), outputStream);
        verify(noOutput, never()).print(any(), any(), any());
        verify(pomXmlOutput, never()).print(any(), any(), any());
        verify(tabularSearchOutput).print(eq(query), any(), eq(outputStream));
    }

    private SearchResponse.Response responseWithResult(int count) {
        return new SearchResponse.Response(count, 0, new SearchResponse.Response.Doc[0]);
    }
}