package com.admin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class FileReadUtil {
    private final String path;
    public FileReadUtil(String path) {
        this.path = path;
    }

    public String getHtmlToString(String template) throws IOException, URISyntaxException {
        return readFromInputStream(getClass().getResourceAsStream(path + template));
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
