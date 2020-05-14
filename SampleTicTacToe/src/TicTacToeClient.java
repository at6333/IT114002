import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class TicTacToeClient extends JFrame
{
    private Queue<Payload> toServer = new LinkedList<Payload>();
    private Queue<Payload> fromServer = new LinkedList<Payload>();

    private JFrame frame = new JFrame("Tic Tac Toe");
    private JLabel messageLabel = new JLabel("");
    private Square[] board = new Square[9];
    private Square currentSquare;
    private static int PORT = 3005;
    private Socket server;
    //private BufferedReader in;
    //private PrintWriter out;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    public static int turn = 0;

    public TicTacToeClient() throws Exception
    {
        //server = new Socket(serverAddress, PORT);
        JPanel connection = createConnection();
        frame.add(connection, BorderLayout.NORTH);
        in = new ObjectInputStream(server.getInputStream());
        out = new ObjectOutputStream(server.getOutputStream());

        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, "South");

        JPanel boardPanel = new JPanel();
        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
        for (int i = 0; i < board.length; i++)
        {
            JButton bt = new JButton();
            bt.setPreferredSize(new Dimension(150,175));
            bt.setBackground(Color.white);
            final int j = i;
            board[i] = new Square();
            bt.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            JButton currentButton = ((JButton)e.getSource());
                            if (turn % 2 == 0)
                            {
                                try
                                {
                                    currentButton.setText("X");
                                    currentButton.setBackground(Color.cyan);
                                    currentButton.setEnabled(false);
                                    turn++;
                                    currentSquare = board[j];
                                    out.writeObject("MOVE " + j);
                                }
                                catch (IOException ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                            else
                            {
                                try
                                {
                                    currentButton.setText("O");
                                    currentButton.setBackground(Color.magenta);
                                    currentButton.setEnabled(false);
                                    turn++;
                                    currentSquare = board[j];
                                    out.writeObject("MOVE " + j);
                                }
                                catch (IOException ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    });

            board[i].add(bt);
            boardPanel.add(board[i]);
        }
        frame.getContentPane().add(boardPanel, "Center");
    }

    public JPanel createConnection()
    {
        JPanel connectionDetails = new JPanel();
        JTextField host = new JTextField();
        host.setText("127.0.0.1");
        JTextField port = new JTextField();
        port.setText("3005");
        JButton connect = new JButton();

        connect.setText("Connect");
        connectionDetails.add(host);
        connectionDetails.add(port);
        connectionDetails.add(connect);

        connect.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        int _port = -1;
                        try
                        {
                            _port = Integer.parseInt(port.getText());
                        }
                        catch (Exception num)
                        {
                            System.out.println("Port is not a number");
                        }
                        if (_port > -1)
                        {
                            try
                            {
                                server = new Socket(host.getText(), _port);
                                connect.setEnabled(false);
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        }
                    }
                }); 

        return connectionDetails;
    }

    public void play() throws Exception
    {
        String response;
        try
        {
            response = (String)in.readObject();
            if (response.startsWith("WELCOME"))
            {
                char mark = response.charAt(0);
                frame.setTitle("Tic Tac Toe - Player " + mark);
            }
            while (true)
            {
                response = (String)in.readObject();
                if (response.startsWith("VALID_MOVE"))
                {
                    messageLabel.setText("Valid move, please wait");
                    currentSquare.repaint();
                }
                else if (response.startsWith("OPPONENT_MOVED"))
                {
                    int loc = Integer.parseInt(response.substring(15));
                    board[loc].repaint();
                    messageLabel.setText("Opponent moved, your turn");
                }
                else if (response.startsWith("VICTORY"))
                {
                    messageLabel.setText("You win");
                    break;
                }
                else if (response.startsWith("DEFEAT"))
                {
                    messageLabel.setText("You lose");
                    break;
                }
                else if (response.startsWith("TIE"))
                {
                    messageLabel.setText("You tied");
                    break;
                }
                else if (response.startsWith("MESSAGE"))
                {
                    messageLabel.setText(response.substring(8));
                }
            }
            out.writeObject("QUIT");
        }
        finally
        {
            server.close();
        }
    }

    private boolean wantsToPlayAgain()
    {
        int response = JOptionPane.showConfirmDialog(frame,
                "Want to play again?",
                "Ain't this fun?",
                JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }

    static class Square extends JPanel
    {
        JLabel label = new JLabel((Icon)null);

        public Square()
        {
            setBackground(Color.white);
            add(label);
        }

    }

    public static void main(String[] args)
    {
        while (true)
        {
            try
            {
                TicTacToeClient client = new TicTacToeClient();
                client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                client.frame.setSize(600, 600);
                client.frame.setVisible(true);
                client.frame.setResizable(false);
                client.play();
                if (!client.wantsToPlayAgain())
                {
                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
