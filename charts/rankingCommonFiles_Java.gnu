# set terminal png nocrop enhanced size 500,500 fontscale 2.5 font "Times,8" 
# set output 'cities.1.png'
unset border
unset key
set datafile separator ","
set size ratio 1 1,1
set style data lines
unset xtics
unset ytics
Scale(size) = 0.25*sqrt(sqrt(column(size)))
CityName(String,Size) = sprintf("{/=%d %s}", Scale(Size), stringcolumn(String))
GPFUN_Scale = "Scale(size) = 0.25*sqrt(sqrt(column(size)))"
GPFUN_CityName = "CityName(String,Size) = sprintf(\"{/=%d %s}\", Scale(Size), stringcolumn(String))"
save_encoding = "utf8"
x = 0.0
## Last datafile plotted: "cities.dat"
plot 'data1.dat' using 5:4:($2 < 50 ? "-" : CityName(1,2)) with labels
