package de.thedodo24.adventuria.adventuriaproxy.common.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Language {


    private static Map<String, String> languageMap = new HashMap<>();

    public static void init() throws NoSuchElementException {
        Yaml yaml = new Yaml();
        InputStream inputStream;
        inputStream = Language.class.getClassLoader().getResourceAsStream("language.yml");
        Map<String, String> values = yaml.load(inputStream);
        if(values != null)
            languageMap = values;
        else
            throw new NoSuchElementException("No file named language.yml found in resources");
    }

    public static String get(String key) {
        if(languageMap.containsKey(key))
            return languageMap.get(key);
        return "§cError: phrase-not-found §e("+key+")";
    }

    public static String get(String key, Object... args) {
        if(languageMap.containsKey(key))
            return MessageFormat.format(languageMap.get(key), args);
        return "§cError: phrase-not-found §e("+key+")";
    }

}
