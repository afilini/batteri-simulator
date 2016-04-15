package cms;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

/**
 * Copyright (C) Alekos Filini (afilini) - All Rights Reserved
 * <p/>
 * This file is part of cms
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alekos Filini <alekos.filini@gmail.com>, marzo 2016
 */

public class ResultSender {

    public static boolean send (Vector<Status> result) throws IOException {
        Gson gson = new Gson();
        System.out.println(gson.toJson(result));

        URL url = new URL("http://localhost/bcms/utils/saveMatch.php");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        byte[] out = ("data=" + gson.toJson(result)).getBytes();
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("X-Magic", "06da66efdb0c95f723d2b06369505eadbca9ff7f721cec1f7437a7fd8cb9c947");
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

        return true;
    }
}
