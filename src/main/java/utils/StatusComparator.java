package utils;

import java.util.Comparator;

/**
 * Copyright (C) Alekos Filini (afilini) - All Rights Reserved
 * <p/>
 * This file is part of utils
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alekos Filini <alekos.filini@gmail.com>, aprile 2016
 */

public final class StatusComparator implements Comparator<Status> {
    @Override
    public int compare(Status o1, Status o2) {
        if (o1.isDead() && o2.isDead())
            return (int) (o2.getTime() - o1.getTime());

        if (o1.isDead())
            return 1;

        if (o2.isDead())
            return -1;

        return (int) (o2.getNum() - o1.getNum());
    }
}
