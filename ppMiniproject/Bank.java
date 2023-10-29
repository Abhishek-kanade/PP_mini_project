/*This java Banking appliction which use basic java 
programming.It is based on java and sql(MySQL) interfacing 
In which java use as frontend which interact with user and MySQL use to 
store all the data in tabluar form.This programming can do all basic Bank
functionalites like create new acccount ,withdraw and deposit money and display 
all trasition history.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;
//Customer class it contain all function which perfrom Banking functionalities like:
/*
 * create new account
 * show details of customer
 * withdraw and deposite money
 * display trasition history with date
 */
class Customer{
    Scanner sc =new Scanner(System.in);
    private int AccountNo,PIN;//Customer account number and PIN.
    String Name;// customer Name .
    int Balance=0;//customer current Balance in account.
    String Type,Phone,PAN_no; //Account Type(Current/saving),Phone number,PAN card numeber.
    //this method will help to create new account of customer and add all detials of customer by taking user input and 
    // store all customer data in Bank_cus table by executing SQL quries . 
    public void get_account(Connection con) throws SQLException{ 
        String query="insert into Bank_cus(Name,AccountType,Phone_no,PAN_No,Balance,PIN) values(?,?,?,?,?,?)";//sql query from insert customer data in Bank data table
        System.out.print("enter your Name : ");
        Name=sc.nextLine();                      //take input name from user
        System.out.print("enter your Phone number : ");
        Phone=sc.next();                        //take input Phone number from user
        System.out.print("enter your PAN Card number : ");
        PAN_no=sc.next();                       //take input PAN card number from user
        int Pin_check=1;                        //this is use run the while until user enter PIN is not verified
        while(Pin_check!=0){
            System.out.print("Create PIN : ");
            PIN=sc.nextInt();
            System.out.print("ReEnter your PIN : ");
            if(PIN==sc.nextInt()){        //if both PIN and ReEnter is same Pin check will be 0 and while get terminated
                Pin_check=0;
            }
            else{
                System.out.println("PIN is not match"); //if both is different it will diplay message and process will be continue.
            }
        }
         System.out.print("choose your Account type \n1.Savings\n2.Current\n");
        switch(sc.nextInt()){ //It will ask user to choose their Account Type and their choice is stored in (Type)variable.
            case 1:
                Type="Savings";
                break;
            case 2:
                Type="Current";
                break;
            default:         //if user enter other number the account type will become Savings.
                Type="Savings";
                break;        
        }
        char AccountType_check='y';
        while(AccountType_check!='e'){
            if(Type=="Savings"){ //if user select savings account, their zero opening balance and it deposit any amount then loop get terminate.
                System.out.println("enter your deposite Ammount : ");
                Balance=sc.nextInt();
                AccountType_check='e';
            }
            else if (Type=="Current") { // in current account their minimum opening balance of 50000,if use deposit less then 5000 then loop will be continue.
                System.out.println("enter your deposite Ammount : ");
                Balance=sc.nextInt();
                if(Balance<5000){
                    System.out.println("Minium balance deposit is 5000");
                }
                else{  // if user deposite more then 5000 then loop will be terminate.
                    AccountType_check='e';
                }
                
            } 
        }
        PreparedStatement ps=con.prepareStatement(query); //Preapre query from execution by connect with mysql.
        ps.setString(1, Name); //it set user name in query by replacing with '?'. 
        ps.setString(2, Type); //it set user Account Type in query by replacing with '?'. 
        ps.setString(3, Phone); //it set user's Phone number in query by replacing with '?'. 
        ps.setString(4,PAN_no);//it set user's PAN card number in query by replacing with '?'. 
        ps.setInt(5,Balance); //it set user's name in query by replacing with '?'. 
        ps.setInt(6, PIN); //it set user's Account PIN in query by replacing with '?'. 
        ps.executeUpdate();    //it execute the final command in sql server and store data user data in (Bank_cus) Table
        String query1="select AccountNo from Bank_cus where PAN_No=?"; //this sql query is use to get Account Number of user(Account number automatically gentrated) 
        PreparedStatement ps1=con.prepareStatement(query1); //Preparing query statment for execution
        ps1.setString(1, PAN_no);   //Set PAN card number for where condition
        ResultSet resutl_set=ps1.executeQuery();  //Store the result in resutl set
        while(resutl_set.next()){                // while loop print one by one data from table
            AccountNo=resutl_set.getInt("AccountNo"); //store Account number in variable from table
            System.out.println("Your Account is created ");
            System.out.println("\n\tYour Account Number is "+AccountNo+"\n\n");
        }
        //Below query statment is use create table which stored transition history of customer and every has their own unique transition history table which end with their Account number.
        String query0="create table ac_"+AccountNo+"(Sr_no int primary key auto_increment,Ammount int,Remark varchar(20),Date_of_transition date,Balance int)";
        PreparedStatement ps0=con.prepareStatement(query0);
        ps0.executeUpdate();
    
    }
    public void Show_details(Connection con) throws SQLException{ //this methode is use show customer'Account details 
        String query2="select * from Bank_cus where AccountNo=?"; //sql query show all customer details which has respective Account number
        PreparedStatement ps1=con.prepareStatement(query2); //
        System.out.print("Enter your Account Number : ");
        ps1.setInt(1,sc.nextInt());
        ResultSet rs1=ps1.executeQuery();
        while(rs1.next()){
            if(rs1.wasNull()){
                System.out.println("\tAccount not exist\n");


                System.exit(0);
            }
            AccountNo=rs1.getInt("AccountNo");
            System.out.println("Account Number : "+AccountNo);
            Name=rs1.getString("Name");
            System.out.println("Name : "+Name);
            Type=rs1.getString("AccountType");
            System.out.println("Account Type : "+Type);
            Phone=rs1.getString("Phone_no");
            System.out.println("Phone number : "+Phone);
            PAN_no=rs1.getString("PAN_No");
            System.out.println("PAN card number :"+PAN_no);
            Balance=rs1.getInt("Balance");
            System.out.println("Balance : Rs. "+Balance);
        }

    }
    /*this method perform the Deposit from Bank 
     * first it ask for Account number then PIN for verification
     * after verification it ask for deposit amount 
     * after it balance get changed and this deposit history store in transition history 
     */
    public void deposit(Connection con) throws SQLException{
        int Ammount;
        System.out.print("Enter your Account Number : ");
        AccountNo=sc.nextInt();
        String q3="select PIN,Balance from Bank_cus where AccountNo=?";
        PreparedStatement ps3=con.prepareStatement(q3);
        ps3.setInt(1,AccountNo);
        ResultSet rs3=ps3.executeQuery();
        while(rs3.next()){
            if(rs3.wasNull()){
                System.out.println("Account not exist");
                System.exit(1);
            }
            PIN=rs3.getInt("PIN");
            Balance=rs3.getInt("Balance");
        }
        int check_PIN=1,count=0; //it check the PIN is correct or not and count counts the number attempt to enter PIN.It will give only 3 attempt after it programm get terminate.
        while(check_PIN!=0){
            System.out.print("Enter your PIN : ");
            if(PIN==sc.nextInt()){
                check_PIN=0;
            }
            else if(count==3){
                System.exit(1); //exit Programm after 3 attempt
            }
            else{
                count++;
                System.out.println("Wrong PIN \n attempt left "+(3-count)); //it display number of attempt left.
            }
        }
        LocalDate date= LocalDate.now(); //this is store current date of system for transition history.
        System.out.print("Enter your Deposit Ammount : ");
        Ammount=sc.nextInt();
        Balance+=Ammount; //Balance get updated.
        String q4="update Bank_cus set Balance=? where AccountNo=?"; //query for Update Balance in Customer table.
        PreparedStatement ps4=con.prepareStatement(q4);
        ps4.setInt(1, Balance);
        ps4.setInt(2, AccountNo);
        ps4.executeUpdate();
        String q5="insert into ac_"+AccountNo+"(Ammount,Remark,Date_of_transition,Balance) values(?,?,?,?)";
        PreparedStatement ps5=con.prepareStatement(q5);
        ps5.setInt(1, Ammount);
        ps5.setString(2,"Credited");
        ps5.setObject(3,date);
        ps5.setInt(4, Balance);
        ps5.executeUpdate();
        System.out.print("Do you want to see your Balance[Y/N] :");//here system ask for display customer updated balance.
        char check_balance=sc.next().charAt(0);
        if(check_balance=='Y'||check_balance=='y'){ 
            System.out.println("your Current Balance : "+Balance);
        }
    }
    /*this method perform the Withdraw from Bank 
     * first it ask for Account number then PIN for verification
     * after verification it ask for Withdrawal amount 
     * after it balance get changed and this Withdraw history store in transition history 
     */
    public void withdraw(Connection con) throws SQLException{
        int Ammount;
        System.out.print("Enter your Account Number : ");
        AccountNo=sc.nextInt();
        String query6="select PIN,Balance from Bank_cus where AccountNo=?";
        PreparedStatement ps6=con.prepareStatement(query6);
        ps6.setInt(1,AccountNo);
        ResultSet rs6=ps6.executeQuery();
        while(rs6.next()){     
            if(rs6.wasNull()){
                System.out.println("Account not exist");
                System.exit(1);
            } 
            else{        
            PIN=rs6.getInt("PIN");
            Balance=rs6.getInt("Balance");
        }}
        int check1=1,count=0; //it check the PIN is correct or not and count counts the number attempt to enter PIN.It will give only 3 attempt after it programm get terminate.
        while(check1!=0&&count<=3){
            System.out.print("Enter your PIN : ");
            if(PIN==sc.nextInt()){
                check1=0;
            }
             else if(count==3){
                System.exit(1);
            }
            else{
                count++;
                System.out.println("Wrong PIN \n attempt left "+(3-count));

            }
        }
        LocalDate date= LocalDate.now(); //this is store current date of system for transition history.
        System.out.print("Enter your withdraw Ammount : ");
        Ammount=sc.nextInt();
        Balance-=Ammount;
        String q4="update Bank_cus set Balance=? where AccountNo=?";
        PreparedStatement ps4=con.prepareStatement(q4);
        ps4.setInt(1, Balance);
        ps4.setInt(2, AccountNo);
        ps4.executeUpdate();
        String q5="insert into ac_"+AccountNo+"(Ammount,Remark,Date_of_transition,Balance) values(?,?,?,?)";
        PreparedStatement ps5=con.prepareStatement(q5);
        ps5.setInt(1, Ammount);
        ps5.setString(2,"Debited");
        ps5.setObject(3,date);
        ps5.setInt(4, Balance);
        ps5.executeUpdate();
        System.out.print("Do you want to see your Balance[Y/N] :");
        char check_balance=sc.next().charAt(0);
        if(check_balance=='Y'|| check_balance=='y'){
            System.out.println("your Current Balance : "+Balance);
        }
    }
    /*this methode is use display transition histroy of user 
     * it fetch data from "ac_AccountNo" table 
     * which stores all individuale customer transition data corrsponding to their Account number.
     */
    public void Show_transition(Connection con) throws SQLException{
        System.out.print("Enter your Account Number : ");
        AccountNo=sc.nextInt();
        String q7="select PIN from Bank_cus where AccountNo=?";
        PreparedStatement ps7=con.prepareStatement(q7);
        ps7.setInt(1,AccountNo );
        ResultSet rs7=ps7.executeQuery();
        while(rs7.next()){
            if(rs7.wasNull()){
                System.out.println("Account not exist");
                System.exit(1);
            }
            PIN=rs7.getInt("PIN");
        }
        int chk=1,count=0;
        System.out.print("enter PIN : ");
        while(chk!=0){
            if(PIN==sc.nextInt()){
                chk=0;
            }
            else if(count==3){
                System.exit(1);
            }
            else{
                count++;
                System.out.println("Wrong PIN \nleft attempt "+(3-count));
            }
        }
        //here it shows customer transition history in tabluer from.
        String q8="select * from ac_"+AccountNo;
        PreparedStatement ps8=con.prepareStatement(q8);
        ResultSet rs8=ps8.executeQuery();
        int Sr,amt,Bal;
        String Remark;
        System.out.printf("%-67s\n","_____________________________________________________________________");
        System.out.printf("|%-6s|%-12s|%-10s|%-20s|%-15s|%n","Sr_no","Ammount","Remark","Date_of_transition","Balance");
        System.out.printf("%-67s\n","---------------------------------------------------------------------");
        while(rs8.next()){
            Sr=rs8.getInt("Sr_no");
            amt=rs8.getInt("Ammount");
            Bal=rs8.getInt("Balance");
            Remark=rs8.getString("Remark");
            System.out.printf("|%-6s|%-12s|%-10s|%-20s|%-15s|%n",Sr,amt,Remark,rs8.getObject("Date_of_transition"),Bal);
            System.out.printf("%-67s\n","---------------------------------------------------------------------");
        }
    }
}
public class Bank {//this bank is use all above methode to for perform Bank functionalities
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Scanner sc=new Scanner(System.in);   //create Scanner class object sc.
        Class.forName("com.mysql.cj.jdbc.Driver"); 
        String url="jdbc:mysql://localhost:3306/Bank";
        String user="root";
        String  pass="a.k.47576";
        Connection con = DriverManager.getConnection(url, user, pass);//Create MySQl connection with JDBC
        Customer customer1=new Customer(); //Creating Customer class object customer.
        int choice;
        System.out.printf("%-67s\n","_____________________________________________________________________");
        System.out.println("\t\t\tWelcome to GS-Bank");
        System.out.printf("%-67s\n","---------------------------------------------------------------------");
        System.out.print("\n\n1.Create new Account \n2.Show Details\n3.Withdraw\n4.Deposite\n5.Transition History\n\n");
        System.out.print("enter your choice : ");
        choice=sc.nextInt();
        switch(choice){
            case 1:
                customer1.get_account(con);
                break;
            case 2:
                customer1.Show_details(con);
                break;
            case 3:
                customer1.withdraw(con);
                break;
            case 4:
                customer1.deposit(con);
                break;
            case 5:
                customer1.Show_transition(con);
                break;
            default:
                break;                    
        }
        sc.close();
    }
}

