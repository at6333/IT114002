import java.io.*;
import java.net.*;
import java.util.*;

public class TicTacToeServer
{
    public static boolean isRunning = true;
    public static long ClientID = 0;
    int port = 3005;

    public synchronized long getNextId()
    {
        ClientID++;
        return ClientID++;
    }

    public static void main(String[] args)
    {
        int port = 3005;
        if (args.length >= 1)
        {
            String arg = args[0];
            try
            {
                port = Integer.parseInt(arg);
            }
            catch (Exception e)
            {
                // parse issue
            }
        }
        TicTacToeServer server = new TicTacToeServer();
        System.out.println("Tic Tac Toe Server is running");
        System.out.println("Listening on port " + port);
        server.start(port);
        System.out.println("Server stopped");
    }

    public void start(int port)
    {
        this.port = port;
        System.out.println("Waiting for client");
        try (ServerSocket serverSocket = new ServerSocket(port);)
        {
            while (TicTacToeServer.isRunning)
            {
                try
                {
                    Socket client1 = serverSocket.accept();
                    Socket client2 = serverSocket.accept();
                    //System.out.println("Client connecting...");
                    //ServerThread thread = new ServerThread(client, this);
                    //thread.start();
                    //thread.setClientId(getNextId());
                    //clients.add(thread);
                    //System.out.println("Client added to clients pool");

                    Game game = new Game();
                    Game.Player playerX = game.new Player(client1, "X");
                    Game.Player playerO = game.new Player(client2, "O");
                    playerX.setOpponent(playerO);
                    playerO.setOpponent(playerX);
                    game.currentPlayer = playerX;
                    playerX.start();
                    playerO.start();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                isRunning = false;
                Thread.sleep(50);
                System.out.println("Closing server socket");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /*@Deprecated
    public int getClientIndexByThreadId(long id)
    {
        for (int i = 0, l = clients.size(); i < l; i++)
        {
            if (clients.get(i).getId() == id)
            {
                return i;
            }
        }
        return -1;
    }*/

    /*public synchronized void broadcast(Payload payload, String name)
    {
        String msg = payload.getMessage();
        payload.setMessage(
                (name!=null?name:"[Name Error]")
                + (msg != null?": " + msg:"")
                );
        broadcast(payload);
    }

    public synchronized void broadcast(Payload payload)
    {
        System.out.println("Sending message to " + clients.size() + " clients");
        Iterator<ServerThread> iter = clients.iterator();
        while (iter.hasNext())
        {
            ServerThread client = iter.next();
            boolean messageSent = client.send(payload);
            if (!messageSent)
            {
                iter.remove();
                System.out.println("Removed client " + client.getId());
            }
        }
    }

    public synchronized void broadcast(Payload payload, long id)
    {
        int from = getClientIndexByThreadId(id);
        String msg = payload.getMessage();
        payload.setMessage(
                (from>-1?"Client[" + from + "]":"unknown")
                + (msg != null?": " + msg:"")
                );
        broadcast(payload);
    }

    public synchronized void broadcast(String message, long id)
    {
        Payload payload = new Payload();
        payload.setPayloadType(PayloadType.MESSAGE);
        payload.setMessage(message);
        broadcast(payload, id);
    }*/

}

class Game
{
    private Player[] board = {
            null, null, null,
            null, null, null,
            null, null, null, };

    Player currentPlayer;

    public boolean hasWinner()
    {
        return
                (board[0] != null && board[0] == board[1] && board[0] == board[2])
            ||  (board[3] != null && board[3] == board[4] && board[3] == board[5])
            ||  (board[6] != null && board[6] == board[7] && board[6] == board[8])
            ||  (board[0] != null && board[0] == board[3] && board[0] == board[6])
            ||  (board[1] != null && board[1] == board[4] && board[1] == board[7])
            ||  (board[2] != null && board[2] == board[5] && board[2] == board[8])
            ||  (board[0] != null && board[0] == board[4] && board[0] == board[8])
            ||  (board[2] != null && board[2] == board[4] && board[2] == board[6]);
    }

    public boolean boardFilledUp()
    {
        for (int i = 0; i < board.length; i++)
        {
            if (board[i] == null)
            {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean legalMove(int location, Player player)
    {
        if (player == currentPlayer && board[location] == null)
        {
            board[location] = currentPlayer;
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerMoved(location);
            return true;
        }
        return false;
    }

    class Player extends Thread
    {
        String mark;
        Player opponent;
        Socket socket;
        BufferedReader input;
        PrintWriter output;
        public Player(Socket socket, String mark)
        {
            this.socket = socket;
            this.mark = mark;
            try
            {
                input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("WELCOME " + mark);
                System.out.println("Welcome " + mark);
                output.println("MESSAGE Waiting for opponent to connect");
                System.out.println("Waiting for opponent to connect");
            }
            catch (IOException e)
            {
                System.out.println("Player died: " + e);
            }
        }

        public void setOpponent(Player opponent)
        {
            this.opponent = opponent;
        }

        public void otherPlayerMoved(int location)
        {
            output.println("OPPONENT_MOVED " + location);
            System.out.println("Opponent moved " + location);
            output.println(
                    hasWinner() ? "DEFEAT" : boardFilledUp() ? "TIE" : "");
            System.out.println(
                    hasWinner() ? "DEFEAT" : boardFilledUp() ? "TIE" : "");
        }

        public void run()
        {
            try
            {
                output.println("MESSAGE All players connected");
                System.out.println("All players connected");
                if (mark.equalsIgnoreCase("X"))
                {
                    output.println("MESSAGE Your move");
                    System.out.println("Client 1 Your move\nClient 2 has next move");
                }
                else
                {
                    System.out.println("Client 2 Your move\nClient 1 has next move");
                }
                while (true)
                {
                    String command = input.readLine();
                    if (command.startsWith("MOVE"))
                    {
                        int location = Integer.parseInt(command.substring(5));
                        if (legalMove(location, this))
                        {
                            output.println("VALID_MOVE");
                            output.println(hasWinner() ? "VICTORY"
                                    : boardFilledUp() ? "TIE"
                                    : "");
                        }
                        else
                        {
                            output.println("MESSAGE ?");
                            System.out.println("Move already made; move to unclaimed space");
                        }
                    }
                    else if (command.startsWith("QUIT"))
                    {
                        return;
                    }
                }
            }
            catch (IOException e)
            {
                System.out.println("Player died: " + e);
            }
            finally
            {
                try {socket.close();} catch (IOException e) {}
            }
        }
    }
}
