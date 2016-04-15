package batteri;

import cms.Food;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;

public class PepeTheFrog extends Batterio {
    private static final LinkedList<ZonaFavorevole> zoneMigliori = new LinkedList<>();
    private static boolean[][] staMangiando;

    private static int[] potenzeDue = {1, 2, 4, 8, 16, 32, 64, 128};

    private static final int DIFF_AVVICINAMENTO_ZONA = 3;
    private static final int LIVELLI_RICERCA = 4;

    private static final int VALORE_INIZIALE_ZONE = 50;

    public static final int RAGGIO_INIZIALE = 20;
    private static final int RAGGIO_MASSIMO = 80;

    private final int RAGGIO_MIGLIORI;
    private final int PASSO_ESPLORAZIONE;

    private int direzione = -1;
    private int mangia = 0;

    private final int generazione;

    private ZonaFavorevole zona = null;

    private static Random random = new Random();

    public PepeTheFrog(int x, int y, Color c, Food f) {
        this(x, y, c, f, 0, 1, 75);

        if (staMangiando == null) // Il primo crea la matrice
            staMangiando = new boolean[food.getWidth()][food.getHeight()];
    }

    private PepeTheFrog(int x, int y, Color c, Food f, int generazione, int passo_esp, int raggio_migliori) {
        super(x, y, c, f);
        this.generazione = generazione;

        if (Math.random() > 0.9) {
            passo_esp += (int) (Math.random() * 3) - 1;
            passo_esp = Math.max(Math.min(passo_esp, 2), 1);
        }

        PASSO_ESPLORAZIONE = passo_esp;

        if (Math.random() > 0.85) {
            raggio_migliori += (int) (Math.random() * 10) - 5;
            raggio_migliori = Math.max(Math.min(raggio_migliori, 80), 60);
        }

        RAGGIO_MIGLIORI = raggio_migliori;
    }

    public int dist(int x1, int y1, int x2, int y2) {
        int diffX = x1 - x2;
        int diffY = y1 - y2;
        return (int) Math.round(Math.sqrt((diffX * diffX + diffY * diffY)));
    }

    private Risultato quadranteMigliore() {
        for (int i = 0; i < LIVELLI_RICERCA; i++) {
            int startX = Math.max(0, getX() - i);
            int startY = Math.max(0, getY() - i);

            int endX = Math.min(getX() + i, food.getWidth() - 1);
            int endY = Math.min(getY() + i, food.getHeight() - 1);

            try {
                for (int j = startX; j < endX; j++)
                    if (ControllaCibo(j, startY) && !staMangiando[j][startY]) {
                        direzione = (int) Math.floor((Math.toDegrees(Math.atan2(startY - y, x - j)) + 180) / 360 * 8);
                        return new Risultato(true, j, startY);
                    }

                for (int j = startX; j < endX; j++)
                    if (ControllaCibo(j, endY) && !staMangiando[j][endY]) {
                        direzione = (int) Math.floor((Math.toDegrees(Math.atan2(endY - y, x - j)) + 180) / 360 * 8);
                        return new Risultato(true, j, endY);
                    }

                for (int j = startY + 1; j < endY - 1; j++)
                    if (ControllaCibo(startX, j) && !staMangiando[startX][j]) {
                        direzione = (int) Math.floor((Math.toDegrees(Math.atan2(j - y, x - startX)) + 180) / 360 * 8);
                        return new Risultato(true, startX, j);
                    }

                for (int j = startY + 1; j < endY - 1; j++)
                    if (ControllaCibo(endX, j) && !staMangiando[endX][j]) {
                        direzione = (int) Math.floor((Math.toDegrees(Math.atan2(j - y, x - endX)) + 180) / 360 * 8);
                        return new Risultato(true, endX, j);
                    }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }

        return new Risultato(false, 0, 0);
    }

    private ZonaFavorevole inZonaMigliore() {
        for (ZonaFavorevole z : zoneMigliori)
            if (dist(z.centroX, z.centroY, getX(), getY()) <= z.raggio && z.presenti < 15)
                return z;

        return null;
    }

    private void vaiAZona(ZonaFavorevole interno) {
        if (zona != null) // Mi rimuovo dalla vecchia zona
            zona.presenti--;

        if (interno.presenti > 15 && mangia > 30 && Math.random() >= 0.90) {
            int centroX = food.getWidth() / 2;
            int centroY = food.getHeight() / 2;

            int dirX = centroX - getX() < 0 ? -1 : 1;
            int dirY = centroY - getY() < 0 ? -1 : 1;

            mangia = 0;

            x = (int) (x + dirX * RAGGIO_MIGLIORI * 1.3);
            y = (int) (y + dirY * RAGGIO_MIGLIORI * 1.3);

            zona = null;

            return;
        }

        zona = interno;
        zona.presenti++;

        int dx = (int) (Math.random() * 2 * DIFF_AVVICINAMENTO_ZONA) - DIFF_AVVICINAMENTO_ZONA;
        int dy = (int) (Math.random() * 2 * DIFF_AVVICINAMENTO_ZONA) - DIFF_AVVICINAMENTO_ZONA;

        int finalX = interno.centroX + dx;
        int finalY = interno.centroY + dy;

        muoviControllaLimiti(finalX, finalY);
    }

    private void muoviControllaLimiti(int finalX, int finalY) {
        int startX = finalX;
        int startY = finalY;

        if (finalX < 0)
            finalX = 0;
        else if (finalX >= food.getWidth())
            finalX = food.getWidth() - 1;

        if (finalY < 0)
            finalY = 0;
        else if (finalY >= food.getHeight())
            finalY = food.getHeight() - 1;

        x = finalX;
        y = finalY;

        if (finalX != startX || finalY != startY) {
            direzione = (direzione + 12) % 8;
        }
    }


    private void muoviDirezione() {
        switch (direzione) {
            case -1:
                nuovaDirezione(); // Niente break per non sprecare il ciclo
            case 0:
                muoviControllaLimiti(getX() + PASSO_ESPLORAZIONE, getY());
                break;

            case 1:
                muoviControllaLimiti(getX() + PASSO_ESPLORAZIONE, getY() - PASSO_ESPLORAZIONE);
                break;

            case 2:
                muoviControllaLimiti(getX(), getY() - PASSO_ESPLORAZIONE);
                break;

            case 3:
                muoviControllaLimiti(getX() - PASSO_ESPLORAZIONE, getY() - PASSO_ESPLORAZIONE);
                break;

            case 4:
                muoviControllaLimiti(getX() - PASSO_ESPLORAZIONE, getY());
                break;

            case 5:
                muoviControllaLimiti(getX() - PASSO_ESPLORAZIONE, getY() + PASSO_ESPLORAZIONE);
                break;

            case 6:
                muoviControllaLimiti(getX(), getY() + PASSO_ESPLORAZIONE);
                break;

            case 7:
                muoviControllaLimiti(getX() + PASSO_ESPLORAZIONE, getY() + PASSO_ESPLORAZIONE);
                break;

            default:
                direzione = -1;
                nuovaDirezione();
        }
    }

    private int rimuoviX = -1, rimuoviY = -1;
    private int turniDigiuno = 0;

    @Override
    protected void Sposta() {
        if (rimuoviX != -1) {
            staMangiando[rimuoviX][rimuoviY] = false; // Segno che non sto più mangiando
            rimuoviX = -1;
        }

        Risultato ris = quadranteMigliore();
        ZonaFavorevole interno = inZonaMigliore();

        if (interno != null) {
            try {
                interno.contoFood -= potenzeDue[turniDigiuno] - 1;
                if (turniDigiuno == 0)
                    interno.raggio = Math.min(interno.raggio + 6, RAGGIO_MASSIMO);
                else
                    interno.raggio = Math.max(interno.raggio - potenzeDue[turniDigiuno] / 5, RAGGIO_INIZIALE);

            } catch (ArrayIndexOutOfBoundsException e) {}

            if (interno.contoFood <= 0) {
                zoneMigliori.remove(interno);
                interno = null;
                turniDigiuno = 0;
            }
        }

        if (ris.found) {
            mangia++;
            x = ris.dx;
            y = ris.dy;

            staMangiando[x][y] = true;
            rimuoviX = x;
            rimuoviY = y;

            if (interno == null) {
                ZonaFavorevole z = new ZonaFavorevole(ris.dx, ris.dy, VALORE_INIZIALE_ZONE);
                zoneMigliori.add(z);
            } else {
                turniDigiuno = 0;
            }
        } else {
            if (interno != null) {
                turniDigiuno++;
                vaiAZona(interno);
            } else { // Esplorazione
                if (direzione == -1)
                    nuovaDirezione();

                muoviDirezione();
            }
        }
    }

    private void nuovaDirezione() {
        if (direzione != -1) // Cerco una direzione vicina
            direzione = (direzione + 8 + (int) (Math.random() * 5) - 2) % 8;
        else
            direzione = (int) (Math.random() * 8);
    }

    @Override
    public Batterio Clona() {
        return new PepeTheFrog(random.nextInt(food.getWidth()), random.nextInt(food.getHeight()), colore, food, generazione + 1, PASSO_ESPLORAZIONE, RAGGIO_MIGLIORI);
    }

    @Override
    public String toString() {
        return "PepeTheFrog";
    }

}

class ZonaFavorevole {
    protected final int centroX;
    protected final int centroY;

    protected int contoFood;
    protected int presenti = 0;

    protected int raggio = PepeTheFrog.RAGGIO_INIZIALE;

    protected ZonaFavorevole(int x, int y, int conto) {
        centroX = x;
        centroY = y;
        contoFood = conto;
    }
}

class Risultato {
    protected final boolean found;

    protected final int dx;
    protected final int dy;

    public Risultato(boolean found, int dx, int dy) {
        this.found = found;
        this.dx = dx;
        this.dy = dy;
    }
}