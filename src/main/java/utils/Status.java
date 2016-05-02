package utils;

/**
 * Copyright (C) Alekos Filini (afilini) - All Rights Reserved
 * <p/>
 * This file is part of cms
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alekos Filini <alekos.filini@gmail.com>, marzo 2016
 */

public class Status {
    protected boolean dead;
    protected long time;
    protected long num;
    protected String name;

    public Status(String name, boolean dead, long n) {
        this.name = name;
        this.dead = dead;
        if (dead)
            this.time = n;
        else
            this.num = n;
    }

    @Override
    public String toString() {
        return String.format("%s %b %d", name, dead, dead ? time : num);
    }

    public boolean isDead() {
        return dead;
    }

    public long getTime() {
        return time;
    }

    public long getNum() {
        return num;
    }

    public String getName() {
        return name;
    }
}
