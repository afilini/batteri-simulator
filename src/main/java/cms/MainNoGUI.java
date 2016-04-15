package cms;

import batteri.Batterio;
import com.google.gson.Gson;
import com.sun.tools.internal.ws.wsdl.document.jaxws.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.Exception;
import java.util.*;

/**
 * Copyright (C) Alekos Filini (afilini) - All Rights Reserved
 * <p/>
 * This file is part of cms
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alekos Filini <alekos.filini@gmail.com>, marzo 2016
 */

public class MainNoGUI {

    private static int NUM_BATTERI = 100;

    private LinkedList<Batterio> batteri;
    private Food food;
    private HashMap<String, Integer> numeroBatteri;
    private HashMap<String, Color> coloreBatteri;
    private ArrayList<String> nomiBatteri;
    private Terrain terrain;

    protected HashMap<String, Status> stati = new HashMap<>();
    private long startTime;

    private VideoStreamer streamer;

    private static Color[] COLORS = {Color.BLUE, Color.RED, Color.MAGENTA, new Color(117,4,128), Color.WHITE, new Color(163,166,201)};

    public MainNoGUI(String nomiClassiBatteri[]) {
        Vector<Class> classes = new Vector<>();
        for (String name: nomiClassiBatteri) {
            try {
                Class add = Class.forName("batteri." + name);
                classes.add(add);

                System.out.println("[CLASSLOADER] Class " + name + " correctly loaded");
            } catch (ClassNotFoundException e) {
                System.err.println("[CLASSLOADER] Could not load class " + name);
            }
        }

        food = new Food(1024, 700);
        inizializzaBatteri(classes);

        terrain = new Terrain(food, batteri, Color.yellow, numeroBatteri);

        streamer = new VideoStreamer(this);
        streamer.start();
    }

    private void inizializzaBatteri(Vector<Class> classi) {
        batteri = new LinkedList<>();
        Random r = new Random();
        numeroBatteri = new HashMap<>();
        coloreBatteri = new HashMap<>();
        nomiBatteri = new ArrayList<>();

        int count = 0;
        for (Class batterio: classi) {
            for (int i = 0; i < NUM_BATTERI; i++)
                try {
                    batteri.add((Batterio) batterio.getConstructors()[0].newInstance(r.nextInt(food.getWidth()),
                            r.nextInt(food.getHeight()),
                            COLORS[count], food));
                } catch (Exception e) {}

            numeroBatteri.put(batterio.getSimpleName(), NUM_BATTERI);
            coloreBatteri.put(batterio.getSimpleName(), COLORS[count]);
            nomiBatteri.add(batterio.getSimpleName());

            count++;
        }
    }

    public void setStartTime () {
        final Timer timerStatus = new Timer();

        TimerTask taskUpdateFood = new TimerTask() {
            @Override
            public void run() {
                terrain.toggleFood();
            }
        };
        timerStatus.schedule(taskUpdateFood, 0, 1000);

        TimerTask taskStatus;
        taskStatus = new TimerTask() {
            @Override
            public void run() {
                if (!streamer.isAlive()) {
                    System.out.println("[MAIN] simulazione completata");

                    timerStatus.cancel();
                    timerStatus.purge();

                    Vector<Status> result = new Vector<>();
                    for (String nome: nomiBatteri) {
                        if (stati.get(nome) == null) {
                            Status stato = new Status(nome, false, numeroBatteri.get(nome));
                            result.add(stato);
                        }
                    }

                    Comparator<Status> comparator = new Comparator<Status>() {
                        @Override
                        public int compare(Status o1, Status o2) {
                            if (o1.dead && o2.dead)
                                return (int) (o2.time - o1.time);

                            if (o1.dead)
                                return 1;

                            if (o2.dead)
                                return -1;

                            return (int) (o2.num - o1.num);
                        }
                    };


                    Iterator it = stati.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        result.add((Status) pair.getValue());
                        it.remove();
                    }

                    Collections.sort(result, comparator);
                    try {
                        ResultSender.send(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for (Status s: result)
                        System.out.println(s);

                }
            }
        };
        timerStatus.schedule(taskStatus, 500, 500);

        startTime = System.currentTimeMillis();
    }

    public BufferedImage paintImage() {
        BufferedImage screen = terrain.paintImage();
        Graphics2D g = screen.createGraphics();

        g.setFont(new Font("Consolas", Font.BOLD, 20));

        int counter = 0;
        int margin = 3;

        long durationSecs = System.currentTimeMillis() - startTime;

        g.setPaint(Color.green);
        String duration = String.format("%02d:%02d.%03d", ((durationSecs / 1000) % 3600) / 60, ((durationSecs / 1000) % 60), durationSecs % 1000);
        FontMetrics fm = g.getFontMetrics();
        int x = screen.getWidth() - fm.stringWidth(duration) - 10;
        int y = fm.getHeight();
        g.drawString(duration, x, y);

        for (String name: nomiBatteri) {
            g.setPaint(coloreBatteri.get(name));

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

    public static void main (String args[]) {
        new MainNoGUI(args);
    }
}
