package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DatabaseConnector extends SQLiteOpenHelper {
    public static final String ACCOUNT_TABLE = "ACCOUNT_TABLE";
    public static final String BANK_NAME = "BANK_NAME";
    public static final String ACCOUNT_HOLDER_NAME = "ACCOUNT_HOLDER_NAME";
    public static final String BALANCE = "BALANCE";

    public static final String ACCOUNT_NO = "ACCOUNT_NO";

    public static final String TRANSACTION_TABLE = "TRANSACTION_TABLE";
    public static final String TRANSACTION_ID = "TRANSACTION_ID";
    public static final String DATE = "DATE";
    public static final String AMOUNT = "AMOUNT";
    public static final String EXPENSE_TYPE = "EXPENSE_TYPE";


    public DatabaseConnector(@Nullable Context context) {
        super(context, "190232V.db", null, 1);
    }

    // this is called the first time a database is accessed. There should be code in here to create a new database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement1 = "CREATE TABLE " + ACCOUNT_TABLE + "(" + ACCOUNT_NO + " VARCHAR(50) PRIMARY KEY, " + BANK_NAME + " VARCHAR(100), "+ ACCOUNT_HOLDER_NAME + " VARCHAR(100), " + BALANCE + " FLOAT);";
        db.execSQL(createTableStatement1);
        String createTableStatement3 = "CREATE TABLE " + TRANSACTION_TABLE + "(" + TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " DATE, "+ ACCOUNT_NO + " VARCHAR(50), " + EXPENSE_TYPE + " INTEGER, "+AMOUNT+" FLOAT);";
        db.execSQL(createTableStatement3);

    }

    //This is called if the database version number changes. It prevents users apps from breaking when you change the database design.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    // Insert an account to database
    public boolean addAccount_db(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(ACCOUNT_NO, account.getAccountNo());
        cv.put(BANK_NAME, account.getBankName());
        cv.put(ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        cv.put(BALANCE, account.getBalance());

        long insert = db.insert(ACCOUNT_TABLE,null,cv);
        if(insert==-1){
            return false;
        }else{
            return true;
        }
    }
    public boolean addTransaction_db(Transaction transaction){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        Date date = transaction.getDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String str_date = formatter.format(date);

        // Formatting transaction type for db insert
        Integer expenseType = 0;
        ExpenseType transaction_type = transaction.getExpenseType();
        if (transaction_type == ExpenseType.EXPENSE){
            expenseType = 1;
        }
        else if (transaction_type == ExpenseType.INCOME){
            expenseType = 2;
        }

        cv.put(DATE,str_date);
        cv.put(ACCOUNT_NO, transaction.getAccountNo());
        cv.put(EXPENSE_TYPE , expenseType);
        cv.put(AMOUNT, transaction.getAmount());

        long insert = db.insert(TRANSACTION_TABLE, null,cv);
        db.close();
        if(insert==-1){
            return false;
        }else{
            return true;
        }
    }
    // Get all Transaction logs
    public List<Transaction> getAllLog(){
        List<Transaction> returnList = new ArrayList<>();

        String sql_query = "SELECT * FROM "+ TRANSACTION_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql_query, null);

        if(cursor.moveToFirst()){
            do {
                int transactionID = cursor.getInt(0);
                String datestr = cursor.getString(1);
                String accountNo = cursor.getString(2);
                int transactionType = cursor.getInt(3);
                double amount = Math.round(cursor.getFloat(4) * 100.0) / 100.0;

                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(datestr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                ExpenseType ex = null;
                if(transactionType == 1){
                    ex = ExpenseType.EXPENSE;
                }
                else if (transactionType == 2){
                    ex = ExpenseType.INCOME;
                }

                Transaction curTransaction = new Transaction(date, accountNo, ex, amount);
                returnList.add(curTransaction);

            }while (cursor.moveToNext());

        }else {
            // Do nothing;
        }
        return returnList;
    }
    // Get all Accounts in database
    public List<Account> getAllAccount(){
        List<Account> returnList = new ArrayList<>();

        String sql_query = "SELECT * FROM "+ ACCOUNT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql_query, null);

        if(cursor.moveToFirst()){
            do {
                String accountNo = cursor.getString(0);
                String bankName = cursor.getString(1);
                String accountHolder = cursor.getString(2);
                float initialBalance = cursor.getFloat(3);

                Account curAccount = new Account(accountNo, bankName, accountHolder, initialBalance);

                returnList.add(curAccount);

            }while (cursor.moveToNext());

        }else {
            // Do nothing;
        }
        return returnList;
    }
    // Get Account from account number
    public Account getAccountFromNo(String accountNoIn){
        Account curAccount = null;

        String sql_query = "SELECT * FROM "+ ACCOUNT_TABLE+" WHERE "+ACCOUNT_NO+" = '"+accountNoIn+"';";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql_query, null);

        cursor.moveToFirst();
        String accountNo = cursor.getString(0);
        String bankName = cursor.getString(1);
        String accountHolder = cursor.getString(2);
        float initialBalance = cursor.getFloat(3);

        curAccount = new Account(accountNo, bankName, accountHolder, initialBalance);

        return curAccount;
    }
    // Delete account using account number
    public boolean deleteOneAccount(String accountNo){

        SQLiteDatabase db = this.getWritableDatabase();
        String sqlDeleteQuery = "DELETE FROM "+ ACCOUNT_TABLE+" WHERE "+ ACCOUNT_NO + " = '"+accountNo+ "';";
        Cursor cursor = db.rawQuery(sqlDeleteQuery, null);
        if(cursor.moveToFirst()){
            return true;
        }
        else {
            return false;
        }
    }
    public boolean  updateAccountBalance(String accountNo,double amount){
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlUpdateBalanceQuery = "UPDATE "+ACCOUNT_TABLE+" SET "+BALANCE+" = (SELECT "+BALANCE+" FROM "+ACCOUNT_TABLE+" WHERE "+ACCOUNT_NO+" = '"+accountNo+"') + "+amount+" WHERE "+ACCOUNT_NO+" = '"+accountNo+"';";
        Cursor cursor = db.rawQuery(sqlUpdateBalanceQuery, null);
        if(cursor.moveToFirst()){
            return true;
        }
        else {
            return false;
        }

    }
}