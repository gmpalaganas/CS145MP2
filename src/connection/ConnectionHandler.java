package connection;

import java.io.*;
import java.net.*;

public class ConnectionHandler{

	private Socket socket;

	private OutputStream out_stream;
	private OutputStreamWriter os_writer;
    private ObjectOutputStream obj_out;
	private PrintWriter p_writer;

	private InputStream in_stream;
	private InputStreamReader ins_reader;
	private BufferedReader b_reader;

	public ConnectionHandler(Socket s){

		try{
			
			this.socket = s;
		
			this.out_stream = socket.getOutputStream();
			this.obj_out = new ObjectOutputStream(out_stream);
			this.os_writer = new OutputStreamWriter(out_stream);
			this.p_writer = new PrintWriter(os_writer);

			this.in_stream = socket.getInputStream();
			this.ins_reader = new InputStreamReader(in_stream);
			this.b_reader = new BufferedReader(ins_reader);	
		}catch(Exception e){
			System.out.println("Unable to initialize Connection Handler :(");
         	e.printStackTrace();
		}
		
	}

	public boolean sendMessage(String msg){
		boolean ret = true; 

		try{
			synchronized(p_writer){
				p_writer.println(msg);
				p_writer.flush();
			}
		}catch(Exception e){
			ret = false;
		}

		return ret;
	}

	public String getMessage(){
		String ret = "";
		InetAddress ip = socket.getInetAddress();

		try{

			synchronized(b_reader){
				String inc =  b_reader.readLine();
				ret = inc;

				if(b_reader.ready())
					ret += "\n";

				while(b_reader.ready()){
					inc =  b_reader.readLine();
					ret += inc;

					if(b_reader.ready())
					ret += "\n";
				}
			}
			
		}catch(IOException e){
			System.out.println("Unable read message from " + ip +  " :(");
         	e.printStackTrace();
		}

		return ret;
	}

	public boolean sendObject(Object obj){
		
		boolean ret = true;
        
		try{
			synchronized(obj_out){
				obj_out.reset();
	            obj_out.writeObject(obj);   	            
	        	obj_out.flush();
	        }
        }catch (IOException e){
	        e.printStackTrace();
	        ret = false;
        } 

        return ret;
	}

	public Object getObject(){
		Object ret = null;
		
		try{
            ObjectInputStream obj_in = new ObjectInputStream(in_stream);
			synchronized(obj_in){
				ret = obj_in.readObject();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return ret;
	}

    public void getFile(String fileName){
        
        try{

            FileOutputStream fo_stream = new FileOutputStream(fileName);
            BufferedOutputStream bo_stream = new BufferedOutputStream(fo_stream);

            synchronized(in_stream){
                int buffer_size = socket.getReceiveBufferSize();
                byte byte_arr[] = new byte[buffer_size];
                 
                int cur = in_stream.read(byte_arr);

                while(cur > 0){
                    
                    bo_stream.write(byte_arr,0,cur);
                    cur = in_stream.read(byte_arr,0,cur);
                 }       
            } 

            fo_stream.close();
            bo_stream.close();
            

        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void sendFile(String fileName){
        
        try{

            File file = new File(fileName); 
            FileInputStream fi_stream = new FileInputStream(file);
            BufferedInputStream bfi_stream = new BufferedInputStream(fi_stream);
            BufferedOutputStream bo_stream = new BufferedOutputStream(out_stream);

            synchronized(out_stream){
                long file_size = file.length(); 
                byte byte_arr[] = new byte[(int)file_size];

                int cur;

                while( (cur = bfi_stream.read(byte_arr)) >= 0 ){
                    out_stream.write(byte_arr, 0, cur);
                    cur = bfi_stream.read(byte_arr);

                }

                fi_stream.close();
                bfi_stream.close();
                bo_stream.flush();
                bo_stream.close();


            } 
            

        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void closeOutStreams(){
       
        try{
            p_writer.flush();
            os_writer.flush();
            out_stream.flush();
            out_stream.close();
            os_writer.close();
            p_writer.close();
        }catch(IOException e){
        
        }
    }
    
}
