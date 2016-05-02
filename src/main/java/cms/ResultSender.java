package cms;

import com.google.gson.Gson;
import utils.Status;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

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
    private final String pushUrl;

    public ResultSender(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public <T extends Collection<Status>> void send(T result) throws IOException {
        Gson gson = new Gson();
        System.out.println(gson.toJson(result));

        URL url = new URL(pushUrl + "/utils/saveMatch.php");
        System.out.println("[ResultSender] using " + pushUrl + " as root");

        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        byte[] out = ("data=" + gson.toJson(result)).getBytes();
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("X-Magic", "06da66efdb0c95f723d2b06369505eadbca9ff7f721cec1f7437a7fd8cb9c947");
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.connect();

        OutputStream os = http.getOutputStream();
        os.write(out);
    }
}
