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

import batteri.Batterio;
import utils.Status;
import utils.StatusComparator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Classe che gestisce la simulazione della battaglia tra batteri
 */

public class Simulator {
    /**
     * Numero di batteri da instanziare per ogni tipo
     */
    private static final int NUM_BATTERI = 100;
    private LinkedList<Batterio> batteri;

    private Food food;
    private HashMap<String, Integer> numeroBatteri;
    private HashMap<String, Color> coloreBatteri;
    private ArrayList<String> nomiBatteri;
    /**
     * Terrain sul quale vengono disposti i batteri
     */
    private Terrain terrain;

    /**
     * URL del server http a cui inviare il risultato
     */
    private final String pushUrl;

    protected HashMap<String, Status> stati = new HashMap<>();
    private long startTime;

    /**
     * Colori da assegnare ai sei batteri instanziati
     */
    private static Color[] COLORS = {Color.BLUE, Color.RED, Color.MAGENTA, new Color(117, 4, 128), Color.WHITE, new Color(163, 166, 201)};

    /**
     * Costruttore della classe
     *
     * @param nomiClassiBatteri array dei nomi dei batteri da istanziare
     * @param pushUrl           URL del server http a cui inviare il risultato finale
     */
    public Simulator(String nomiClassiBatteri[], String pushUrl) {
        Vector<Class> classes = loadClasses(nomiClassiBatteri);

        food = new Food(1024, 700);
        inizializzaBatteri(classes);

        terrain = new Terrain(food, batteri, Color.yellow, numeroBatteri);

        this.pushUrl = pushUrl;
    }

    /**
     * Metodo che si occupa di caricare le classi dei batteri che dovranno combattere
     *
     * @param nomiClassiBatteri array dei nomi dei batteri da caricare
     * @return {@link Vector} di {@link Class classi} da istanziare
     */
    private Vector<Class> loadClasses(String[] nomiClassiBatteri) {
        Vector<Class> classes = new Vector<>();
        for (String name : nomiClassiBatteri) {
            try {
                Class add = Class.forName("batteri." + name);
                classes.add(add);

                System.out.println("[CLASSLOADER] Class " + name + " correctly loaded");
            } catch (ClassNotFoundException e) {
                System.err.println("[CLASSLOADER] Could not load class " + name);
            }
        }
        return classes;
    }

    /**
     * Metodo che inizializza i batteri istanziando le classi
     *
     * @param classi Vector di classi da istanziare
     */
    private void inizializzaBatteri(Vector<Class> classi) {
        batteri = new LinkedList<>();
        Random r = new Random();
        numeroBatteri = new HashMap<>();
        coloreBatteri = new HashMap<>();
        nomiBatteri = new ArrayList<>();

        int count = 0;
        for (Class batterio : classi) {
            nomiBatteri.add(batterio.getSimpleName());

            for (int i = 0; i < NUM_BATTERI; i++)
                try {
                    batteri.add((Batterio) batterio.getConstructors()[0].newInstance(r.nextInt(food.getWidth()),
                            r.nextInt(food.getHeight()),
                            COLORS[count], food));

                    numeroBatteri.put(batterio.getSimpleName(), NUM_BATTERI);
                    coloreBatteri.put(batterio.getSimpleName(), COLORS[count]);
                } catch (Exception e) {
                    System.err.println("Non e' stato possibile istanziare il batterio " + batterio.getSimpleName());
                }

            count++;
        }
    }

    /**
     * Metodo che si occupa di concludere la simulazione, ottenendo gli stati dei batteri
     * e lanciando la richiesta HTTP per salvare il risultato
     */

    public void endSimulation() {
        System.out.println("[SIMULATOR] simulazione completata");

        /*
         * Salvo gli stati dei batteri ancora in vita
         */
        Vector<Status> result = new Vector<>();
        for (String nome : nomiBatteri) {
            if (stati.get(nome) == null) {
                Status stato = new Status(nome, false, numeroBatteri.get(nome));
                result.add(stato);
            }
        }

        /*
         * Salvo gli stati dei batteri estinti
         */
        Iterator it = stati.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            result.add((Status) pair.getValue());
            it.remove();
        }

        /*
         * Li ordino secondo il criterio stabilito in {@link StatusComparator}
         */
        Collections.sort(result, new StatusComparator());
        try {
            // Cerco di inviare il risultato al server
            ResultSender sender = new ResultSender(pushUrl + "/utils/saveMatch.php");
            String serverMessage = sender.send(result);

            System.out.println(String.format("Server replied with: %s", serverMessage));
        } catch (IOException e) {
            System.err.println("Non e' stato possibile comunicare il risultato");
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Metodo che inizializza la simulazione lanciando il timer che introduce cibo sul campo e imposta
     * il timestamp dell'inizio della simulazione
     */

    public void setupSimulation() {
        final Timer timerStatus = new Timer();

        TimerTask taskUpdateFood = new TimerTask() {
            @Override
            public void run() {
                terrain.toggleFood();
            }
        };
        timerStatus.schedule(taskUpdateFood, 0, 1000);

        startTime = System.currentTimeMillis();
    }

    /**
     * Metodo che genera l'immagine da visualizzare a schermo o inviare allo stream.
     * <p>
     * L'immagine e' semplicemente l'immagine ottenuta dal {@link Terrain} con aggiunte su schermo le seguenti informazioni:
     * - Tempo della simulazione
     * - Numero di batteri rimasti in vita per ogni tipo di batterio in gara
     *
     * @return l'immagine creata contentente le informazioni
     */

    public BufferedImage paintImage() {
        BufferedImage screen = terrain.paintImage();
        Graphics2D g = screen.createGraphics();

        g.setFont(new Font("Consolas", Font.BOLD, 20));

        int counter = 0;
        int margin = 3;

        long durationSecs = System.currentTimeMillis() - startTime;

        g.setPaint(Color.green);
        // Stampo la stringa della durata
        String duration = String.format("%02d:%02d.%03d", ((durationSecs / 1000) % 3600) / 60, ((durationSecs / 1000) % 60), durationSecs % 1000);
        FontMetrics fm = g.getFontMetrics();
        int x = screen.getWidth() - fm.stringWidth(duration) - 10;
        int y = fm.getHeight();
        // La scrivo a schermo
        g.drawString(duration, x, y);

        // Scrivo quanti batteri sono ancora in vita per ogni tipo
        for (String name : nomiBatteri) {
            g.setPaint(coloreBatteri.get(name));

            // Se si sono estinti lo salvo negli stati
            if (stati.get(name) == null && numeroBatteri.get(name) == 0) {
                long diffSecondi = (System.currentTimeMillis() - startTime) / 1000;
                Status stato = new Status(name, true, diffSecondi);
                stati.put(name, stato);
            }

            String s = name + ": " + numeroBatteri.get(name);
            fm = g.getFontMetrics();
            x = screen.getWidth() - fm.stringWidth(s) - 10;
            y = fm.getHeight();
            g.drawString(s, x, 45 + y * counter + margin * counter);

            counter++;
        }

        return screen;
    }
}
