package ch.hortis.maven.plugins;

import com.google.common.io.CharStreams;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Charsets.UTF_8;
import static org.fest.assertions.Assertions.assertThat;

public class TestRegex {

    private String pattern = "console\\.(log|warn|error|info)\\(.+\\);";
    Pattern p = Pattern.compile(pattern);


    @Test
    public void shouldContainConsoleLog() throws IOException, URISyntaxException {
        String content = readInputStreamAsString(this.getClass().getResourceAsStream("/test.js"));
        assertThat(countOccurrenceOfPattern(content)).isEqualTo(5);
    }

    private int countOccurrenceOfPattern(String content) {
        int counter = 0;
        Matcher m = p.matcher(content);
        while (m.find()) {
            counter++;
        }
        return counter;
    }

    public static String readInputStreamAsString(InputStream in) throws IOException, URISyntaxException {
        return CharStreams.toString(new InputStreamReader(in, UTF_8));
    }
}
