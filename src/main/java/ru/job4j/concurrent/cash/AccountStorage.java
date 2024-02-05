package ru.job4j.concurrent.cash;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Optional;

@ThreadSafe
public class AccountStorage {
    @GuardedBy("this")
    private final HashMap<Integer, Account> accounts = new HashMap<>();

    public synchronized boolean add(Account account) {
        return accounts.putIfAbsent(account.id(), account) == null;
    }


    public synchronized boolean update(Account account) {
        return accounts.replace(account.id(), new Account(account.id(), account.amount())) == null;
    }

    public synchronized void delete(int id) {
        getById(id).ifPresentOrElse(account -> accounts.remove(id),
                () -> System.out.println("Account with id " + id + " not found"));
    }

    public synchronized Optional<Account> getById(int id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public synchronized boolean transfer(int fromId, int toId, int amount) {
        boolean rsl = false;
        Optional<Account> fromAccount = getById(fromId);
        Optional<Account> toAccount = getById(toId);

        if (fromAccount.isPresent() && toAccount.isPresent()) {
            Account from = fromAccount.get();
            Account to = toAccount.get();

            if (from.amount() >= amount) {
                update(new Account(from.id(), from.amount() - amount));
                update(new Account(to.id(), to.amount() + amount));
                rsl = true;
            }
        }
        return rsl;
    }
}
