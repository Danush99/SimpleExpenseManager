package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;


import android.content.Context;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DatabaseTransactionDAO implements TransactionDAO {
    private DatabaseConnector dbc;

    public DatabaseTransactionDAO(DatabaseConnector dbh) {
        this.dbc = dbh;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        //transactions.add(transaction);
        boolean success = dbc.addTransaction_db(transaction);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return dbc.getAllLog();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = this.getAllTransactionLogs();
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
