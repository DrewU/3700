public class Sieve {

    public static void main(String args[]) {
        int n = 10000000;
        Long before = System.currentTimeMillis();
        Sieve mySieve = new Sieve();
        mySieve.sieve(n);
        System.out.println("\nTotal time: " + (System.currentTimeMillis() - before));
    }

    void sieve(int n) {
        boolean primeNumbers[] = new boolean[n + 1];
        for (int i = 0; i < n; i++) {
            primeNumbers[i] = true;
        }

        for (int p = 2; p * p <= n; p++) {
            if (primeNumbers[p] == true) {
                for (int i = p * p; i <= n; i += p) {
                    primeNumbers[i] = false;
                }
            }
        }

        for (int i = 2; i <= n; i++) {
            if (primeNumbers[i] == true) {
                System.out.println(i + " ");
            }
        }
    }
}

