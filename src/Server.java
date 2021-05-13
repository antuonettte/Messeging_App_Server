import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class Server extends JFrame{

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;


    public Server(){
        super("Instant Chat");
        userText = new JTextField();
        userText.setEditable(false);

        userText.addActionListener(
                event -> {
                    sendMessage(event.getActionCommand());
                    userText.setText("");
                }

        );

        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        chatWindow.setEditable(false);
        add(new JScrollPane(chatWindow));

        setSize(800,500);
        setVisible(true);
    }

    //set up and run the

    public void startRunning(){
        try{
            server = new ServerSocket(4222, 100);
            while(true){
                try{
                    waitForConnection();
                    setupStreams();
                    whileChatting();

                }catch(EOFException eofException){
                    showMessage("\n Server Connection Terminated");
                }finally{
                    closeCrap();
                }
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    //wait for client connection, then display connection msg

    private void waitForConnection() throws IOException{
        showMessage("Waiting for User to connect....");
        connection = server.accept();
        showMessage(" Now connected to" + connection.getInetAddress());
    }
    // setups streams for date
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams Setup \n ");
    }

    private void whileChatting() throws IOException{
        String message = "You are now connected";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n" + message);
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n Something Not WoRkInG OoPs");
            }
        }while(!message.equals("CLIENT -END"));
    }

    //End socket and clear streams

    private void closeCrap(){
        showMessage("\n Ending Connection... \n");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
        //sends message to client
    private void sendMessage(String message){
        try{
            output.writeObject("SERVER - " + message);
            output.flush();
            showMessage("\n" + message);
        }catch(IOException ioException){
            chatWindow.append("\n ERROR: CAN't SEND MESSAGE ");
        }
    }


    //Keeps the chat window updated
    private void showMessage(final String message){
        SwingUtilities.invokeLater(
                () -> chatWindow.append(message)
        );
    }
    //Give Client Permission to use text box
    private void ableToType(final boolean bool){
        SwingUtilities.invokeLater(
                () -> userText.setEditable(bool)
        );
    }

}
