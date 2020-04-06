-injars       population-simulator-deobf.jar
-outjars      population-simulator.jar

-repackageclasses ''
-overloadaggressively
-optimizationpasses 4
-libraryjars libs

-keep class simulator.Simulator {
  public static void main(java.lang.String[]);
}

-printmapping population-simulator.txt