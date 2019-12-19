package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;



public class PersistentMemoryTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {

    public static final String DATABASE_NAME = "170417B";
    public static final String TRANSACTION_COLUMN_NO = "accountNo";
    public static final String TRANSACTION_COLUMN_DATE = "date";
    public static final String TRANSACTION_COLUMN_METHOD = "method";
    public static final String TRANSACTION_COLUMN_AMOUNT = "amount";


    private List<Transaction> transactions;

    public PersistentMemoryTransactionDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
        transactions=new LinkedList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
// already created in account type
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tbltrans");
        onCreate(db);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);

        Date dates = transaction.getDate();

        byte[] byteDate = dates.toString().getBytes();
        ExpenseType methods = transaction.getExpenseType();
        String strMethod = methods.toString();

        Double amounts = transaction.getAmount();

        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());



        Log.d("Date",formattedDate);



        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", accountNo);
        contentValues.put("amount", amounts);
        contentValues.put("method",strMethod);
        contentValues.put("date", byteDate);


        db.insert("tbltrans", null, contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        transactions.clear();
        Log.d("creation","starting");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( " select * from tbltrans", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){

            String accountNo = res.getString(res.getColumnIndex(TRANSACTION_COLUMN_NO));
            Double amount = res.getDouble(res.getColumnIndex(TRANSACTION_COLUMN_AMOUNT));
            String transMethod = res.getString(res.getColumnIndex(TRANSACTION_COLUMN_METHOD));

            ExpenseType type = ExpenseType.valueOf(transMethod);
            byte[] date = res.getBlob(res.getColumnIndex(TRANSACTION_COLUMN_DATE));


            String str = new String(date, StandardCharsets.UTF_8);
            Log.d("loadedDate",str);

            Date finalDate;
            try {


                SimpleDateFormat inputFormat = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'z", Locale.ENGLISH);
                finalDate = inputFormat.parse(str);
                transactions.add(new Transaction(finalDate,accountNo,type,amount));
                Log.d("creation","success");
            }catch (java.text.ParseException e){
                Log.d("creation","failed");
                Calendar cal = Calendar.getInstance();

                finalDate = cal.getTime();
                transactions.add(new Transaction(finalDate,accountNo,type,amount));

            }


            res.moveToNext();
        }
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
