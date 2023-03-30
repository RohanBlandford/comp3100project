import java.net.*;  
import java.io.*;
import java.util.Scanner;  

class MyClientGets{  
public static void main(String args[])throws Exception{  
Socket s=new Socket("localhost",50000);   
DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

String str="";    
String username = System.getProperty("user.name");   

dout.write(("HELO\n").getBytes()); 
dout.flush();  
str=in.readLine();  
// System.out.println("To HELO Server says: "+str); 

dout.write(("AUTH " + username + "\n").getBytes()); 
dout.flush();
str=in.readLine();  
// System.out.println("To AUTH Server says: "+str); 
 
dout.write(("REDY\n").getBytes()); 
dout.flush();
str=in.readLine();  

// System.out.println("To REDY Server says: "+str); 

dout.write(("GETS All\n").getBytes()); 
dout.flush();
str=in.readLine();  
// System.out.println("To GETS All Server says: "+str); 
int nRecs = Integer.parseInt(str.split(" ", 0)[1]);

dout.write(("OK\n").getBytes()); 
dout.flush();

int coreCount = 0; //last read server core count
int largestCoreCount = 0; //largest server core count so far
int numLargest = 0; //number of largest core count servers
String largestServer = null; //name of largest server

for (int i = 0; i < nRecs; i++) {
    str = in.readLine();
    // System.out.println(str);

    String[] arrOfStr = str.split(" ", 0);

    coreCount = Integer.parseInt(arrOfStr[4]);
    // System.out.println("coreCount: " + coreCount);
    if (coreCount == largestCoreCount) {
        numLargest += 1;
    }
    if (coreCount > largestCoreCount) { 
        largestCoreCount = coreCount;
        largestServer = arrOfStr[0];
        numLargest = 1;
        // System.out.println("name & largestCoreCount " + largestServer + " " + largestCoreCount);
    }
}


System.out.println("NumLargest: " + numLargest);
System.out.println("Largest server: " + largestServer); 

dout.write(("OK\n").getBytes()); 
dout.flush();
str=in.readLine();  
// System.out.println("OK: "+str); 

int i = 0;
while (str != null){ 
    dout.write(("REDY\n").getBytes()); 
    dout.flush();
    str=in.readLine();  
    // System.out.println("While loop REDY: "+str); 

    // System.out.println("While loop str: "+str.split(" ", 0)[0]);
    // System.out.println(str.split(" ", 0)[0].equals("JOBN")); 
    
    if (str.split(" ", 0)[0].equals("JOBN") == true) { 
        dout.write(("SCHD " + i + " " + largestServer + "\n").getBytes());
        dout.flush();
        str=in.readLine();  
        // System.out.println("While loop SCHD: "+str); 
        // System.out.println( "SCHD " + i + " " + largestServer);
        i += 1;
        // System.out.println("While loop i: "+ i);  
        }
    else if (str.split(" ", 0)[0].equals("JCPL") == true){
        dout.write(("REDY\n").getBytes()); 
        dout.flush();
        str=in.readLine();  
        // System.out.println("JCPL: "+str); 
    }
    else {
        str = null;
        // System.out.println("While loop else"); 
        }
}

dout.write(("QUIT\n").getBytes()); 
dout.close();  
s.close();  
}}