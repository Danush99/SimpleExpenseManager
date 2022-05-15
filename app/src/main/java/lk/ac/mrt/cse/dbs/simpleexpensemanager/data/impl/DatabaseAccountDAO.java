package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class DatabaseAccountDAO implements AccountDAO {
    private DatabaseConnector dbc;
    public DatabaseAccountDAO(DatabaseConnector dbh) {
        this.dbc = dbh;
    }
    @Override
    public List<String> getAccountNumbersList() {
        List<String> resultAccounts = new ArrayList<>();
        List<Account> allAccount = dbc.getAllAccount();

        for (Account var : allAccount)
        {
            resultAccounts.add(var.getAccountNo());
        }
        return resultAccounts;
    }

    @Override
    public List<Account> getAccountsList() {
        return dbc.getAllAccount();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return dbc.getAccountFromNo(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        dbc.addAccount_db(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        dbc.deleteOneAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        amount = Math.round(amount * 100.0) / 100.0;
        float value = 0;


        if (expenseType == ExpenseType.EXPENSE){
            value -= amount;
        }
        else if (expenseType == ExpenseType.INCOME){
            value += amount;
        }
        dbc.updateAccountBalance(accountNo, value);
    }
}
