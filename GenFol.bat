echo -----------------------------
echo GenFol
set classpath0=%classpath%
set classpath=%classpath%;C:\ddc\java
cd fol
javac *.java
cd ..
set classpath=%classpath0%
echo Finished





