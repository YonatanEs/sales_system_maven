
package General;

import com.google.gson.JsonParser;


public class Error {
    
    public static String parseJsonError(String json) {
        try {
            return JsonParser.parseString(json)
                    .getAsJsonObject()
                    .get("error")
                    .getAsString();
        } catch (Exception e) {
            return json;
        }
    }
}
