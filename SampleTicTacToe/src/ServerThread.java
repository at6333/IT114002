import java.io.*;
import java.net.*;

public class ServerThread extends Thread
{
    private Socket client;
    private ObjectInputStream in; //from client
    private ObjectOutputStream out; //to client
    private boolean isRunning = false;
    private TicTacToeServer server;
    private String clientName = "Anon";

    public ServerThread(Socket client, TicTacToeServer server) throws IOException
    {
        this.client = client;
        this.server = server;
        isRunning = true;
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());
    }

    public void setClientId(long id)
    {
        clientName += "_" + id;
    }

    public boolean send(Payload payload)
    {
        try
        {
            out.writeObject(payload);
            return true;
        }
        catch (IOException e)
        {
            System.out.println("Error sending message to client");
            e.printStackTrace();
            cleanup();
            return false;
        }
    }

    @Deprecated
    public boolean send(String message)
    {
        Payload payload = new Payload();
        payload.setPayloadType(PayloadType.MESSAGE);
        payload.setMessage(message);
        return send(payload);
    }

    public void broadcastConnected()
    {
        System.out.println(this.clientName + " broadcast connected");
        Payload payload = new Payload();
        payload.setPayloadType(PayloadType.CONNECT);
        server.broadcast(payload, this.clientName);
    }

    public void broadcastDisconnected()
    {
        Payload payload = new Payload();
        payload.setPayloadType(PayloadType.DISCONNECT);
        server.broadcast(payload, this.clientName);
    }

    @Override
    public void run()
    {
        try
        {
            Payload fromClient;
            while (isRunning
                    && !client.isClosed()
                    && (fromClient = (Payload)in.readObject()) != null)
            {
                processPayload(fromClient);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Terminating Client");
        }
        finally
        {
            broadcastDisconnected();
            System.out.println("Server cleanup");
            cleanup();
        }
    }

    private void processPayload(Payload payload)
    {
        System.out.println("Received from client: " + payload);
        switch (payload.getPayloadType())
        {
            case CONNECT:
                String clientName = payload.getMessage();
                if (clientName != null)
                {
                    this.clientName = clientName;
                }
                broadcastConnected();
                break;
            case DISCONNECT:
                System.out.println("Received disconnect");
                break;
            case MESSAGE:
                server.broadcast(payload, this.clientName);
                break;
            case SWITCH:
                payload.setMessage(this.clientName);
                break;
            default:
                System.out.println("Unhandled payload type from client " + payload.getPayloadType());
                break;
        }
    }

    private void cleanup()
    {
        if (in != null)
        {
            try {in.close();}
            catch (IOException e) {System.out.println("Input already closed");}
        }
        if (out != null)
        {
            try {out.close();}
            catch (IOException e) {System.out.println("Client already closed");}
        }
        if (client != null && !client.isClosed())
        {
            try {client.shutdownInput();}
            catch (IOException e) {System.out.println("Socket/Input already closed");}
            try {client.shutdownOutput();}
            catch (IOException e) {System.out.println("Socket/Output already closed");}
            try {client.close();}
            catch (IOException e) {System.out.println("Client already closed");}
        }
    }
}
