# SPL_HW3 - 318416575_207181629
How to run TPCMain:
Server:
mvn clean
mvn compile
mvn exec:java -Dexec.mainClass=bgu.spl.net.BGSServer.TPCMain -Dexec.args=7777
Client:
make clean
make
bin/BGSclient "127.0.0.1" 7777

How to run ReactorMain
Server:
mvn clean
mvn compile
mvn exec:java -Dexec.mainClass="bgu.spl.net.BGSServer.ReactorMain" -Dexec.args="7777 5"
Client:
make clean
make
bin/BGSclient "127.0.0.1" 7777

Command lines for examples:
REGISTER tal g 11-04-1997
LOGIN omer sss 17-03-2000
LOGOUT
FOLLOW 0 omer
PM omer hey!
POST hey to all my followers and @gev
LOGSTAT
STAT
BLOCK yarden

-Filtered set of words stroe in: bgu.spl.net.impl.DataHolder.java 
in line 18 -  private final List<String> BannedWords = new LinkedList<String>();
