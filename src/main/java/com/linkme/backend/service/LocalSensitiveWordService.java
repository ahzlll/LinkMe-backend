package com.linkme.backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class LocalSensitiveWordService {

    @Autowired(required = false)
    private Set<String> localSensitiveWords;

    private final Set<String> sensitiveWordSet = new HashSet<>();
    private Pattern[] patterns;

    @PostConstruct
    public void init() {
        if (localSensitiveWords != null && !localSensitiveWords.isEmpty()) {
            sensitiveWordSet.addAll(localSensitiveWords);
        }

        if (sensitiveWordSet.isEmpty()) {
            sensitiveWordSet.add("台独");
            sensitiveWordSet.add("分裂国家");
            sensitiveWordSet.add("暴力恐怖");
            sensitiveWordSet.add("色情低俗");
        }

        buildPatterns();
    }

    private void buildPatterns() {
        patterns = new Pattern[sensitiveWordSet.size()];
        int index = 0;
        for (String word : sensitiveWordSet) {
            patterns[index++] = Pattern.compile(Pattern.quote(word), Pattern.CASE_INSENSITIVE);
        }
    }

    public List<String> findAll(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> found = new ArrayList<>();
        Set<String> foundSet = new HashSet<>();

        for (String word : sensitiveWordSet) {
            if (text.toLowerCase().contains(word.toLowerCase()) && !foundSet.contains(word)) {
                found.add(word);
                foundSet.add(word);
            }
        }

        for (Pattern pattern : patterns) {
            java.util.regex.Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String matched = matcher.group();
                if (!foundSet.contains(matched)) {
                    found.add(matched);
                    foundSet.add(matched);
                }
            }
        }

        return found;
    }

    public boolean contains(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        return !findAll(text).isEmpty();
    }

    public String replace(String text, char replacement) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        String result = text;
        for (Pattern pattern : patterns) {
            result = pattern.matcher(result).replaceAll(String.valueOf(replacement));
        }
        return result;
    }

    public void addWord(String word) {
        if (word != null && !word.trim().isEmpty()) {
            sensitiveWordSet.add(word.trim());
            buildPatterns();
        }
    }

    public void removeWord(String word) {
        if (word != null) {
            sensitiveWordSet.remove(word.trim());
            buildPatterns();
        }
    }

    public int getWordCount() {
        return sensitiveWordSet.size();
    }
}
