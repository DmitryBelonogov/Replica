package com.nougust3.diary.db;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SQLParser {

    public static List<String> parseSqlFile(String file, AssetManager assetManager) throws IOException {
        List<String> instructions = null;
        InputStream is = assetManager.open(file);

        try {
            instructions = parseSqlFile(is);
        }
        finally {
            is.close();
        }

        return instructions;
    }

    public static List<String> parseSqlFile(InputStream is) throws IOException {
        String script = removeComments(is);
        return splitSqlScript(script, ';');
    }

    private static String removeComments(InputStream is) throws IOException {

        StringBuilder sql = new StringBuilder();

        InputStreamReader isReader = new InputStreamReader(is);
        try {
            BufferedReader buffReader = new BufferedReader(isReader);
            try {
                String line;
                String multiLineComment = null;
                while ((line = buffReader.readLine()) != null) {
                    line = line.trim();

                    if (multiLineComment == null) {
                        if (line.startsWith("/*")) {
                            if (!line.endsWith("}")) {
                                multiLineComment = "/*";
                            }
                        } else if (line.startsWith("{")) {
                            if (!line.endsWith("}")) {
                                multiLineComment = "{";
                            }
                        } else if (!line.startsWith("--") && !line.equals("")) {
                            sql.append(" ").append(line);
                        }
                    } else if (multiLineComment.equals("/*")) {
                        if (line.endsWith("*/")) {
                            multiLineComment = null;
                        }
                    } else if (multiLineComment.equals("{")) {
                        if (line.endsWith("}")) {
                            multiLineComment = null;
                        }
                    }

                }
            } finally {
                buffReader.close();
            }

        } finally {
            isReader.close();
        }

        return sql.toString();
    }


    private static List<String> splitSqlScript(String script, char delim) {
        List<String> statements = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inLiteral = false;
        char[] content = script.toCharArray();
        for (int i = 0; i < script.length(); i++) {
            if (content[i] == '\'') {
                inLiteral = !inLiteral;
            }
            if (content[i] == delim && !inLiteral) {
                if (sb.length() > 0) {
                    statements.add(sb.toString().trim());
                    sb = new StringBuilder();
                }
            } else {
                sb.append(content[i]);
            }
        }
        if (sb.length() > 0) {
            statements.add(sb.toString().trim());
        }
        return statements;
    }
}
