package ohtu.intjoukkosovellus;

import static java.lang.System.arraycopy;

public class IntJoukko {

    public final static int KAPASITEETTI = 5, // aloitustalukon koko
            OLETUSKASVATUS = 5;  // luotava uusi taulukko on 
    // näin paljon isompi kuin vanha
    private int kasvatuskoko;     // Uusi taulukko on tämän verran vanhaa suurempi.
    private int[] taulukko;      // Joukon luvut säilytetään taulukon alkupäässä. 
    private int alkioidenLkm;    // Tyhjässä joukossa alkioiden_määrä on nolla. 

    public IntJoukko() {
        this(KAPASITEETTI, OLETUSKASVATUS);
    }

    public IntJoukko(int kapasiteetti) {
        this(kapasiteetti, OLETUSKASVATUS);
    }

    public IntJoukko(int kapasiteetti, int kasvatuskoko) {
        if (kapasiteetti < 0) {
            throw new IndexOutOfBoundsException("Kapasiteetti ei voi olla negatiivinen");//heitin vaan jotain :D
        }
        if (kasvatuskoko < 0) {
            throw new IndexOutOfBoundsException("Kasvatuskoko ei voi olla negatiivinen");//heitin vaan jotain :D
        }
        taulukko = new int[kapasiteetti];
        alkioidenLkm = 0;
        this.kasvatuskoko = kasvatuskoko;

    }

    public boolean lisaa(int luku) {
        if (kuuluu(luku)) {
            return false;
        }
        if (alkioidenLkm % taulukko.length == 0) {
            kasvataTaulukkoa(kasvatuskoko);
        }
        
        taulukko[alkioidenLkm] = luku;
        alkioidenLkm++;
        return true;
    }

    public void lisaaMonta(int[] luvut) {
        for (int luku : luvut) {
            lisaa(luku);
        }
    }

    public boolean kuuluu(int luku) {
        for (int i = 0; i < alkioidenLkm; i++) {
            if (luku == taulukko[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean poista(int luku) {
        for (int i = 0; i < alkioidenLkm; i++) {
            if (luku == taulukko[i]) {
                arraycopy(taulukko, i + 1, taulukko, i, alkioidenLkm - i);
                alkioidenLkm--;
                return true;
            }
        }
        return false;
    }

    public boolean poistaMonta(int[] luvut) {
        boolean palautus = false;
        for (int luku : luvut) {
            palautus |= poista(luku);
        }
        return palautus;
    }
    
    public int mahtavuus() {
        return alkioidenLkm;
    }

    private void kasvataTaulukkoa(int kasvatus) {
        int[] uusi = new int[taulukko.length + kasvatus];
        arraycopy(taulukko, 0, uusi, 0, taulukko.length);
        taulukko = uusi;
    }

    @Override
        public String toString() {
        String tuotos = "{";
        int idx = 0;
        while (idx < alkioidenLkm) {
            tuotos += taulukko[idx] + (idx < alkioidenLkm - 1 ? ", " : "");
            idx++;
        }
        tuotos += "}";
        return tuotos;
    }

    public int[] toIntArray() {
        int[] taulu = new int[alkioidenLkm];
        System.arraycopy(taulukko, 0, taulu, 0, taulu.length);
        return taulu;
    }

    public static IntJoukko yhdiste(IntJoukko a, IntJoukko b) {
        IntJoukko palautus = new IntJoukko();
        palautus.lisaaMonta(a.toIntArray());
        palautus.lisaaMonta(b.toIntArray());
        return palautus;
    }

    public static IntJoukko leikkaus(IntJoukko a, IntJoukko b) {
        IntJoukko palautus = new IntJoukko();
        int[] aTaulu = a.toIntArray();
        for (int i = 0; i < aTaulu.length; i++) {
            if (b.kuuluu(aTaulu[i])) {
                palautus.lisaa(aTaulu[i]);
            }
        }
        return palautus;

    }

    public static IntJoukko erotus(IntJoukko a, IntJoukko b) {
        IntJoukko palautus = new IntJoukko();
        palautus.lisaaMonta(a.toIntArray());
        palautus.poistaMonta(b.toIntArray());

        return palautus;
    }

}
