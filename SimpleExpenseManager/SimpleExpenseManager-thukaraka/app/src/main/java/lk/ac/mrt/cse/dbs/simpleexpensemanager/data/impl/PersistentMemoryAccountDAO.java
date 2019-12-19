package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class PersistentMenoryAccountDAO extends SQLiteOpenHelper implements AccountDAO {


    public static final String DATABASE_NAME = "170417B";
    public static final String ACCOUNTS_COLUMN_NO = "accountNo";
    public static final String ACCOUNTS_COLUMN_BANK_NAME = "bankName";
    public static final String ACCOUNTS_COLUMN_HOLDER_NAME = "accountHolderName";
    public static final String ACCOUNTS_COLUMN_BALANCE = "balance";






    public PersistentMenoryAccountDAO(Context context) {
        super(context,DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table account " +
                "(accountNo text primary key, bankName text,accountHolderName text,balance double)"
        );
        db.execSQL(
                "create table tbltrans " +
                        "(accountNo text, method text, date BLOB , amount double)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS account");
        onCreate(db);
    }

    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> arraylist = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            arraylist.add(res.getString(res.getColumnIndex(ACCOUNTS_COLUMN_NO)));
            res.moveToNext();
        }
        return arraylist;
    }

    @Override
    public List<Account> getAccountsList() {
        ArrayList<Account> arraylist = new ArrayList<Account>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String accountNo = res.getString(res.getColumnIndex(ACCOUNTS_COLUMN_NO));
            String bankName = res.getString(res.getColumnIndex(ACCOUNTS_COLUMN_BANK_NAME));
            String accountHolderName = res.getString(res.getColumnIndex(ACCOUNTS_COLUMN_HOLDER_NAME));
            Double balance = res.getDouble(res.getColumnIndex(ACCOUNTS_COLUMN_BALANCE));

            arraylist.add(new Account(accountNo,bankName,accountHolderName,balance));
            res.moveToNext();
        }
        return arraylist;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account where id="+accountNo+"", null );

        String accountno = res.getString(res.getColumnIndex(ACCOUNTS_COLUMN_NO));
        String bankName = res.getString(res.getColumnIndex(ACCOUNTS_COLUMN_BANK_NAME));
        String accountHolderName = res.getString(res.getColumnIndex(ACCOUNTS_COLUMN_HOLDER_NAME));
        Double balance = res.getDouble(res.getColumnIndex(ACCOUNTS_COLUMN_BALANCE));
        Account account=new Account(accountno,bankName,accountHolderName,balance);
        return account ;

    }

    @Override
    public void addAccount(Account account) {
        String accountNo = account.getAccountNo();
        String bankName = account.getBankName();
        String holderName = account.getAccountHolderName();
        Double balance = account.getBalance();


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", accountNo);
        contentValues.put("bankName", bankName);
        contentValues.put("accountHolderName", holderName);
        contentValues.put("balance", balance);

        db.insert("account", null, contentValues);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("account",
                "accountNo = ? ",
                new String[] { accountNo});
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
// Not recommended
    }
}
