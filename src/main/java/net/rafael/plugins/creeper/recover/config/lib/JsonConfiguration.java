package net.rafael.plugins.creeper.recover.config.lib;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonConfiguration {

    private final File file;
    private JsonObject jsonObject;

    public JsonConfiguration(File file, JsonObject jsonObject) {

        this.file = file;
        this.jsonObject = jsonObject;

    }

    public JsonObject getJson() {
        return jsonObject;
    }

    public void setJson(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public File getFile() {
        return file;
    }

    public void saveConfig() {

        Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting().create();

        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8));
            try {
                out.write(gson.toJson(jsonObject));
            } finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static JsonConfiguration loadConfig(File folder, String fileName) {

        if(!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder.getPath() + "/" + fileName);

        JsonObject jsonObject = new JsonObject();

        if(!file.exists()) {
            try {

                file.createNewFile();

                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write("{}");
                fileWriter.flush();
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            try {

                JsonReader jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

                jsonObject = new JsonParser().parse(jsonReader).getAsJsonObject();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        return new JsonConfiguration(file, jsonObject);

    }

}
