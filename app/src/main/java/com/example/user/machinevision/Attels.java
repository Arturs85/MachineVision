package com.example.user.machinevision;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by user on 2016.09.18..
 */
public  class Attels {
    boolean[] ekstremi= new boolean[307200];
    boolean[] ekstremiVertikali;
    static HorizontalaLinija linijaH;
byte[] datuMasivs;
    int summa1=0;
    int summa2 = 0;
    int summaY = 0;
    int skaits = 0;
    int vSlieksnis = 10;
    ArrayList<HorizontalaLinija> linijas;
    int spilgtumsDelta = 20;
    //KrasainaBilde krasainaBilde;
   // EkstremuBilde ekstremuBilde;
   // EkstremuBilde vertikaluEkstremuBilde;
   // LinijuBilde linijuBilde;
   // TirasLinijas tiraBilde;
    ArrayList<HorizontalaLinija> mekletHorLinijas(byte[] datuMasivs){
        int kolonna = 0;
        int rinda = 0;
       int sakumaKoordinateX=0;
        int sakumaKoordinateY = 0;
        int linijasGarums=0;
       linijas = new ArrayList<HorizontalaLinija>();
       // if (ekstremi==null){
         ekstremi = atrastEkstremus(datuMasivs,ekstremi);
     //   }
        boolean velkLiniju = false;
        for(int a =0;a<=ekstremi.length-2;a++ ){

            if (ekstremi[a]==false&&ekstremi[a+1]==true){
                velkLiniju = true;
                sakumaKoordinateX = kolonna;
                sakumaKoordinateY = rinda;

            }
            if (ekstremi[a]==true&&ekstremi[a+1]==true){
                velkLiniju = true;
                linijasGarums++;

            }
            if (ekstremi[a]==true&&ekstremi[a+1]==false){
                velkLiniju = false;
                if (linijasGarums>100){
//linijaH = new HorizontalaLinija(sakumaKoordinateX,sakumaKoordinateY,kolonna);
                    linijas.add(new HorizontalaLinija(sakumaKoordinateX,sakumaKoordinateY,kolonna));



                }
                linijasGarums = 0;
            }

            kolonna++;
            if(kolonna>=640){
                rinda++;
                linijasGarums = 0;
                kolonna = 0;
            }
        }

        return linijas;

    }
    //*
    ArrayList<HorizontalaLinija> atlasitLinijas (ArrayList<HorizontalaLinija> visasLinijas)
    {
       for (int a = 0; a<=visasLinijas.size()-2 ;a++) {

           pieskaititBlakusliniju(a,visasLinijas);
           visasLinijas.get(a).sakumaX=summa1/skaits;
           visasLinijas.get(a).beiguX=summa2/skaits;
visasLinijas.get(a).y = summaY/skaits;

          /// / int pieskaitamais = 1;

       }
        return visasLinijas;

    }
        void pieskaititBlakusliniju(int indeks,ArrayList<HorizontalaLinija> visasLinijas){
        int pieskaitamais = 0;
            skaits=1;
            summa1=visasLinijas.get(indeks).sakumaX;
            summa2 = visasLinijas.get(indeks).beiguX;
            summaY = visasLinijas.get(indeks).y;
           //ignoreet tajaa pat rindaa esossaas liinijas
            do{
                pieskaitamais++;


        }while (visasLinijas.get(indeks).y==visasLinijas.get(indeks+pieskaitamais).y&& indeks+pieskaitamais<visasLinijas.size()-2);


        // ja ir blakusliinija
       // ArrayList<HorizontalaLinija> visasLinijas ;

        if(visasLinijas.get(indeks+pieskaitamais).y <= visasLinijas.get(indeks).y+2
                &&visasLinijas.get(indeks+pieskaitamais).sakumaX<visasLinijas.get(indeks).beiguX
            &&visasLinijas.get(indeks+pieskaitamais).beiguX>visasLinijas.get(indeks).sakumaX
        && indeks+pieskaitamais<visasLinijas.size()-2  )
        {
            // indeks++;
                summa1 =summa1 +visasLinijas.get(indeks+pieskaitamais).sakumaX;
                summa2 = summa2 + visasLinijas.get(indeks+pieskaitamais).beiguX;
                summaY = summaY + visasLinijas.get(indeks+pieskaitamais).y;
                skaits++;


                pieskaititBlakusliniju(indeks + pieskaitamais + 1, visasLinijas);
                visasLinijas.remove(indeks+pieskaitamais);

            return;
        }
        else

            return;

    }
   //*/
    boolean[] atrastVisusEkstremus(byte[] datuMasivs){
        ekstremi  = new boolean[307200];
        ekstremi =    atrastEkstremus(datuMasivs,ekstremi);
        ekstremi = atrastVertEkstremus(datuMasivs,ekstremi);
        return ekstremi;
    }
    boolean[] atrastEkstremus(byte[] dati,boolean[] ekstremi){
       // boolean[] ekstremi =   new boolean[245760];
        for (int a = 0; a < 307200-2561;  a = a + 1) {
            if(noteiktMalu(dati[a],dati[a+640],dati[a+640*2],dati[a+640*3]))
            {
                ekstremi[a]= true;
            }
            else{
            if(noteiktMalu(dati[a],dati[a+1],dati[a+2],dati[a+3]))
            {
                ekstremi[a]= true;
            }
            else
                ekstremi[a]=false;
            }

        }
        return ekstremi;
    }
    boolean[][] atrastEkstremus160x120(byte[] dati,boolean[][] ekstremi){

            //boolean[][] ekstremi = new boolean[160][120];
            int x = 0;
            int y = 0;

            for (int a = 0; a < 307200 - 2564; a = a + 4) {
                if (x >= 159) {
                    x = 0;
                    y++;
                    a += 3 * 640 + 4;
                }
                if (noteiktMalu(dati[a],dati[a+640*3])) {
                    ekstremi[x][y] = true;

                } else {
                    if (noteiktMalu(dati[a],dati[a+4]))
                        ekstremi[x][y] = true;
                    else
                        ekstremi[x][y] = false;

                }

                x++;
            }
            return ekstremi;
        }
    //returns masiivu ar celjam nederigiem punktiem izmantojot kadra apaksas krasu histogramu
    Canvas atrastVirsmuLoRes(Canvas canvas,Paint paint, int[] hist,byte[] data){
        int rinda=1;
        int kolonna=1;
        int uvRinda=480;
        int vKolonna=1;
        Paint caursp = new Paint();
        caursp.setAlpha(0);
        for (int a = 0; a < 307200-2000; a = a + 2) { // 640x384


            if (kolonna >= 641) {
                rinda=rinda+1;
               a= a + 640;
                kolonna = 1;
                vKolonna = 1;
            }


            int v = data[((rinda+480) * 640 + kolonna-1) ];
            if(v < 0) v += 127; else v -= 128;

            int u = data[((rinda+480) * 640 + (kolonna) )];
            if(u < 0) u += 127; else u -= 128;

            if(!(Math.abs(v)<2 && Math.abs(u)<2)){
                double hue = Math.atan2(v, u);
                hue = Math.toDegrees(hue);
                int hue2 = (int)hue;
                if (hue2<0)
                    hue2 = 360 + hue2;
                hue2 = hue2-90;
                if(hue2<0)
                    hue2 = 360 + hue2;
                if(hue2 >361){
                    System.out.println(hue2);
                    hue2 = 360;
                }
                // g.setColor(Color.black);

                //g.setColor(yuvToRGB(y, v, u));
                if(hist[hue2]< vSlieksnis)
                    canvas.drawRect(kolonna,2*rinda,kolonna+2,2*rinda+2, paint);

                    // masivs[a]=true;
                else
                    canvas.drawRect(kolonna, 2*rinda, kolonna + 1, 2*rinda + 1, caursp);
                // masivs[a]=false;

                // g.drawLine(kolonna, rinda, kolonna , rinda );
                // g.drawLine(400-rinda,kolonna, 401-rinda,kolonna+1);
            }
            kolonna = kolonna + 2;


        }
        return canvas; //masivs;
    }
//returns masiivu ar celjam nederigiem punktiem izmantojot kadra apaksas krasu histogramu
    Canvas atrastVirsmu(Canvas canvas,Paint paint, int[] hist,byte[] data){
        int rinda=1;
        int kolonna=1;
        int uvRinda=480;
        int vKolonna=1;
        Paint caursp = new Paint();
        caursp.setAlpha(0);
        for (int a = 0; a < 307200-640; a = a + 1) { // 640x384


            if (kolonna >= 641) {
                rinda=rinda+1;
                if ((rinda & 1) == 1){ // paara rindas gadiijumaa
                    uvRinda++;
                    //System.out.println(uvRinda+" "+vKolonna+" "+rinda);
                }
                kolonna = 1;
                vKolonna = 1;
            }


            int v = data[(uvRinda * 640 + vKolonna-1) ];
            if(v < 0) v += 127; else v -= 128;

            int u = data[(uvRinda * 640 + (vKolonna) )];
            if(u < 0) u += 127; else u -= 128;

            if(!(Math.abs(v)<2 && Math.abs(u)<2)){
                double hue = Math.atan2(v, u);
                hue = Math.toDegrees(hue);
                int hue2 = (int)hue;
                if (hue2<0)
                    hue2 = 360 + hue2;
                hue2 = hue2-90;
                if(hue2<0)
                    hue2 = 360 + hue2;
                if(hue2 >361){
                    System.out.println(hue2);
                    hue2 = 360;
                }
               // g.setColor(Color.black);

                //g.setColor(yuvToRGB(y, v, u));
                if(hist[hue2]< vSlieksnis)
                    canvas.drawPoint(kolonna, rinda, paint);

                // masivs[a]=true;
                else
                    canvas.drawPoint(kolonna, rinda, caursp);
                   // masivs[a]=false;

                   // g.drawLine(kolonna, rinda, kolonna , rinda );
                // g.drawLine(400-rinda,kolonna, 401-rinda,kolonna+1);
            }
            kolonna = kolonna + 1;
            if((kolonna & 1)==1)
                vKolonna = vKolonna+2;

        }
        return canvas; //masivs;
    }
    // returns histogrammu, masiva indeksi atbilst hue graadiem
    int[] atrastKrasuHistogrammu(int[] hist,byte[] data){
        int rinda = 0;
        int kolonna =0;

        for (int i = 0; i < hist.length; i++) { //izdzēš iepr datus

            hist[i]= 0;
        }
        for(int i = (680*640)+100;i < 720*640-200;i+=2){

            if(kolonna >= 440){
                kolonna = 0;
                rinda++;
                i += 200;
            }
            int u  = (data[i]);
            int v = data[i+1];
            if(u < 0) u += 127; else u -= 128;
            if(v < 0) v += 127; else v -= 128;
            double hue = Math.atan2(u, v);
            hue = Math.toDegrees(hue);
            int hue2 = (int)hue;
            if (hue2<0)
                hue2 = 360 + hue2;
            hue2 = hue2-90;
            if(hue2<0)
                hue2 = 360 + hue2;

//data[i] = 0;
            //if(y < 0) y += 255;
            hist[hue2]++;

            kolonna+=2;
        }
        return hist;
    }

    boolean[][] atrastKrasuLaukumus(byte[] dati,boolean[][] ekstremi){

        //boolean[][] ekstremi = new boolean[160][120];
        int x = 0;
        int y = 0;

        for (int a = 307200; a < 460800-1400; a = a + 2) {
            if (x >= 640) {
                x = 0;
                y+=2;
               // a += 3 * 640 + 4;
            }
           int v = dati[a];
            if(v < 0) v += 127; else v -= 128;
            if (v>vSlieksnis) {
                ekstremi[x][y] = true;
                ekstremi[x+1][y] = true;
                ekstremi[x+1][y+1] = true;
                ekstremi[x][y+1] = true;
            }
else{
                ekstremi[x][y] = false;
                ekstremi[x+1][y] = false;
                ekstremi[x+1][y+1] = false;
                ekstremi[x][y+1] = false;

            }
            x+=2;
        }
        return ekstremi;
    }
    boolean[] atrastVertEkstremus(byte[] dati,boolean[] ekstremi){
       // boolean[] ekstremiVertikali =   new boolean[245760];
        for (int a = 0; a < 307200-4;  a = a + 1) {
            if(noteiktMalu(dati[a],dati[a+1],dati[a+2],dati[a+3]))
            {
                ekstremi[a]= true;
            }
            //else
              //  ekstremiVertikali[a]=false;

        }
        return ekstremi;
    }
    boolean noteiktMalu(int a,int a1,int a2,int a3){
        boolean irMala = false;
        if(a+a1<a2+a3-spilgtumsDelta||a+a1>a2+a3+spilgtumsDelta)
            irMala = true;


        return irMala;
    }
    boolean noteiktMalu(int a,int a1){
        boolean irMala = false;
        if(a<a1-spilgtumsDelta||a>a1+spilgtumsDelta)
            irMala = true;


        return irMala;
    }
    boolean[] atrastOtrasKartasEkstremus(boolean[] pirmasKEkst) {
        for (int a = 0; a < pirmasKEkst.length - 641; ++a) {
            if (pirmasKEkst[a] == false && pirmasKEkst[a + 1] == true)
                pirmasKEkst[a] = true;
            else {
                if (pirmasKEkst[a] == false && pirmasKEkst[a + 640] == true)
                    pirmasKEkst[a] = true;
                else
                    pirmasKEkst[a] = false;

            }

        }
        return pirmasKEkst;
    }
//Attels apakssklase
 class HorizontalaLinija {
    int sakumaX;
    int y;
    int beiguX;

    HorizontalaLinija(int sakumaX,int y,int beiguX){
        this.sakumaX = sakumaX;
        this.y = y;
        this.beiguX = beiguX;

    }
    int getGarums(){
        int garums=0;
        garums = beiguX-sakumaX;
        return garums;
    }
}


}
