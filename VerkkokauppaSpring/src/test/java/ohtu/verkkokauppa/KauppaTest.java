/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ohtu.verkkokauppa;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 *
 * @author pyykkomi
 */
public class KauppaTest {

    private Pankki pankki;
    private Viitegeneraattori viite;
    private Varasto varasto;
    
    @Before
    public void setUp() {
        pankki = mock(Pankki.class);
        viite = mock(Viitegeneraattori.class);
        varasto = mock(Varasto.class);
    }
    
    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {
        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);
        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10); 
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        // sitten testattava kauppa 
        Kauppa k = new Kauppa(varasto, pankki, viite);              

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(),anyInt());   
        // toistaiseksi ei välitetty kutsussa käytetyistä parametreista
    }
    
    @Test
    public void ostoksenPaaytyttyaPankinMetodiTilisiirtoSaaOikeatArvotYhdellaTuotteella() {
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        
        when(viite.uusi()).thenReturn(42);
        when(varasto.saldo(1)).thenReturn(10); 
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");
        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), anyString(), eq(5));
                
    }
    
    @Test
    public void ostoksenPaaytyttyaPankinMetodiTilisiirtoSaaOikeatArvotMonellaEriTuotteella() {
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        
        when(viite.uusi()).thenReturn(42);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.saldo(2)).thenReturn(10);         
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(1, "munat", 10));
        
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "12345");
        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), anyString(), eq(15));
                
    }
    
    @Test
    public void ostoksenPaaytyttyaPankinMetodiTilisiirtoSaaOikeatArvotMonellaSamallaTuotteella() {
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        
        when(viite.uusi()).thenReturn(42);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");
        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), anyString(), eq(10));
                
    }
    
    @Test
    public void ostoksenPaaytyttyaPankinMetodiTilisiirtoSaaOikeatArvotKunTuoteOnLoppu() {
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        
        when(viite.uusi()).thenReturn(42);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.saldo(2)).thenReturn(0);         
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(1, "munat", 10));
        
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "12345");
        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), anyString(), eq(5));
                
    }
    
    @Test
    public void aloitaAsiointiNollaaEdellisenOstoksen() {
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        
        when(viite.uusi()).thenReturn(42);
        when(varasto.saldo(1)).thenReturn(1).thenReturn(0);   
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.aloitaAsiointi();
        k.lisaaKoriin(1);        
        k.tilimaksu("pekka", "12345");
        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), anyString(), eq(0));
                
    }
    
    @Test
    public void pyydetaanUusiViite() {
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        
        when(viite.uusi()).thenReturn(42).thenReturn(43).thenReturn(44);
        when(varasto.saldo(1)).thenReturn(3).thenReturn(2).thenReturn(1);   
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");
        verify(viite, times(1)).uusi();
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");
        verify(viite, times(2)).uusi();
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");
        verify(viite, times(3)).uusi();
        
    }
    
    @Test
    public void poistaKoristaToimii() {
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        
        when(viite.uusi()).thenReturn(42);
        when(varasto.saldo(1)).thenReturn(10).thenReturn(9); 
        Tuote t = new Tuote(1, "maito", 5);
        when(varasto.haeTuote(1)).thenReturn(t);
        
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        k.poistaKorista(1);        
        k.tilimaksu("pekka", "12345");
        verify(varasto, times(1)).palautaVarastoon(eq(t));
        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), anyString(), eq(5));
                
    }
}
