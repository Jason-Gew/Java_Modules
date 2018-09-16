package tcp.client;
/******************************************************************
* Simple TCP Client, supports socket connection with timeout, 
* direct send message, send message and get server message back.
*
* Created by Jason/Ge Wu	
* On Jul/21/2017
*******************************************************************/
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.net.Socket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;

public class TCP_Client 
{
	private Boolean status;
	private String result;
	protected Integer timeout;
	protected Integer conn_timeout;
	protected final String server;
	protected final Integer port;
	
	public TCP_Client(String server, Integer port)
	{
		this.server = server;
		this.port = port;
	}
	
	public TCP_Client(String server, Integer port, Integer timeout)
	{
		this.server = server;
		this.port = port;
		this.timeout = timeout;
	}
	
	public TCP_Client(String server, Integer port, Integer timeout, Integer connection_timeout)
	{
		this.server = server;
		this.port = port;
		this.timeout = timeout;
		conn_timeout = connection_timeout;
	}
	
	private String timestamp()
	{
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter current_ts = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String ts = now.format(current_ts);
		return ts;
	}
	
	public void setTimeout(Integer timeout)
	{
		if(timeout > 1000)		// millisecond as unit
			this.timeout = timeout;
	}
	
	public void setConnect_Timeout(Integer connection_timeout)
	{
		if(timeout > 1000)		// millisecond as unit
			conn_timeout = connection_timeout;
	}
	
	public boolean getStatus()
	{
		return status;
	}
	
	public String getResult()
	{
		return result;
	}
	
	public static String getMAC()
	{
		try
		{
            InetAddress localhost = InetAddress.getLocalHost(); 
            NetworkInterface network = NetworkInterface.getByInetAddress(localhost); 
     //       String name = localhost.getHostName();
     //       System.out.println(name);
            byte[] macArray = network.getHardwareAddress();
            
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < macArray.length; i++) 
            {
                    str.append(String.format("%02X%s", macArray[i], (i < macArray.length - 1) ? "-" : ""));
            }
            String macAddress=str.toString();
         
            return macAddress;
        }
        catch(Exception err)
        {
            err.printStackTrace();  //print Exception StackTrace
            return null;
        } 
    }
	/*********** Send a Message Without Receiving ***********/
	public boolean Send(String message)
	{
		boolean flag = false;
		try
		{
			Socket client = new Socket();
			if(timeout != null && conn_timeout == null)
			{
				client.connect(new InetSocketAddress(server, port), timeout);
				client.setSoTimeout(timeout); 
			}
			else if(timeout != null && conn_timeout != null)
			{
				client.connect(new InetSocketAddress(server, port), conn_timeout);
				client.setSoTimeout(timeout); 
			}
			else if(timeout == null && conn_timeout != null)
			{
				client.connect(new InetSocketAddress(server, port), conn_timeout);
			}
			else
			{
				client.connect(new InetSocketAddress(server, port), 0);	//Timeout 0 means using OS default
			}
			// Write Data to the socket
			OutputStreamWriter msg_out = new OutputStreamWriter(client.getOutputStream());
			BufferedWriter buffer_out = new BufferedWriter(msg_out);
			buffer_out.write(message);
			buffer_out.flush();
	//		buffer_out.close();
			
			flag = true;
			if(client != null)
			{  
	            client.close();
			}
			result = "Success";
			status = flag;
			System.out.println(timestamp()+" TCP Send: "+result);
			return flag;
		}
		catch(SocketTimeoutException e)
		{
			System.out.print(timestamp()+ " : ");	
			System.err.println("Socket Timedout for [ " + timeout + " ] Second(s).");
			result = e.toString();
			status = flag;
			return flag;
		}
		catch(SocketException e)
		{
			System.out.print(timestamp()+ " : ");
			System.err.println(e);
			result = e.toString();
			status = flag;
			return flag;
		}
		catch(IOException e) 
		{
		//	e.printStackTrace();
			System.out.print(timestamp()+ " : ");	
			System.err.println(e);
			result = e.toString();
			status = flag;
			return flag;
		}	
	}
	/******** Send a Message and Get Server Response ********/
	public String Send_(String message)
	{
		String receive = null;
		status = false;
		result = "Fail";
		try
		{
			Socket client = new Socket();
			if(timeout != null && conn_timeout == null)
			{
				client.connect(new InetSocketAddress(server, port), timeout);
				client.setSoTimeout(timeout); 
			}
			else if(timeout != null && conn_timeout != null)
			{
				client.connect(new InetSocketAddress(server, port), conn_timeout);
				client.setSoTimeout(timeout); 
			}
			else if(timeout == null && conn_timeout != null)
			{
				client.connect(new InetSocketAddress(server, port), conn_timeout);
			}
			else
			{
				client.connect(new InetSocketAddress(server, port), 0);	//Timeout 0 means using OS default
			}		
		//Write Data to the socket
			OutputStreamWriter msg_out = new OutputStreamWriter(client.getOutputStream());
			BufferedWriter buffer_out = new BufferedWriter(msg_out);
			buffer_out.write(message);
			buffer_out.flush();
			client.shutdownOutput();
		//Read Data from the socket
            BufferedReader buffer_in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String temp = null;
            StringBuilder recv = new StringBuilder();
            while((temp = buffer_in.readLine()) != null)
            {
                recv.append(temp);
            }
			buffer_in.close();
			buffer_out.close();
			receive = recv.toString();

			result = "Success";
			status = true;
			System.out.println(timestamp() +" TCP Send: "+result);	
			if(client != null)
			{  
	            client.close();
			}
			return receive;
		}
		catch(SocketTimeoutException err)
		{
			System.out.print(timestamp()+ " : ");	
			System.err.println("Socket Timedout for [ " + timeout + " ] Second(s).");
			result = err.toString();
			return receive;
		}
		catch(SocketException err)
		{
			System.out.print(timestamp() + " : ");	
			System.err.println(err);
			result = err.toString();
			System.out.println(status);
			return receive;
		}
		catch(IOException err) 
		{
		//	err.printStackTrace();
			System.out.print(timestamp() + " : ");	
			result = err.toString();
			return receive;
		}
	}
	/********** Send Multiple Messages (developing) **********/
	public boolean Send_Multiple(ArrayList<String> messages)
	{
		boolean flag = false;
		try
		{
			Socket client = new Socket();
			if(timeout != null && conn_timeout == null)
			{
				client.connect(new InetSocketAddress(server, port), timeout);
				client.setSoTimeout(timeout); 
			}
			else if(timeout != null && conn_timeout != null)
			{
				client.connect(new InetSocketAddress(server, port), conn_timeout);
				client.setSoTimeout(timeout); 
			}
			else if(timeout == null && conn_timeout != null)
			{
				client.connect(new InetSocketAddress(server, port), conn_timeout);
			}
			else
			{
				client.connect(new InetSocketAddress(server, port), 0);	//Timeout 0 means using OS default
			}
			// Write Data to the socket
			OutputStreamWriter msg_out = new OutputStreamWriter(client.getOutputStream());
			BufferedWriter buffer_out = new BufferedWriter(msg_out);
			for(String msg : messages)
			{
				buffer_out.write(msg+"\n");
				buffer_out.flush();
			}
			
	//		buffer_out.close();
			
			flag = true;
			if(client != null)
			{  
	            client.close();
			}
			result = "Success";
			status = flag;
			System.out.println(timestamp()+" TCP Send: "+result);
			return flag;
		}
		catch(SocketTimeoutException e)
		{
			System.out.print(timestamp()+ " : ");	
			System.err.println("Socket Timedout for [ " + timeout + " ] Second(s).");
			result = e.toString();
			status = flag;
			return flag;
		}
		catch(SocketException e)
		{
			System.out.print(timestamp()+ " : ");
			System.err.println(e);
			result = e.toString();
			status = flag;
			return flag;
		}
		catch(IOException e) 
		{
		//	e.printStackTrace();
			System.out.print(timestamp()+ " : ");	
			System.err.println(e);
			result = e.toString();
			status = flag;
			return flag;
		}			
	}
	
}
