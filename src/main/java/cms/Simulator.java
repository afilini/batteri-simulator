package cms;

import batteri.Batterio;
import utils.Status;
import utils.StatusComparator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Copyright (C) Alekos Filini (afilini) - All Rights Reserved
 * <p/>
 * This file is part of cms
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alekos Filini <alekos.filini@gmail.com>, aprile 2016
 */

public class Simulator {
    private static final int NUM_BATTERI = 100;
    private LinkedList<Batterio> batteri;

    private Food food;
    private HashMap<String, Integer> numeroBatteri;
    private HashMap<String, Color> coloreBatteri;
    private ArrayList<String> nomiBatteri;
    private Terrain terrain;

    private final String pushUrl;

    protected HashMap<String, Status> stati = new HashMap<>();
    private long startTime;

    private static Color[] COLORS = {Color.BLUE, Color.RED, Color.MAGENTA, new Color(117, 4, 128), Color.WHITE, new Color(163, 166, 201)};

    public Simulator(String nomiClassiBatteri[], String pushUrl) {
        Vector<Class> classes = loadClasses(nomiClassiBatteri);

        food = new Food(1024, 700);
        inizializzaBatteri(classes);

        terrain = new Terrain(food, batteri, Color.yellow, numeroBatteri);

        this.pushUrl = pushUrl;
    }

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

    public void endSimulation() {
        System.out.println("[SIMULATOR] simulazione completata");

        Vector<Status> result = new Vector<>();
        for (String nome : nomiBatteri) {
            if (stati.get(nome) == null) {
                Status stato = new Status(nome, false, numeroBatteri.get(nome));
                result.add(stato);
            }
        }

        Iterator it = stati.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            result.add((Status) pair.getValue());
            it.remove();
        }

        Collections.sort(result, new StatusComparator());
        try {
            ResultSender sender = new ResultSender(pushUrl);
            sender.send(result);
        } catch (IOException e) {
            System.err.println("Non e' stato possibile comunicare il risultato");
            e.printStackTrace();
        }
    }

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

        for (String name : nomiBatteri) {
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
}
