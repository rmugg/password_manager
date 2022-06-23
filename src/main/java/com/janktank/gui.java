package com.janktank;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import javafx.util.Callback;

import java.awt.Color;
import java.awt.Component;




public class gui{
    
    private static JFrame f;
    private static sqlConnector s;
    private static JTable dataTable;
    private static ArrayList<secretObject> secretArray;
    private static  HashMap<Integer, String> rowIndexEncrypted = new HashMap<Integer, String>();
    private Encryptor encryptor;


    //by default a gui creates an Encryptor object, a datatable and an empty JFrame.
    public gui(){
        encryptor = new Encryptor();
        dataTable = null;
        f = new JFrame("Password Manager");
    }
  
    /*buildGui method takes a sqlConnector object to interact with the database
     *and builds the gui for housing of the buttons and data table
    */
    public void buildGUI(sqlConnector sc){
        s = sc;
        final Color darkishGray = new Color(53, 53, 53);
       
     

        f.getContentPane().removeAll();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(900, 500);
        f.setLocation(300,200);
        f.setForeground(Color.WHITE);
        f.getContentPane().setBackground(Color.DARK_GRAY);


        final JButton buttonGet = new JButton("Get");
        final JButton buttonInsert = new JButton("Insert");
        final JButton buttonExit = new JButton("Exit");
        buttonGet.setBackground(Color.gray);
        buttonInsert.setBackground(Color.gray);
        buttonExit.setBackground(Color.gray);
        JPanel buttonBar = new JPanel();
         
        buttonBar.add(buttonGet);
        buttonBar.add(buttonInsert);
        buttonBar.add(buttonExit);
        buttonBar.setBackground(darkishGray);
        f.getContentPane().add(BorderLayout.SOUTH, buttonBar);

       
        
        

        /*button "Get"
         * Builds and displays a Jtable of mysql data
         * data = table items ( retrieved from sqlConnector.retrieveAll(); )
         * data is decrypted in the method above
         * columnTItles = header row (hand coded) 
        */
        buttonGet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    buildGUI(s);
                
                    rowIndexEncrypted = new HashMap<Integer, String>();
                    
                    String[] columnTitles = {"Domain", "Username", "Password", "iv", "Last Updated"};
                    secretArray = s.retrieveAll();
                    String[][] formatedSecrets = new String[secretArray.size()][5];
                    
                    

                    for(int x = 0; x < secretArray.size(); x++){
                        secretObject secret = secretArray.get(x);
                        formatedSecrets[x][0] = secret.getDomain();
                        formatedSecrets[x][1] = secret.getUser();
                        formatedSecrets[x][2] = secret.getSecret();
                        formatedSecrets[x][3] = secret.getIv();
                        formatedSecrets[x][4] = secret.getLastUpdate();
                    }

                    TableModel model = new DefaultTableModel(formatedSecrets, columnTitles){
                        public boolean isCellEditable(int row, int column){
                            return false;//This causes all cells to be not editable
                        }};
                    
                
                    dataTable = new JTable(model); 
                    dataTable.isCellEditable(0, 4);
                    dataTable.setRowSelectionAllowed(true);


                    dataTable.setForeground(Color.WHITE);
                    dataTable.setBackground(Color.DARK_GRAY);
                    dataTable.setGridColor(darkishGray);
                    dataTable.getTableHeader().setBackground(darkishGray);
                    dataTable.getTableHeader().setForeground(Color.white);
                   
                    JTableHeader dataTableHeader = dataTable.getTableHeader();
                    
                    final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
                    renderer.setBackground(Color.red); 
                    dataTableHeader.setDefaultRenderer(renderer);
                    
                    dataTable.setSize(400,400);
                    dataTable.setAutoCreateRowSorter(true);
                    dataTable.getRowSorter().toggleSortOrder(0);

                    JScrollPane tableHolder = new JScrollPane(dataTable);
                    tableHolder.getViewport().setBackground(Color.DARK_GRAY);
                    tableHolder.getViewport().setForeground(Color.WHITE);
                    tableHolder.setBorder(BorderFactory.createEmptyBorder());

                    f.getContentPane().remove(tableHolder);
                    f.getContentPane().add(BorderLayout.CENTER, tableHolder);  
                    f.validate();
                    dataTable.getSelectionModel().clearSelection();
                    gui.clickRow();
                   
                } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
                        | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e1) {

                    e1.printStackTrace();
                }
            }
        });

        /*button "Insert"
         *calls the insertEntry function on the sqlConnector s;
        */
        buttonInsert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                
                    
                    final JFrame inputFrame = new JFrame();
;                   final JPanel insertEntry = new JPanel();
                    final JTextField domainInput = new JTextField(25);
                    final JLabel domainLabel = new JLabel("Domain:    ");
                    final JTextField usernameInput = new JTextField(25);
                    final JLabel userLabel = new JLabel("User Name: ");
                    final JTextField keyInput = new JTextField(25);
                    final JLabel keyLabel = new JLabel("Password:  ");

                    domainLabel.setForeground(Color.WHITE);
                    domainInput.setBackground(Color.gray);
                    domainInput.setBorder(new LineBorder(Color.BLACK, 1));
                    domainInput.setForeground(Color.white);

                    userLabel.setForeground(Color.WHITE);
                    usernameInput.setBackground(Color.gray);
                    usernameInput.setBorder(new LineBorder(Color.BLACK, 1));
                    usernameInput.setForeground(Color.white);

                    keyLabel.setForeground(Color.WHITE);
                    keyInput.setBackground(Color.gray);
                    keyInput.setBorder(new LineBorder(Color.BLACK, 1));
                    keyInput.setForeground(Color.white);

                    final JButton submitKeyBtn = new JButton("Insert");
                    submitKeyBtn.setBackground(Color.gray);
                    submitKeyBtn.setForeground(Color.white);
                    
            
                    insertEntry.add(domainLabel);
                    insertEntry.add(domainInput);
                    insertEntry.add(userLabel);
                    insertEntry.add(usernameInput);
                    insertEntry.add(keyLabel);
                    insertEntry.add(keyInput);
                    insertEntry.add(submitKeyBtn);
                    insertEntry.setBackground(darkishGray);
                    
                    inputFrame.getContentPane().add(BorderLayout.CENTER, insertEntry);
                    inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    inputFrame.pack();
                    inputFrame.setSize(300, 300);
                    inputFrame.validate();
                    inputFrame.setVisible(true);
            
            
                    submitKeyBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                s.insertEntry(domainInput.getText(), usernameInput.getText(), keyInput.getText());
                            } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
                                    | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException
                                    | IllegalBlockSizeException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            
                            inputFrame.dispose();

                        }
                    });

                
                    
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        /*button "exit"
         *exits the program;
        */
        buttonExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

    }










/*buildKey method - creates a floating jpanel that has the user input their key
 *it then takes the entered key and SHA-256 hashes it to use as part of a key
 *used in the creation of sqlConnector object connection which is used to call the
 *function buildGUI(connection) 
*/
    public void keyInputGui() {
        final JPanel keyEntry = new JPanel();
        final JTextField keyInput = new JTextField(25);
        final JButton submitKeyBtn = new JButton("Use Key");
        

        keyEntry.add(keyInput);
        keyEntry.add(submitKeyBtn);
        
        f.getContentPane().add(BorderLayout.CENTER, keyEntry);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(300, 100);
        f.validate();
        f.setVisible(true);


        submitKeyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectAndBuild(keyInput.getText());
            }
        });
    }


    private String buildKey(String input) throws NoSuchAlgorithmException {
        return Hasher.toHexString(Hasher.getSHA(input));
    }



    /*Used to build the inputted key, create a sqlConnector and build the main gui window for
      viewing and inserting of login information
    */
    public void connectAndBuild(String input){
        try {
                buildGUI(new sqlConnector(buildKey(input)));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
                e1.printStackTrace();
            }
    }










    /* Action listener for the JTable dataTable
       If row is selected -> 
       decrpyt username and password using the IV stored in that row ->
       replace cells with decrypted values ->
       on row change, re-encrypt and replace valuse in the rows cell
    */
    public static void clickRow(){
        dataTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {

                   int selectedRow = dataTable.getSelectedRow();

                   if(selectedRow != -1){

                        Object user = dataTable.getValueAt(selectedRow, 1);
                        Object password = dataTable.getValueAt(selectedRow, 2);
                        Object iv = dataTable.getValueAt(selectedRow, 3);

                        dataTable.getSelectionModel().clearSelection();

                        if(rowIndexEncrypted.containsKey(selectedRow)){

                            String cipherTextUser = "";
                            String cipherTextPassword = "";

                            try {

                                cipherTextUser = Encryptor.encrypt(s.algorithm, user.toString(), s.returnFinalKey(), Encryptor.generateIv(iv.toString()));
                                cipherTextPassword = Encryptor.encrypt(s.algorithm, password.toString(), s.returnFinalKey(), Encryptor.generateIv(iv.toString()));

                            } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
                                    | InvalidAlgorithmParameterException | BadPaddingException
                                    | IllegalBlockSizeException e) {

                                e.printStackTrace();
                            }
                            
                            dataTable.setValueAt(cipherTextUser, selectedRow, 1);
                            dataTable.setValueAt(cipherTextPassword, selectedRow, 2);
                            rowIndexEncrypted.remove(selectedRow);
                            highlightRowSelection(selectedRow);
                            
                            return;

                        }
                        try {
                            
                            String plainTextUser = Encryptor.decrypt(s.algorithm, user.toString(), s.returnFinalKey(), Encryptor.generateIv(iv.toString()));
                            String plainTextPassword = Encryptor.decrypt(s.algorithm, password.toString(), s.returnFinalKey(), Encryptor.generateIv(iv.toString()));

                            dataTable.setValueAt(plainTextUser, selectedRow, 1);
                            dataTable.setValueAt(plainTextPassword, selectedRow, 2);

                            rowIndexEncrypted.put(selectedRow, iv.toString());
                            highlightRowSelection(selectedRow);
                            
                            // dataTable.getSelectionModel().clearSelection();

                        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
                                | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {

                            e.printStackTrace();

                        }
                   }
            }
        });
    } 



    private static void highlightRowSelection(int input){
        final int rowIndex = input;
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                final Color darkishGreen = new Color(85, 127, 70, 90);
                if(row == rowIndex || rowIndexEncrypted.containsKey(row)){
                    c.setBackground(darkishGreen);
                   
                } 
                
                if(!rowIndexEncrypted.containsKey(row)){
                    c.setBackground(null);
                   
                }
                return c;
            }
        });
    }

    public void resetTable(){
        f.removeAll();
        buildGUI(s);
    }




}
