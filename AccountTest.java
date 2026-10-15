/** Plain-Java tests (no libraries). Run: see README. */
public class AccountTest {
    private static int failures = 0;

    private static void check(String name, boolean condition) {
        System.out.println((condition ? "PASS  " : "FAIL  ") + name);
        if (!condition) failures++;
    }

    public static void main(String[] args) throws Exception {
        Account a = new Account("Test", 1000);

        a.deposit(500);
        check("deposit increases balance", a.getBalance() == 1500);

        a.withdraw(200);
        check("withdraw decreases balance", a.getBalance() == 1300);

        try { a.deposit(0); check("zero deposit rejected", false); }
        catch (InvalidAmountException e) { check("zero deposit rejected", true); }

        try { a.deposit(-5); check("negative deposit rejected", false); }
        catch (InvalidAmountException e) { check("negative deposit rejected", true); }

        try { a.withdraw(15000); check("overdraw rejected", false); }
        catch (InsufficientFundsException e) { check("overdraw rejected", true); }

        try { a.withdraw(25000); check("over-limit withdrawal rejected", false); }
        catch (InvalidAmountException e) { check("over-limit withdrawal rejected", true); }

        check("balance unchanged after failed attempts", a.getBalance() == 1300);

        System.out.println(failures == 0 ? "\nAll tests passed." : "\n" + failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
