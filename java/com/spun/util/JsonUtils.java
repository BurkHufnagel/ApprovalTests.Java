package com.spun.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtils
{
  public static String prettyPrint(String json)
  {
    if (!ObjectUtils.isClassPresent("com.google.gson.Gson")) { throw new RuntimeException(
        "Missing Gson dependency\n  Pretty print uses Gson parser.\n  You can get this from the maven repo \n  or https://github.com/google/gson"); }
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(json);
    return gson.toJson(je);
  }
}
