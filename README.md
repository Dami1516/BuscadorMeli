# BuscadorMeli
Buscador desarrollado para el programa Mercadolibre Mobile Candidate


Se decidió utilizar las siguientes dependencias

implementation 'com.android.support.constraint:constraint-layout:1.1.3':
  Se utilizaron "constraint layout" debido a su facilidad para hacer activity´s que se comporten bien ante varias pantallas y resoluciones. También a su poder
  de poder poner los componentes relativos a otros componentes y de ésta forma lograr diseños responsive.
  
implementation 'com.android.support:recyclerview-v7:28.0.0':
  Las RecyclerView hacen uso del patrón ViewHolder. Esta es una de las principales diferencias entre ListView y RecyclerView. El RecyclerView es mucho más potente,
  flexible y una mejora importante sobre ListView.
  Hace las cosas un poco más complejas en RecyclerView pero muchos de los problemas que enfrentamos en el ListView se resuelven de manera eficiente.

implementation 'com.android.support:cardview-v7:28.0.0':
  Se decidió utilizar CardView para mostrar los elemenos resultantes de la busqueda de una manera mas ordenada, atractiva y eficiente.
