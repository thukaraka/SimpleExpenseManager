package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMenoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.MainActivity;


public class PersistentMemoryExpenseManager extends ExpenseManager {
    @Override
    public void setup() throws ExpenseManagerException {
        TransactionDAO ptransDAO = new PersistentMemoryTransactionDAO(MainActivity.getContext());
        setTransactionsDAO(ptransDAO);

        AccountDAO persistentMemoryAccountDAO = new PersistentMenoryAccountDAO(MainActivity.getContext());
        setAccountsDAO(persistentMemoryAccountDAO);
    }
}
