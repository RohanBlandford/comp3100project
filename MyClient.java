import java.net.*;  
import java.io.*;
import java.util.Scanner;  

class MyClient{  
public static void main(String args[])throws Exception{  
//communication with server
Socket s=new Socket("localhost",50000);   
DataOutputStream dout=new DataOutputStream(s.getOutputStream()); 
BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

//open the ds-system.xml file generated from the config on client start  
File ds_system_xml = new File("ds-system.xml");
Scanner xml_in = new Scanner(ds_system_xml);

//variables to keep identify and track the appropriate server for LRR
int core_count = 0; //last read server core count
int largest_core_count = 0; //largest server core count so far
int num_largest_servers = 0; //number of largest core count servers
String largest_server_name = null; //name of largest server
String str="";    

//read the file and extract information relating to the correct server
while (xml_in.hasNextLine()) {
    xml_in.hasNextLine();
    str = xml_in.nextLine();
    // System.out.println(str);

   String[] str_split_array = str.split(" ");

    if (str.contains("type") == true){
        core_count = Integer.parseInt(str_split_array[5].split("\"")[1]);
        // System.out.println(core_count + " " + largest_server_name);
    }
    if (core_count > largest_core_count) { 
        largest_core_count = core_count;
        largest_server_name = str_split_array[1].split("\"")[1];
        num_largest_servers = Integer.parseInt(str_split_array[2].split("\"")[1]);
        //System.out.println("name & largest_core_count " + largest_server_name + " " + largest_core_count);
    }
}

xml_in.close(); //close the xml file for resource management sake

//Server handshake
dout.write(("HELO\n").getBytes()); 
dout.flush();  
str=in.readLine();  
// System.out.println("To HELO Server says: "+str); 

String username = System.getProperty("user.name");  
dout.write(("AUTH " + username + "\n").getBytes()); 
dout.flush();
str=in.readLine();  
// System.out.println("To AUTH Server says: "+str); 
// System.out.println("num_largest_servers: " + num_largest_servers);
// System.out.println("Largest server: " + largest_server_name); 

int i = 0; //to keep track of number of while loop iterations. Used for job id 
int server_num = 0; //for assigning jobs to servers when there are more than 1 of the largest server type
while (str != null){ 
    if (str.equals("NONE") == true){
        break;
    }
    dout.write(("REDY\n").getBytes()); 
    dout.flush();
    str=in.readLine();  
    // System.out.println("While loop REDY: "+str); 
    // System.out.println("name & largest_core_count " + largest_server_name + " " + largest_core_count);
    // System.out.println("While loop str: "+str.split(" ", 0)[0]);
    // System.out.println(str.split(" ", 0)[0].equals("JOBN")); 
    
    if (str.split(" ", 0)[0].equals("JOBN") == true && server_num < num_largest_servers) { 
        dout.write(("SCHD " + i + " " + largest_server_name + " " + server_num + "\n").getBytes());
        dout.flush();
        str=in.readLine();  

        i += 1;
        server_num += 1;
        //System.out.println(server_num + " " + num_largest_servers);
        if (server_num == num_largest_servers){
            server_num = 0;
        }
        // System.out.println("While loop SCHD: "+str); 
        // System.out.println( "SCHD " + i + " " + largest_server_name);
        // System.out.println("While loop i: "+ i);  
    }
    else if (str.split(" ", 0)[0].equals("JCPL") == true){
        dout.write(("REDY\n").getBytes()); 
        dout.flush();
        str=in.readLine();  
        //System.out.println("JCPL: "+str); 

            if (str.split(" ", 0)[0].equals("JOBN") == true) { 
                dout.write(("SCHD " + i + " " + largest_server_name + " " + server_num + "\n").getBytes());
                dout.flush(); 
                str=in.readLine();  
                i += 1;
                server_num += 1;
                //System.out.println(server_num + " " + num_largest_servers);

                if (server_num == num_largest_servers){
                    server_num = 0;
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