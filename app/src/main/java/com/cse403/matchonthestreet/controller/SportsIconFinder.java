package com.cse403.matchonthestreet.controller;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Hao Liu on 2/23/16.
 *
 * This is the utility class that parses the title of the event and assign
 * icons to them.
 */
public class SportsIconFinder {

    private static SportsIconFinder sportsIconFinder;

    protected static Map<Set<String>, String> drawableMap = new HashMap<>();

    private SportsIconFinder(Context context) {
        System.out.println("called icon finder");
        AssetManager manager = context.getResources().getAssets();
        try {
            String[] drawableNames = manager.list("");
            for (String filename : drawableNames) {
                if (filename.startsWith("sports_") && filename.endsWith(".png")) {
                    String[] drawableTokens = filename.substring(0, filename.length() - 4).
                            replace("sports_", "").
                            split("_");

                    ArrayList<String> tokenList = new ArrayList<String>(Arrays.asList(drawableTokens));
                    String[] badWords = new String[]{
                            "and", "a", "on", "with", "up", "down", "out", "view", "side",
                            "of", "ball", "person", "player", "group", "team", "playing", "like",
                            "game", "couple", "at", "the", "from", "between", "among", "within",
                            "in", "black", "silhouette"
                    };
                    for (String w : badWords) {
                        while (tokenList.contains(w) || Pattern.matches("\\d", w)) {
                            tokenList.remove(w);
                        }
                    }
                    System.out.print("Filename: " + filename + "=[");
                    for (String t : tokenList) {
                        System.out.print(t + ",");
                    }
                    System.out.println("];");

                    drawableMap.put(new HashSet<>(tokenList), filename);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialize(Context context) {
        if (sportsIconFinder == null) {
            sportsIconFinder = new SportsIconFinder(context);
        }
    }

    public static SportsIconFinder getInstance() {
        return sportsIconFinder;
    }

    public String matchString(Context context, String query) {
        String bestMatch = "";
        int bestCount = 0;

        query = query.replaceAll("[^\\w\\s]", "").toLowerCase();
        System.out.println("Q: " + query);
        String[] queryTokens = query.split(" ");

        // Special case for "tennis" not "table tennis"
        if (query.contains("tennis") && !query.contains("table tennis")) {
            bestMatch = "sports_tennis_ball.png";
        } else {
            for (Set<String> tokens : drawableMap.keySet()) {
                int matchCount = 0;
                for (String token : queryTokens) {
                    if (tokens.contains(token)) {
                        if (matchCount == 0) {
                            matchCount++;
                        }
                    }
                }
                if (matchCount > bestCount) {
                    bestCount = matchCount;
                    bestMatch = drawableMap.get(tokens);
                }
            }

            if (bestMatch.isEmpty() || bestCount == 0) {
                return null;
            }
        }

        return bestMatch;
    }

    public static Drawable getAssetImage(Context context, String filename) {
        AssetManager assets = context.getResources().getAssets();
        InputStream buffer = null;
        if (drawableMap.values().contains(filename)) {
            try {
                buffer = new BufferedInputStream((assets.open(filename)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(buffer);
            return new BitmapDrawable(context.getResources(), bitmap);
        } else {
            return null;
        }
    }



}
