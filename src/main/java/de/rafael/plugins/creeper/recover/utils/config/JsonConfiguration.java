/*
 * Copyright (c) 2022-2023. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *         this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *     * Neither the name of the developer nor the names of its contributors
 *         may be used to endorse or promote products derived from this software
 *         without specific prior written permission.
 *     * Redistributions in source or binary form must keep the original package
 *         and class name.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.rafael.plugins.creeper.recover.utils.config;

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

    public void clear() {
        this.jsonObject = new JsonObject();
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
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {

            try {

                JsonReader jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

                jsonObject = JsonParser.parseReader(jsonReader).getAsJsonObject();

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            }

        }

        return new JsonConfiguration(file, jsonObject);

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
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

}
