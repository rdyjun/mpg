package namegenerator;

public class KeyNameGenerator {
    
    public static String getKey(String statName, String optionName) {
        return "stats." + statName + ".amount." + optionName;
    }
}
