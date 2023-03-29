import java.net.*;  
import java.io.*;
import java.util.Scanner;  

class MyClient{  
public static void main(String args[])throws Exception{  
Socket s=new Socket("localhost",50000);   
DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

File dsSystemXml = new File("ds-system.xml");
Scanner xml_in = new Scanner(dsSystemXml);

int coreCount = 0; //last read server core count
int largestCoreCount = 0; //largest server core count so far
int numLargest = 0; //number of largest core count servers
String largestServer = null; //name of largest server
String str="";    

while (xml_in.hasNextLine()) {
    xml_in.hasNextLine();
    str = xml_in.nextLine();
    // System.out.println(str);

   String[] arrOfStr = str.split(" ");

    if (str.contains("type") == true){
        coreCount = Integer.parseInt(arrOfStr[5].split("\"")[1]);
        // System.out.println(coreCount + " " + largestServer);
    }
    if (coreCount > largestCoreCount) { 
        largestCoreCount = coreCount;
        largestServer = arrOfStr[1].split("\"")[1];
        numLargest = Integer.parseInt(arrOfStr[2].split("\"")[1]);
        //System.out.println("name & largestCoreCount " + largestServer + " " + largestCoreCount);
    }
}

str = "";
String username = System.getProperty("user.name");   

dout.write(("HELO\n").getBytes()); 
dout.flush();  
str=in.readLine();  
// System.out.println("To HELO Server says: "+str); 

dout.write(("AUTH " + username + "\n").getBytes()); 
dout.flush();
str=in.readLine();  

// System.out.println("To AUTH Server says: "+str); 
// System.out.println("NumLargest: " + numLargest);
// System.out.println("Largest server: " + largestServer); 

int i = 0;
int serverNum = 0;
while (str != null){ 
    if (str.equals("NONE") == true){
        break;
    }
    dout.write(("REDY\n").getBytes()); 
    dout.flush();
    str=in.readLine();  

    // System.out.println("While loop REDY: "+str); 
    // System.out.println("name & largestCoreCount " + largestServer + " " + largestCoreCount);
    // System.out.println("While loop str: "+str.split(" ", 0)[0]);
    // System.out.println(str.split(" ", 0)[0].equals("JOBN")); 
    
    if (str.split(" ", 0)[0].equals("JOBN") == true && serverNum < numLargest) { 
        dout.write(("SCHD " + i + " " + largestServer + " " + serverNum + "\n").getBytes());
        dout.flush();
        str=in.readLine();  

        i += 1;
        serverNum += 1;
        //System.out.println(serverNum + " " + numLargest);
        if (serverNum == numLargest){
            serverNum = 0;
        }
        // System.out.println("While loop SCHD: "+str); 
        // System.out.println( "SCHD " + i + " " + largestServer);
        // System.out.println("While loop i: "+ i);  
        }
    else if (str.split(" ", 0)[0].equals("JCPL") == true){
        dout.write(("REDY\n").getBytes()); 
        dout.flush();
        str=in.readLine();  
        //System.out.println("JCPL: "+str); 

            if (str.split(" ", 0)[0].equals("JOBN") == true) { 
                dout.write(("SCHD " + i + " " + largestServer + " " + serverNum + "\n").getBytes());
                dout.flush(); 
                str=in.readLine();  
                i += 1;
                serverNum += 1;
                //System.out.println(serverNum + " " + numLargest);

                if (serverNum == numLargest){
                    serverNum = 0;
                }   
            }
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