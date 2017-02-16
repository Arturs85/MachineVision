package com.example.user.machinevision;

/**
 * Created by user on 2016.09.28..
 */
public  class SvitrkodaLasitajs {

static     byte[] nolasitSvitrkodu(int indeks,byte[] dati){
    int platums=0;
    int sakumaPunkts;
    byte[] binaraisSvitrkods = new byte[95];
//meklee paareju melns -> balts
    while(!(!spilgtums(indeks,dati)&& spilgtums(indeks-640,dati))&& indeks >1300){
indeks = indeks- 640;

}
   sakumaPunkts = indeks;
  //   skaita baltaa laukuma platumu
    while(spilgtums(indeks,dati)&& spilgtums(indeks-640,dati)&& indeks >1300){
        platums++;
        indeks = indeks- 640;

    }
       for (int a =0;a<94;a++){
          if((sakumaPunkts-platums*640/2-640*2*platums - a*640)<4300)
              break;
           if(spilgtums((sakumaPunkts-platums*640/2-640*2*platums - a*640),dati))
              binaraisSvitrkods[a]=0;
           else
              binaraisSvitrkods[a]=1;

       }

        return binaraisSvitrkods;
    }
   static boolean spilgtums (int indeks,byte[] dati){
        boolean spilgtums = false;
        if ((dati[indeks-640]+dati[indeks]+dati[indeks+640])/3>126) {
spilgtums = true;
        }
    return spilgtums;
    }


}
