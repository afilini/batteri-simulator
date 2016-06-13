/*
  Copyright (C) 2016 Alekos Filini (alekos.filini@gmail.com)

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation; either version 2
  of the License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package cms;

import com.google.gson.Gson;
import utils.Status;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;

/**
 * Classe che si occupa di inviare lo stato finale della partita, ovvero una collection di {@link utils.Status}
 * serializzandolo come stringa JSON e inviandola in POST a un server http specificato.
 */

public class ResultSender {
    private final String pushUrl;

    /**
     * Costruttore della classe
     *
     * @param pushUrl url sul server http a cui inviare il risultato
     */

    public ResultSender(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    /**
     * Metodo che invia il risultato
     *
     * @param <T>    tipo generico del risultato da inviare, dev'essere una {@link Collection} o una
     *               sua sottoclasse
     * @param result risultato da inviare, di tipo T
     * @return la risposta del server
     * @throws IOException in caso fallisca l'invio del risultato
     */

    public <T extends Collection<Status>> String send(T result) throws IOException {
        Gson gson = new Gson();
        System.out.println(gson.toJson(result));

        URL url = new URL(pushUrl);
        System.out.println("[ResultSender] using " + pushUrl);

        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        String data = gson.toJson(result);
        byte[] out = ("data=" + URLEncoder.encode(data, "UTF-8")).getBytes();
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        // Header utilizzato per l'autenticazione
        http.setRequestProperty("X-Magic", "06da66efdb0c95f723d2b06369505eadbca9ff7f721cec1f7437a7fd8cb9c947");
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setRequestProperty("Connection", "close");
        http.connect();

        OutputStream os = http.getOutputStream();
        os.write(out);
        os.flush(); // Stranamente sembra non venga flushato il buffer con il disconnect, di conseguenza lo faccio a mano

        Reader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
        StringBuilder response = new StringBuilder();

        for (int read; (read = reader.read()) >= 0; )
            response.append((char) read);

        http.disconnect();

        return response.toString();
    }
}
